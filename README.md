Class Load Agent 
=======
This agent tracks down the stacktrace where a class loaded and tried to find the (first) reason which caused the class to load. 


Motivation
==========
While -verbose:class shows every class loaded 

Running class-load-agent 
===========

Building the project will generate a java agent. This agent can be added to your project with the following command line argument :
`-javaagent:target/class-load-agent-1.0.0.jar=log=<csv-file>,logStackTrace=<true/false>`
The output will be written to the csv file 
If you want to log the full stacktrace set logStackTrace to true 




