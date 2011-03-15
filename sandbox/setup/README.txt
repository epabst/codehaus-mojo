Requirements
------------

This plugin should be used by Maven 3.0+ projects.
Main reason is that the (user and global) settings file could not be accessed with the maven-2 implementation.
Due to MNG-4384 the settings-security handling is not stable with maven-2.
The scm-settings could work with maven-2.

To build, Maven 3.0+ is also required. One reason is that it is required by the integration tests for the plugin.
