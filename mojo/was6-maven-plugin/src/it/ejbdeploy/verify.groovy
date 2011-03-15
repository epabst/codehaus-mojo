def deployedFile = new File(basedir, 'target/was6-maven-plugin/ejbdeploy-it-test-deployed.jar')
assert deployedFile.exists()

def genSource = new File(basedir, 'target/generated-sources/was6-maven-plugin/org/codehaus/mojo/was6/it/EJSRemoteStatelessTestBean_ce2f1a85.java')
assert genSource.exists()