def samplePDF = new File(basedir, "target/sample.pdf")
assert samplePDF.exists()
assert samplePDF.length() > 0

def sampleBBL = new File(basedir, "target/latex/sample/sample.bbl")
assert sampleBBL.exists()
assert sampleBBL.length() > 0

def buildDirSvn = new File(basedir, "target/latex/sample/.svn");
assert !buildDirSvn.exists()