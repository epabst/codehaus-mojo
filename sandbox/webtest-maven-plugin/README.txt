webtest-maven-plugin
========================================================================================================================

The webtest-maven-plugin allows you to run Canoo WebTest within yout Maven build.


Installing the plugin
------------------------------------------------------------------------------------------------------------------------

The proper way of installing the plugin is to pick it from a plugin repository - since this project is still 
free-floating this can't be done so the following approach is recommended

+) download and unpack the project distribution
+) run "mvn install" to install your plugin locally
+) run "mvn site" to generate the plugin documentation
+) go to "./src/test/it1" and run "mvn site" to ensure that the plugin is properly installed

