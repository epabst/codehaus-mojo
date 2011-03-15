// should not exist since easypo.xsd wasn't specified in sourceSchema
def file = new File(basedir, 'mxmlbeans-21-test/target/classes/org/openuri/easypo/Customer.class')
assert !file.exists()

// should exist since it exists in datetime.xsd, which was specified in sourceSchema
file = new File(basedir, 'mxmlbeans-21-test/target/classes/org/apache/xmlbeans/samples/datetime/DatetimeDocument.class')
assert file.exists()
return true
