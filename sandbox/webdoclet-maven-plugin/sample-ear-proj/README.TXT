BUILDING
========

This app doesn't do anything other than show that it is possible to build
an ear and deploy it to jboss with the various xdoclet tags. First ensure
you have an instance of JBoss running and then do the following:

> cd sample-ear-proj
> mvn clean install
> cd trading-app
> mvn jboss:deploy

You may get complaints such as missing server id and if you do then deal
with them - for example adding a server entry in settings.xml for jboss.

Then hopefully jboss should report success.
Then go to the following url

http://agwilliams1000.dyndns.org:8080/trading/trade

and there should be some text or something that verifies everything is ok.
