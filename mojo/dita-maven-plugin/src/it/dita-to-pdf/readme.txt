Prerequisite:

   DITA_HOME env points to a DITA_OT mininum 1.5+ directory
   ANT_HOME env points to a ANT 1.7+ directory
   
   
To run with custom PDF skin

  
   mvn install
   

To run with built-in PDF skin

  
   mvn install -Dcustomization.dir=
   
   
   
Output is at target/dita/out/taskbook-sample.pdf




