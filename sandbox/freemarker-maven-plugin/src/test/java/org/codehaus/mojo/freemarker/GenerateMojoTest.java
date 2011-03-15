/*
The MIT License

Copyright (c) 2006, The Codehaus http://www.codehaus.org/

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.codehaus.mojo.freemarker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import junit.framework.Assert;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.StringOutputStream;

public class GenerateMojoTest extends AbstractMojoTestCase
{
	protected void setUp() throws Exception
	{
		// required for mojo lookups to work
		super.setUp();
	}

    /**
     * Utility function to look up filenames of files in classpath.
     * 
     * Class package name is prepended to filename.
     */
    public static String getFilename(Class c, String filename)
    {
        String packageName = c.getPackage().getName();

        String packagePath = packageName.replace('.', '/') + "/";

        String fn = getBasedir() + "/src/test/resources/" + packagePath + filename;

        return fn;
    }
	
	/**
	 * tests the proper discovery and configuration of the mojo
	 * 
	 * @throws Exception
	 */
	public void testFreeMarkerTestEnvironment() throws Exception
	{
		File testPom = new File(GenerateMojoTest.getFilename(this.getClass(), "pom.xml")); 

		GenerateMojo mojo = (GenerateMojo) this.lookupMojo("generate", testPom);

		Assert.assertNotNull(mojo);
	}
	
	/**
	 * tests properties loader
	 * 
	 * @throws Exception
     */
	public void testOutputToStdout() throws Exception
	{
		File testPom = new File(GenerateMojoTest.getFilename(this.getClass(), "test-stdout-pom.xml")); 

		GenerateMojo mojo = (GenerateMojo) this.lookupMojo("generate", testPom);

		StringOutputStream sos = new StringOutputStream();
		System.setOut(new PrintStream(sos, true));
		
		mojo.execute();
		
		System.setOut(System.out);
		
		String[] result = sos.toString().split("(?:\r\n)|(?:\n)");

        // lines prior to the last two contain logging outout, i.e. "[INFO]..."
        assertEquals("johndoe", result[result.length-2].trim());
        assertEquals("1.5.0_06", result[result.length-1].trim());
	}
	 
	/**
	 * Tests properties loader
	 * 
	 * @throws Exception
	 */
	public void testPropertiesLoader() throws Exception
	{
		File testPom = new File(GenerateMojoTest.getFilename(this.getClass(), "test-properties-pom.xml")); 
	
		GenerateMojo mojo = (GenerateMojo) this.lookupMojo("generate", testPom);
	
		mojo.execute();

		BufferedReader result = getBufferedReader("target/test/test-properties.out");		
		
		assertEquals("johndoe", result.readLine().trim());
		assertEquals("1.5.0_06", result.readLine().trim());
		
		result.close();
	}

    /**
     * Tests helper data model
     * 
     * @throws Exception
     */
    public void testDateDataModel() throws Exception
    {
        File testPom = new File(GenerateMojoTest.getFilename(this.getClass(), "test-date-pom.xml")); 
    
        GenerateMojo mojo = (GenerateMojo) this.lookupMojo("generate", testPom);
    
        mojo.execute();

        BufferedReader result = getBufferedReader("target/test/test-date.out");       
        
        // wed sep 06 2006 21:28:00 UTC
        String date = result.readLine().trim(); 
        assertTrue(date.matches("\\w\\w\\w?\\s\\w\\w\\w\\s\\d\\d\\s\\d\\d\\d\\d\\s\\d\\d:\\d\\d:\\d\\d\\sUTC"));
        
        result.close();
    }    

    /**
     * Tests helper data model
     * 
     * @throws Exception
     */
    public void testObjectConstructorDataModel() throws Exception
    {
        File testPom = new File(GenerateMojoTest.getFilename(this.getClass(), "test-object-constructor-pom.xml")); 
    
        GenerateMojo mojo = (GenerateMojo) this.lookupMojo("generate", testPom);
    
        mojo.execute();

        BufferedReader result = getBufferedReader("target/test/test-object-constructor.out");       
        
        // wed sep 06 2006 21:28:00 UTC
        String s = result.readLine().trim(); 
        assertEquals("string created using object constructor", s);
        
        result.close();
    }    
    
	/**
	 * Tests XML loader
	 * 
	 * @throws Exception
	 */
	public void testXMLLoader() throws Exception
	{
		File testPom = new File(GenerateMojoTest.getFilename(this.getClass(), "test-xml-pom.xml")); 
	
		GenerateMojo mojo = (GenerateMojo) this.lookupMojo("generate", testPom);
	
		mojo.execute();

		BufferedReader result = getBufferedReader("target/test/test-xml.out");		
		
		assertEquals("attribute data", result.readLine().trim());
		assertEquals("element data", result.readLine().trim());
        
		result.close();
	}

    /**
     * Tests XML loader with a xml file that use xml namespace (xmlns)
     * 
     * @throws Exception
     */
    public void testXMLNSLoader() throws Exception
    {
        File testPom = new File(GenerateMojoTest.getFilename(this.getClass(), "test-xmlns-pom.xml")); 
    
        GenerateMojo mojo = (GenerateMojo) this.lookupMojo("generate", testPom);
    
        mojo.execute();

        BufferedReader result = getBufferedReader("target/test/test-xmlns.out");      
        
        assertEquals("pom for testing xmlns data model loader", result.readLine().trim());
        assertEquals("http://maven.apache.org/POM/4.0.0", result.readLine().trim());
        assertEquals("freemarker-maven-plugin", result.readLine().trim());
        
        result.close();
    }
    
	private BufferedReader getBufferedReader(String filename) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		InputStreamReader converter = new InputStreamReader(fis);
		BufferedReader result = new BufferedReader(converter);
		return result;
	}
}