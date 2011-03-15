/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codehaus.mojo.scmchangelog;

import java.io.File;

import junit.framework.TestCase;


/**
 *
 * @author hugonnem
 */
public class ReportHelperTest extends TestCase {
    public ReportHelperTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of copyImage method, of class ReportHelper.
     * @throws java.lang.Exception
     */
    public void testCopyImage() throws Exception {
        String resourcePath = "images/add.gif";
        String outputDir = System.getProperty("java.io.tmpdir");
        ReportHelper.copyImage(resourcePath, outputDir);

        File copy = new File(outputDir + System.getProperty("file.separator") +
                "images" + System.getProperty("file.separator") + "add.gif");
        assertTrue(copy.exists());
        copy.delete();
    }

    /**
     * Test of getImageName method, of class ReportHelper.
     */
    public void testGetImageName() {
        String resourcePath = "images/toto.gif";
        String expResult = "toto.gif";
        String result = ReportHelper.getImageName(resourcePath);
        assertEquals(expResult, result);
        resourcePath = "toto.gif";
        result = ReportHelper.getImageName(resourcePath);
        assertEquals(expResult, result);
        resourcePath = "/var/images/toto.gif";
        result = ReportHelper.getImageName(resourcePath);
        assertEquals(expResult, result);
    }
}
