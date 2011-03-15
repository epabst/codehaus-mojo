package org.codehaus.mojo.springws;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * Unit test for simple SpringWsMojo.
 */
public class SpringWsMojoTest
    extends AbstractMojoTestCase
{

    private Mojo springWsMojo;
    private File targetDirectory;
    private String suffix;
    
    protected void setUp()
        throws Exception
    {
        super.setUp();
        springWsMojo = lookupMojo( "springws", "src/test/resources/test1/plugin-config.xml" );
        targetDirectory = new File( "target/testdir" );
        suffix = ".wsdl";
    }

    public void testSpringWsMojo()
        throws Exception
    {   
        String[] contextLocations = {
            "classpath:test-context1.xml"
        };
        
        setVariableValueToObject( springWsMojo, "contextLocations", contextLocations );
        setVariableValueToObject( springWsMojo, "targetDirectory", targetDirectory );
        setVariableValueToObject( springWsMojo, "suffix", suffix );
        
        springWsMojo.execute();
        
        File expectedWsdlFile = new File( targetDirectory, "testService" + suffix );
        assertTrue( "WSDL file was not generated", expectedWsdlFile.exists() );
    }
}
