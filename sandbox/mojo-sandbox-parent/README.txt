Releasing the mojo-sandbox-parent
 
 This parent overrules the distributionManagement repository to prevent accidental releases.
 To be able to release this sandbox-parent we need to overrule this by hand.
 
 First the release-plugin has to prepare the project to perform it's task.
   mvn release:perform -Dgoals=validate
 This will generate the target/checkout-directory we need next.
 
 Now execute from the target/checkout-directory the following line to complete the release:
   mvn deploy \
   -DaltDeploymentRepository=codehaus-nexus-staging::default::https://nexus.codehaus.org/service/local/staging/deploy/maven2/ \
   -Pmojo-release
   