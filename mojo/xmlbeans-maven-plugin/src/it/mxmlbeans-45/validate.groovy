// should exist since sourceSchema was not specified
def file = new File(basedir, 'mxmlbeans-45-test/target/classes/org/openuri/easypo/Customer.class')
assert file.exists()

// should exist since sourceSchema was not specified
file = new File(basedir, 'mxmlbeans-45-test/target/classes/org/apache/xmlbeans/samples/datetime/DatetimeDocument.class')
assert file.exists()
return true
