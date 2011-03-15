package org.codehaus.mojos.slimdog;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jzonic.webtester.WebTestFileReader;
import org.jzonic.webtester.WebTestResult;
import org.jzonic.webtester.writer.WebTestResultFileWriter;
import org.jzonic.webtester.writer.WebTestResultWriter;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Run a [set of] slimdog-WebTester test(s).
 * 
 * @goal test
 * @phase integration-test
 */
public class WebTesterMojo extends AbstractMojo
{
    
    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String[] DEFAULT_INCLUDES = {"**/**"};

    /** The test-resources to scan for tests.
     * 
     * @parameter expression="${project.build.testResources}"
     */
    private List testResources;
    
    /** A single test file to be run, assumed to be in the testResources somewhere.
     * 
     * @parameter
     */
    private String testFile;
    
    /**
     * Base directory where all reports are written to.
     *
     * @parameter expression="${project.build.directory}/slimdog-reports"
     * @required
     */
    private String reportsDirectory;
    
    public void execute()
        throws MojoExecutionException
    {
        Map testEntries = null;
        
        if ( testFile != null ) {
            testEntries = getTestEntries( testFile );
        }
        else if ( testResources != null && !testResources.isEmpty() )
        {
            testEntries = getTestEntries( null );
        }
        else
        {
            getLog().info( "No tests found." );
            return;
        }
        
        // this will NOT be accessed if we don't have a testFile or testResources.
        // see above if-then-else logic...
        for ( Iterator testEntryIterator = testEntries.entrySet().iterator(); testEntryIterator.hasNext(); )
        {
            Map.Entry testEntry = (Map.Entry) testEntryIterator.next();
            
            File testFile = (File) testEntry.getKey();
            String testName = (String) testEntry.getValue();
            
            runTest( testFile, testName );
        }
    }

    private void runTest( File testFile, String reportFile )
    {
        getLog().info( "Running test: " + testFile );
        
        WebTestFileReader reader = new WebTestFileReader();
        
        WebTestResult result = reader.parseFile( testFile.getAbsolutePath() );
        
        getLog().info( "Parsed test: " + testFile );
        
        File outputFile = new File( reportsDirectory, reportFile + ".txt" );
        
        File outputDir = outputFile.getParentFile();
        
        if( !outputDir.exists() )
        {
            outputDir.mkdirs();
        }
        
        WebTestResultWriter writer = new WebTestResultFileWriter( outputFile.getAbsolutePath() );
        
        getLog().info( "Generating resulting output for test: " + testFile );
        
        writer.generateResult(result);
        
        getLog().info( "Wrote output to: " + outputFile );
    }

    private Map getTestEntries( String singleFileInclude )
    {
        Map resourceEntries = new TreeMap();

        // this label marks the outer loop so we can break all the way out if we're only looking for
        // a single test file, and we find it before the last of the resources are processed.
        allScanning:
        for ( Iterator i = testResources.iterator(); i.hasNext(); )
        {
            Resource resource = (Resource) i.next();

            String targetPath = resource.getTargetPath();

            File resourceDirectory = new File( resource.getDirectory() );

            if ( !resourceDirectory.exists() )
            {
                continue;
            }

            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir( resource.getDirectory() );
            
            if ( singleFileInclude != null )
            {
                scanner.setIncludes( new String[] { singleFileInclude } );
            }
            else
            {
                scanner.addDefaultExcludes();
                
                if ( resource.getIncludes() != null && !resource.getIncludes().isEmpty() )
                {
                    scanner.setIncludes( (String[]) resource.getIncludes().toArray( EMPTY_STRING_ARRAY ) );
                }
                else
                {
                    scanner.setIncludes( DEFAULT_INCLUDES );
                }
                
                if ( resource.getExcludes() != null && !resource.getExcludes().isEmpty() )
                {
                    scanner.setExcludes( (String[]) resource.getExcludes().toArray( EMPTY_STRING_ARRAY ) );
                }
            }

            scanner.scan();

            List includedFiles = Arrays.asList( scanner.getIncludedFiles() );
            for ( Iterator j = includedFiles.iterator(); j.hasNext(); )
            {
                String name = (String) j.next();

                String entryName = name;

                if ( targetPath != null )
                {
                    entryName = targetPath + "/" + name;
                }

                resourceEntries.put( new File( resource.getDirectory(), name ), entryName );
                
                // if we're only running a single test, then we can break all the way out
                // as soon as we find a single matching test file.
                if ( singleFileInclude != null )
                {
                    break allScanning;
                }
            }
        }

        return resourceEntries;
    }
}
