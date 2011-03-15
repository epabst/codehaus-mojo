def generatedSource = new File( basedir, 'target/generated-sources/axistools/wsdl2java/com/foo/bar/one/two' )
assert generatedSource.exists();
return true;