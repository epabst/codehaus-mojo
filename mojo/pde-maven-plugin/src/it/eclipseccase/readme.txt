This maven project fetches elipse clearcase source from 2 cvs reposioties

   o clearcase-java.cvs.sourceforge.net
   o eclipse-ccase.cvs.sourceforge.net

Copy a pre configured pom file into target/checkout/features/net.sourceforge.eclipseccase.feature directory and
finally invoke maven to build the entire feature.

However before the source can be fetched, you need to issue the following commands
to setup cvs repositories.

   o cvs -d:pserver:anonymous@clearcase-java.cvs.sourceforge.net:/cvsroot/clearcase-java login 
   o cvs -d:pserver:anonymous@clearcase-java.cvs.sourceforge.net:/cvsroot/clearcase-java login 

Hit enter when asked for password


   
