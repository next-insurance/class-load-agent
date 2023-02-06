package com.nextinsurance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class ClassLoadAgent {
    private static String logFileName;
    private static boolean fullStacktrace = false;
    private static BufferedWriter writer;

    public static void premain(String agentArgs, Instrumentation inst) {
        if (agentArgs != null && !agentArgs.isBlank()) {
            for (String arg : agentArgs.split(",")) {
                String[] kv = arg.split("=");
                if (kv.length == 2 && kv[0].equals("log")) {
                    System.out.println("Logging to " + kv[1]);
                    logFileName = kv[1];
                }
                if (kv.length == 2 && kv[0].equals("logStackTrace") && kv[1].equals("true")) {
                    System.out.println("Full stack trace " + kv[1]);
                    fullStacktrace = true;
                }
            }
        }
        if (logFileName == null) {
            writer = new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
            try {
                writer = new BufferedWriter(new FileWriter(logFileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        inst.addTransformer(new ClassFileTransformer() {
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer
            ) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

                try {
                    writer.write(className);


                    writer.write(",Loaded-by,");

                    int lastIndex = -1;

                    int sz = stackTrace.length;
                    for (int i = 0; i < sz; i++) {
                        String stackTraceString = stackTrace[i].toString();
                        if (stackTraceString.contains("java.base/java.lang.ClassLoader.loadClass") ||
                                stackTraceString.contains("java.base/java.lang.Class.forName") ||
                                stackTraceString.contains("java.instrument/sun.instrument.InstrumentationImpl.transformer") ||
                                stackTraceString.contains("java.instrument/sun.instrument.InstrumentationImpl.transform") ||
                                stackTraceString.contains("java.lang.Class.getDeclaredFields")
                        ) {
                            lastIndex = i;
                        }

                    }
                    writer.write(stackTrace[lastIndex + 1].toString() + ",");
                    writer.write("by," + stackTrace[lastIndex].toString());
                    writer.newLine();
                    writer.flush();
                    if (stackTrace[lastIndex + 1].toString().contains("java.lang.Thread.getStackTrace(") || fullStacktrace) {
                        writer.write("---------------");
                        writer.newLine();
                        writer.write("-------- could not locate loading class for ");
                        writer.newLine();
                        for (StackTraceElement ste : stackTrace) {
                            writer.write("  " + ste);
                            writer.newLine();
                        }
                        writer.write("---------------------");
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to log file" + logFileName);
                }
                return classfileBuffer;
            }
        });
    }
}
