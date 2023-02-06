Class Load Agent 
=======
This agent tracks down the stacktrace where a class loaded and tried to find the (first) reason which caused the class to load. 


Motivation
==========
While `-verbose:class` shows every class loaded one cannot understand why a class was loaded. 
This project tried to shed light on this by installing a javaagent which listens to class loads 
and tries to locate the initiator of the load.    

Running class-load-agent 
===========

Building the project with `mvn package` will generate a java agent. This agent can be added to your project with the following command line argument :

`-javaagent:target/class-load-agent-1.0.0.jar=log=<csv-file>,logStackTrace=<true/false>`

The output will be written to the `<csv file>` <p>
If you want to log the full stacktrace set logStackTrace to true 




