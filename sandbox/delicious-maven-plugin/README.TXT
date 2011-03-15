About
=====

Delicious plugin is used to generate delicious bookmarks
for a generated maven site.
Additionally it can be used to export browser bookmarks
to a delicious account.



Building
=========

> cd sandbox2
> mvn clean install



Quick Start
===========

First of all add the following server to your settings.xml file
making sure you use the id 'delicious':

    <server>       
      <id>delicious</id>
      <username>david</username>
      <password>pencil</password>
    </server>      

then run the following goal:

> cd myproj
> mvn site org.codehaus.mojo:delicious-maven-plugin:index



Exporting bookmarks
===================

There is also an export goal that can be used standalone without any
project. This will scan an html page for links and add them to the
configured delicious account as in the following example:

> mvn org.codehaus.mojo:delicious-maven-plugin:export\
>  -Dserver=delicious -Durl=file:///Users/jim/bookmarks.html -Dreplace=true

Bear in mind this will take about 1.5 seconds per bookmark which means
to export 400 bookmarks will take about 10 minutes.



Notes
=====

For a typical project this will add between
50 and 100 bookmarks to the delicious account so make sure
you know how to clean up your account if you are experimenting.
Maybe create a brand new account for the sake of experimenting.

sorry about the following weasel words, but it's something
to think about:

DISCLAIMER

THIS TOOL IS PROVIDED AS IS - IT IS THE USERS REPOSIBILITY
TO MAINTAIN GOOD RELATIONS WITH DELICIOUS
