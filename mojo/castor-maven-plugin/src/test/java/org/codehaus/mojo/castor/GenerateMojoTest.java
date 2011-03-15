package org.codehaus.mojo.castor;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusTestCase;
import org.exolab.castor.util.Version;

public class GenerateMojoTest
    extends PlexusTestCase
{

    private static final String TIMESTAMP_DIR = getBasedir() + "/target/test/resources/timestamp";

    private static final String GENERATED_DIR = getBasedir() + "/target/generated-sources/castor";

    private static final String GENERATED_RESOURCES_DIR = getBasedir() + "/target/generated-resources/castor";

    private static final String MAPPING_XSD = getBasedir() + "/src/test/resources/mapping.xml";

    GenerateMojo generateMojo;

    private File aClassFile;

    private File aDescriptorClassFile;

    private double castorVersion;

    public void setUp()
        throws IOException
    {
//    	FileUtils.deleteDirectory( new File( getBasedir() + "/target/test" ) );
        FileUtils.deleteDirectory( new File( GENERATED_DIR ) );
        FileUtils.deleteDirectory( new File( GENERATED_RESOURCES_DIR ) );
        FileUtils.deleteDirectory( new File( TIMESTAMP_DIR ) );

        aClassFile = new File( GENERATED_DIR, "org/codehaus/mojo/castor/A.java" );
        aDescriptorClassFile = new File( GENERATED_DIR, "org/codehaus/mojo/castor/descriptors/ADescriptor.java" );

        this.generateMojo = new GenerateMojo();
        this.generateMojo.setProject( new MavenProject( new Model() ) );
        this.generateMojo.setDest( new File ( GENERATED_DIR ) );
        this.generateMojo.setResourceDestination( new File ( GENERATED_RESOURCES_DIR ) );
        this.generateMojo.setTstamp( new File ( TIMESTAMP_DIR ) );
        
        this.castorVersion = getCastorVersion();
    }

    public void tearDown()
        throws IOException
    {
        generateMojo = null;
    }

    public void testExecute()
        throws MojoExecutionException
    {

        generateMojo.setPackaging( "org.codehaus.mojo.castor" );
        generateMojo.setSchema( new File (MAPPING_XSD ) );
        generateMojo.execute();

        assertTrue( aClassFile.exists() );
        assertTrue( aDescriptorClassFile.exists() );

    }
    
    // MCASTOR-5 issue
    public void testForGetContent() throws Exception {
    	
        generateMojo.setSchema( new File (getPathTo( "src/test/resources/availability_report.xsd" ) ) );
        generateMojo.setProperties( new File (getPathTo( "src/test/resources/castorbuilder.properties" ) ) );
        generateMojo.setTypes("arraylist");
        generateMojo.execute();

        File generatedClass = new File( GENERATED_DIR + "/org/opennms/report/availability", "Created.java" );
		assertTrue( "Expected " + generatedClass + " to exist.", generatedClass.exists() );
		assertFileContains( generatedClass, "getContent" );
		
    }

    private void assertFileContains( File file, String string ) throws IOException {
    	
    	String contents = FileUtils.readFileToString( file, "ISO-8859-1" );
        boolean contains = (contents.indexOf(string) > -1);
    	assertTrue( "Expected " + file + " to contain string " + string, contains );
    	
	}
    

	public void testEmptyPackage()
        throws MojoExecutionException
    {

        generateMojo.setSchema( new File (getPathTo( "src/test/resources/vacuumd-configuration.xsd" ) ) );
        generateMojo.setProperties( new File ( getPathTo( "src/test/resources/castorbuilder.properties" ) ) );
        generateMojo.setTypes("arraylist");
        generateMojo.execute();

        assertTrue( new File( GENERATED_DIR, "Actions.java" ).exists() );
    }

//	public void testGenerateImportedSchemasDisabled()
//	throws MojoExecutionException
//	{
//
//	    generateMojo.setSchema( getPathTo( "src/test/resources/main.xsd" ) );
//	    generateMojo.setProperties( getPathTo( "src/test/resources/castorbuilder.properties" ) );
//	    generateMojo.setTypes("arraylist");
//	    generateMojo.setGenerateImportedSchemas(false);
//	    generateMojo.execute();
//
//	    assertTrue( new File( GENERATED_DIR, "Main.java" ).exists() );
////	    assertFalse( new File( GENERATED_DIR, "MainType.java" ).exists() );
//	}

    public void testGenerateImportedSchemasEnabled()
        throws MojoExecutionException
    {

        generateMojo.setSchema( new File ( getPathTo("src/test/resources/main.xsd") ) );
        generateMojo.setProperties( new File ( getPathTo("src/test/resources/castorbuilder.properties") ) );
        generateMojo.setTypes("arraylist");
        generateMojo.setGenerateImportedSchemas(true);
        generateMojo.execute();

        assertTrue(new File(GENERATED_DIR, "Main.java").exists());
        assertTrue(new File(GENERATED_DIR, "MainType.java").exists());
    }

    public void testGenerateWithVelocity()
    throws MojoExecutionException
    {

        generateMojo.setSchema( new File ( getPathTo("src/test/resources/main.xsd") ) );
        generateMojo.setProperties( new File ( getPathTo("src/test/resources/castorbuilder.properties") ) );
        generateMojo.setTypes("arraylist");
        generateMojo.setGenerateImportedSchemas(true);
        generateMojo.setClassGenerationMode("velocity");
        generateMojo.setPackaging("org.codehaus.mojo.castor.velocity");
        generateMojo.execute();

        assertTrue(new File(GENERATED_DIR, "org/codehaus/mojo/castor/velocity/Main.java").exists());
        assertTrue(new File(GENERATED_DIR, "org/codehaus/mojo/castor/velocity/MainType.java").exists());
    }

    public void testGenerateWithSeparateResourceDirectory()
    throws MojoExecutionException
    {

        // configure MOJO 
        generateMojo.setSchema( new File ( getPathTo("src/test/resources/main.xsd") ) );
        generateMojo.setProperties( new File ( getPathTo("src/test/resources/castorbuilder.properties") ) );
        generateMojo.setTypes("arraylist");
        generateMojo.setGenerateImportedSchemas(true);
        generateMojo.setPackaging("org.codehaus.mojo.castor.velocity");
        generateMojo.setResourceDestination( new File ( GENERATED_RESOURCES_DIR ));
        
        // execute MOJO
        generateMojo.execute();

        // test assertions
        String packagePath = "org/codehaus/mojo/castor/velocity/";
        assertTrue(new File(GENERATED_DIR, packagePath + "Main.java").exists());
        assertTrue(new File(GENERATED_DIR, packagePath + "MainType.java").exists());
        if (this.castorVersion > 1.301) {
            assertTrue(new File(GENERATED_RESOURCES_DIR, packagePath + "/.castor.cdr").exists());
        } else {
            assertFalse(new File(GENERATED_RESOURCES_DIR, packagePath + "/.castor.cdr").exists());
        }
    }
    
    private double getCastorVersion() {
        String text = Version.VERSION;
        int firstPoint = text.indexOf(".");
        double version = Double.valueOf(text.substring(0, firstPoint)); 
        String[] tokens = text.substring(firstPoint + 1).split("\\.");
        int i = 10;
        for (String token: tokens) {
            version = version + Double.valueOf(token)/i;
            i = i * 10;
        }
        return version;
    }
    
    public void testGenerateWithMappings()
    throws MojoExecutionException
    {

        generateMojo.setSchema( new File ( getPathTo("src/test/resources/main.xsd") ) );
        generateMojo.setProperties( new File ( getPathTo("src/test/resources/castorbuilder.properties") ) );
        generateMojo.setTypes( "arraylist" );
        generateMojo.setGenerateMappings( true );
        generateMojo.execute();

        assertTrue( new File ( GENERATED_DIR, "Main.java" ).exists());
        assertTrue( new File ( getBasedir() , "mapping.xml" ).exists());
        
        FileUtils.deleteQuietly( new File( "target/test-classes", "mapping.xml" ) );
    }

    private File getTimeStampFile()
    {
        return new File( TIMESTAMP_DIR, "mapping.xml" );
    }

    public void testCreateTimeStamp()
        throws MojoExecutionException
    {
        File timeStampFile = getTimeStampFile();

        generateMojo.setPackaging( "org.codehaus.mojo.castor" );
        generateMojo.setSchema( new File ( MAPPING_XSD ) );
        generateMojo.execute();
        
        assertTrue( aClassFile.exists() );
        assertTrue( aDescriptorClassFile.exists() );
        assertTrue( timeStampFile.exists() );

    }
       

    public void testCreateTimeStampFolder()
        throws MojoExecutionException
    {
        File timeStampFile = getTimeStampFile();

        generateMojo.setPackaging( "org.codehaus.mojo.castor" );
        generateMojo.setSchema( new File ( MAPPING_XSD ) );
        generateMojo.execute();

        assertTrue( aClassFile.exists() );
        assertTrue( aDescriptorClassFile.exists() );
        assertTrue( timeStampFile.exists() );

    }

//    // timestamp exist but not updated
//    public void testCreateTimeStampOld()
//        throws MojoExecutionException, IOException
//    {
//        File timeStampFile = createTimeStampWithTime( timestampOf( MAPPING_XSD ) - 1 );
//
//        generateMojo.setPackaging( "org.codehaus.mojo.castor" );
//        generateMojo.setSchema( MAPPING_XSD );
//        generateMojo.execute();
//
//        assertTrue( aClassFile.exists() );
//        assertTrue( aDescriptorClassFile.exists() );
//        assertTrue( timeStampFile.exists() );
//
//    }

    private File createTimeStampWithTime( long time )
        throws IOException
    {
        File timeStampFolder = new File( TIMESTAMP_DIR );
        File timeStampFile = getTimeStampFile();
        if ( !timeStampFolder.exists() )
        {
            timeStampFolder.mkdirs();
        }
        if ( !timeStampFile.exists() )
        {
            FileUtils.touch( timeStampFile );
            timeStampFile.setLastModified( time );
        }
        return timeStampFile;
    }

    public void testCreateTimeStampLatest()
        throws MojoExecutionException, IOException
    {
        File timeStampFile = createTimeStampWithTime( timestampOf( MAPPING_XSD ) + 1 );

        generateMojo.setPackaging( "org.codehaus.mojo.castor" );
        generateMojo.setSchema( new File ( MAPPING_XSD ) );
        generateMojo.execute();

        assertTrue( !aClassFile.exists() );
        assertTrue( !aDescriptorClassFile.exists() );
        assertTrue( timeStampFile.exists() );

    }

    private long timestampOf( String file )
    {
        File sourcefile = new File( file );
        long time = sourcefile.lastModified();
        return time;
    }

    public void testDestProperty()
    {
        generateMojo.setDest( new File ( "testString" ) );
        assertEquals( "testString", generateMojo.getDest().toString() );
    }

    public void testTStampProperty()
    {
        generateMojo.setTstamp( new File ( "testString" ) );
        assertEquals( "testString", generateMojo.getTstamp().toString() );
    }

    public void testSchemaProperty()
    {
        generateMojo.setSchema( new File ( "teststring" ) );
        assertEquals( "teststring", generateMojo.getSchema().toString() );
    }

    public void testPackagingProperty()
    {
        generateMojo.setPackaging( "teststring" );
        assertEquals( "teststring", generateMojo.getPackaging() );
    }

    public void testTypesProperty()
    {
        generateMojo.setTypes( "teststring" );
        assertEquals( "teststring", generateMojo.getTypes() );
    }

    public void testMarshalProperty()
    {
        generateMojo.setMarshal( true );
        assertTrue( generateMojo.getMarshal() );
    }

    private String getPathTo( String relativePath )
    {
        return getBasedir() + '/' + relativePath;
    }

}
