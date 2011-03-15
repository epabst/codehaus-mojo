def src1 = new File( basedir, 'target/generated-sources/sqlj/test/com/tmme/x01/TestUNICODE.java' )
def src2 = new File( basedir, 'target/generated-sources/sqlj/test/com/tmme/x01/TestUNICODE2.java' )
assert src1.exists()
assert src2.exists()

def resource1 = new File( basedir, 'target/generated-resources/sqlj/test/com/tmme/x01/TestUNICODE_SJProfile0.ser' )
def resource2 = new File( basedir, 'target/generated-resources/sqlj/test/com/tmme/x01/TestUNICODE2_SJProfile0.ser' )
assert resource1.exists()
assert resource2.exists()

