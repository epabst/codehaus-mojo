package org.codehaus.mojo.setup;

import java.io.File;
import java.io.IOException;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.aether.RepositorySystemSession;

public abstract class AbstractSetupMojoTest
    extends AbstractMojoTestCase
{
    private final String PROPERTY_MERGE = "merge";

    private final String PROPERTY_SETTINGSFILE = "templateFilename";

    private final String testWorkPath = "target/test-work";

    protected File testWorkDirectory;

    protected abstract AbstractSetupMojo getMojo();

    protected String getPropertiesFilename()
    {
        return getMojo().getPropertiesFilename();
    }

    protected String getDefaultTemplateFilename()
    {
        return getMojo().getDefaultTemplateFilename();
    }

    protected void prepareDirectoryStructure( File resourceDirectory, File workDirectory )
        throws IOException
    {
        // prepare directory-structure
        if ( workDirectory.exists() )
        {
            FileUtils.cleanDirectory( workDirectory );
        }
        FileUtils.copyDirectoryStructure( resourceDirectory, workDirectory );
    }

    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        DefaultMavenExecutionRequest mavenRequest = new DefaultMavenExecutionRequest();
        mavenRequest.setSystemProperties( System.getProperties() ).setBaseDirectory( workDirectory );
        setVariableValueToObject( getMojo(), "session", new MavenSession( this.getContainer(),
                                                                          new MavenRepositorySystemSession(),
                                                                          mavenRequest,
                                                                          new DefaultMavenExecutionResult() ) );

        setVariableValueToObject( getMojo(), "baseDirectory", workDirectory );
    }

    @Override
    protected final void setUp()
        throws Exception
    {
        super.setUp();

        testWorkDirectory = getTestFile( testWorkPath );
        testWorkDirectory.mkdirs(); // force creating all required directories

        onSetUp();
    }

    public File getWorkDirectory()
    {
        return testWorkDirectory;
    }

    protected abstract File getDirectoryDefaultTargetFile();

    protected abstract File getDirectoryMissingTargetFile();

    protected abstract File getDirectoryMinimumTargetFile();

    protected abstract String getTestPrefix();

    protected abstract String getTemplateBase();

    protected String getCustomTemplateFileArgument()
    {
        return getTemplateBase() + "-custom.xml";
    }

    protected String getPropertyFileFilteredTemplateFileArgument()
    {
        return getTemplateBase() + "-filefilter.xml";
    }

    protected String getSystemPropertyFilteredTemplateFileArgument()
    {
        return getTemplateBase() + "-systemfilter.xml";
    }

    protected abstract File getTargetFile();

    protected void onSetUp()
        throws Exception
    {
    }

    /**
     * Just to be sure the plugin is available
     * 
     * @throws Exception
     */
    public void testAvailable()
        throws Exception
    {
        assertNotNull( getMojo() );
    }

    /**
     * Testing conditions:
     * <ul>
     * <li>Default template available</li>
     * <li>Target file doesn't exist</li>
     * <li>No merge-type defined</li>
     * </ul>
     * Expected situation:
     * <ul>
     * <li>A prototype file is copied to the baseDirectory</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testNoTargetDefaultTemplateNoMerge()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-noTrgtDefTmpltNoMrg" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );

        assertFalse( getTargetFile().exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        // execute the mojo
        getMojo().execute();

        assertTrue( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        validateAsNoSettings();
    }

    /**
     * Testing conditions:
     * <ul>
     * <li>Target file already exists</li>
     * <li>No template defined</li>
     * <li>No merge-type defined</li>
     * </ul>
     * Expected situation:
     * <ul>
     * <li>Target file is copied to the baseDirectory</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testDefaultTargetNoTemplateNoMerge()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-noTmpltDefTrgtNoMrg" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );

        assertTrue( getTargetFile().exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        getMojo().execute();

        assertTrue( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );
    }

    /**
     * Testing conditions:
     * <ul>
     * <li>Target file already exists</li>
     * <li>No template defined</li>
     * <li>merge-type set to <strong>none</strong></li>
     * </ul>
     * Expected situation:
     * <ul>
     * <li>Target file is copied to the baseDirectory</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testDefaultTargetNoTemplateMergeNONE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTrgtNoTmpltNone" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "none" );

        assertTrue( getTargetFile().exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        // execute the mojo
        try
        {
            getMojo().execute();
            fail( "Mergetype was set while there's no template available." );
        }
        catch ( MojoFailureException mfe )
        {
        }
    }

    /**
     * Testing conditions:
     * <ul>
     * <li>Target file already exists</li>
     * <li>No template defined or available in baseDirectory</li>
     * <li>Merge-type set to <strong>overwrite</strong></li>
     * </ul>
     * Expected situation:
     * <ul>
     * <li>Exception: merge was set, but there's no template</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testDefaultTargetNoTemplateMergeOVERWRITE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-noTmpltDefTrgtOverwrite" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "overwrite" );

        assertTrue( getTargetFile().exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        // execute the mojo
        try
        {
            getMojo().execute();
            fail( "Mergetype was set while there's no template available." );
        }
        catch ( MojoFailureException mfe )
        {
        }
    }

    /**
     * Testing conditions:
     * <ul>
     * <li>Target file already exists</li>
     * <li>No template defined or available in baseDirectory</li>
     * <li>Merge-type set to <strong>overwrite</strong></li>
     * </ul>
     * Expected situation:
     * <ul>
     * <li>Exception: merge was set, but there's no template</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testDefaultTargetNoTemplateMergeEXPAND()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTrgtNoTmpltExpand" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "expand" );

        assertTrue( getTargetFile().exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        // execute the mojo
        try
        {
            getMojo().execute();
            fail( "Mergetype was set while there's no template available." );
        }
        catch ( MojoFailureException mfe )
        {
        }
    }

    /**
     * Testing conditions:
     * <ul>
     * <li>Target file already exists</li>
     * <li>No template defined or available in baseDirectory</li>
     * <li>Merge-type set to <strong>update</strong></li>
     * </ul>
     * Expected situation:
     * <ul>
     * <li>Exception: merge was set, but there's no template</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testDefaultTargetNoTemplateMergeUPDATE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-DefTrgtNoTmpltUpdate" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "update" );

        assertTrue( getTargetFile().exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        // execute the mojo
        try
        {
            getMojo().execute();
            fail( "Mergetype was set while there's no template available." );
        }
        catch ( MojoFailureException mfe )
        {
        }
    }

    /**
     * Test to see if a template will be filtered with the values of the property file
     * 
     * @throws Exception
     */
    public void testPropertyFileFilter()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-filter" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );

        setVariableValueToObject( getMojo(), PROPERTY_SETTINGSFILE, getPropertyFileFilteredTemplateFileArgument() );

        File sourceFile = new File( "src/test/resources/basefolder", getPropertyFileFilteredTemplateFileArgument() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );
        File propertyFile = new File( "src/test/resources/basefolder", getPropertiesFilename() );
        FileUtils.copyFileToDirectory( propertyFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsFilteredTemplate();
    }

    /**
     * Testing conditions:
     * <ul>
     * <li>Target file already exists</li>
     * <li>The default template is available in baseDirectory</li>
     * <li>No merge-type defined</li>
     * </ul>
     * Expected situation:
     * <ul>
     * <li>Target file is copied to the baseDirectory</li>
     * </ul>
     * 
     * @throws Exception
     */
    public void testDefaultTargetDefaultTemplateNoMerge()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTrgtDefTmpltNoMrg" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );

        File targetFile = getTargetFile();
        assertTrue( targetFile.getAbsolutePath() + " should not exist, but does", targetFile.exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        // execute the mojo
        getMojo().execute();

        validateAsDefaultSettings();
    }

    public void testDefaultTargetDefaultTemplateMergeNONE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltDefTrgtNone" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "none" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsDefaultSettings();
    }

    public void testDefaultTargetDefaultTemplateMergeOVERWRITE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltDefTrgtOverwrite" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "overwrite" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsDefaultTemplate();
    }

    public void testDefaultTargetDefaultTemplateMergeEXPAND()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltDefTrgtExpand" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "expand" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsDefaultSettings();
        ;
    }

    public void testDefaultTargetDefaultTemplateMergeUPDATE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltDefTrgtUpdate" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "update" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsDefaultTemplate();
    }

    public void testMinimumTargetDefaultTemplateMergeNONE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltMinTrgtNone" );
        prepareDirectoryStructure( getDirectoryMinimumTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "none" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsMinimumTemplate();
    }

    public void testMinimumTargetDefaultTemplateMergeOVERWRITE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltMinTrgtOverwrite" );
        prepareDirectoryStructure( getDirectoryMinimumTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "overwrite" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsDefaultTemplate();
    }

    public void testMinimumTargetDefaultTemplateMergeEXPAND()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltMinTrgtExpand" );
        prepareDirectoryStructure( getDirectoryMinimumTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "expand" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsMinimalTargetExpandedWithDefaultTemplate();
    }

    public void testMinimumTargetDefaultTemplateMergeUPDATE()
        throws Exception
    {
        // prepare
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-defTmpltMinTrgtUpdate" );
        prepareDirectoryStructure( getDirectoryMinimumTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_MERGE, "update" );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        validateAsMinimalTargetUpdatedWithDefaultTemplate();
    }

    public void testFromBaseDir()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-fromBaseDir" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );

        File sourceFile = new File( "src/test/resources/basefolder", getMojo().getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        File newSettingsFile = getTargetFile();
        assertTrue( newSettingsFile.getAbsolutePath() + " doesn't exist", newSettingsFile.exists() );
        assertEquals( sourceFile.length(), newSettingsFile.length() );

        validateAsDefaultTemplate();
    }

    public void testFromParameterRelative()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-fromParameterRelative" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );

        setVariableValueToObject( getMojo(), PROPERTY_SETTINGSFILE, getCustomTemplateFileArgument() );

        File sourceFile = new File( "src/test/resources/basefolder", getCustomTemplateFileArgument() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        File newSettingsFile = getTargetFile();
        assertEquals( true, newSettingsFile.exists() );
        assertEquals( sourceFile.length(), newSettingsFile.length() );

        validateAsCustomTemplate();
    }

    public void testFromParameterAbsolute()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-fromParameterAbsolute" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(),
                                  PROPERTY_SETTINGSFILE,
                                  FileUtils.resolveFile( currentTestWorkDirectory, getCustomTemplateFileArgument() ).getAbsolutePath() );

        File sourceFile = new File( "src/test/resources/basefolder", getCustomTemplateFileArgument() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        File newSettingsFile = getTargetFile();
        assertTrue( newSettingsFile.getAbsolutePath() + " doens't exist", newSettingsFile.exists() );
        assertEquals( sourceFile.length(), newSettingsFile.length() );

        validateAsCustomTemplate();
    }

    public void testFromUrl()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-fromUrl" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(),
                                  PROPERTY_SETTINGSFILE,
                                  FileUtils.resolveFile( currentTestWorkDirectory, getCustomTemplateFileArgument() ).toURI().toURL().toExternalForm() );

        File sourceFile = new File( "src/test/resources/basefolder", getCustomTemplateFileArgument() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        getMojo().execute();

        File newSettingsFile = getTargetFile();
        assertTrue( newSettingsFile.getAbsolutePath() + " doesn't exist", newSettingsFile.exists() );
        assertEquals( sourceFile.length(), newSettingsFile.length() );

        validateAsCustomTemplate();
    }

    public void testTemplateDefaultSettings()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-templateDefaultSettings" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), PROPERTY_SETTINGSFILE, getCustomTemplateFileArgument() );

        File sourceFile = new File( "src/test/resources/basefolder", getCustomTemplateFileArgument() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        try
        {
            getMojo().execute();
            fail( "mergetype is required, targetfile already exists" );
        }
        catch ( MojoExecutionException mee )
        {
            // mergetype required
        }
        catch ( MojoFailureException mfe )
        {
            // mergetype required
        }
    }

    protected abstract void validateAsDefaultTemplate()
        throws Exception;

    protected abstract void validateAsCustomTemplate()
        throws Exception;

    protected abstract void validateAsFilteredTemplate()
        throws Exception;

    protected void validateAsMinimumTemplate()
        throws Exception
    {
        validateAsNoSettings();
    }

    protected void validateAsMinimalTargetExpandedWithDefaultTemplate()
        throws Exception
    {
        validateAsDefaultTemplate();
    }

    protected void validateAsMinimalTargetUpdatedWithDefaultTemplate()
        throws Exception
    {
        validateAsDefaultTemplate();
    }

    protected abstract void validateAsDefaultSettings()
        throws Exception;

    protected abstract void validateAsNoSettings()
        throws Exception;
}
