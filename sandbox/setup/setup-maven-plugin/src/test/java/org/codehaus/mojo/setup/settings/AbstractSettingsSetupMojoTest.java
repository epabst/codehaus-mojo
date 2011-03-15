package org.codehaus.mojo.setup.settings;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.DefaultSettingsReader;
import org.apache.maven.settings.io.SettingsReader;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.mojo.setup.AbstractSetupMojo;
import org.codehaus.mojo.setup.AbstractSetupMojoTest;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;

public abstract class AbstractSettingsSetupMojoTest
    extends AbstractSetupMojoTest
{
    private static final String TEST_RESOURCE_BASEFOLDER = "src/test/resources/basefolder";

    private static final String SETTINGS_TEMPLATE = "templateFilename";

    public static final String PARAMETER_MERGE = "merge";

    protected abstract AbstractSetupMojo getMojo();

    protected abstract String getSettingsPathVariableName();

    protected abstract String getPropertyValueSuffix();

    @Override
    protected String getTemplateBase()
    {
        return "settings";
    }
    
    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );
        FileUtils.copyDirectoryStructure( getTestFile( "src/test/resources/encryptableUserHome" ), workDirectory );
        MavenSession session = (MavenSession) getVariableValueFromObject( getMojo(), "session" );
        Properties properties = new Properties();
        properties.put( DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION,
                        FileUtils.resolveFile( workDirectory, ".m2/settings-security.xml" ).getAbsolutePath() );
        session.getRequest().setUserProperties( properties );
    }

    public void testFilterSystemProperties()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-filterSystemProperties" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        File sourceFile = new File( TEST_RESOURCE_BASEFOLDER, getPropertyFileFilteredTemplateFileArgument() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        prepareMojo( currentTestWorkDirectory );
        setVariableValueToObject( getMojo(), SETTINGS_TEMPLATE, getPropertyFileFilteredTemplateFileArgument() );

        getMojo().execute();

        SettingsXpp3Reader reader = new SettingsXpp3Reader();
        Settings mergedSettings = reader.read( new FileInputStream( getTargetFile() ) );
        assertEquals( System.getProperty( "user.name" ) + "-dev",
                      ( (Profile) mergedSettings.getProfiles().get( 0 ) ).getId() );
    }

    public void testEncryptPasswordsDefaultTemplate()
        throws Exception
    {
        File currentTestWorkDirectory =
            new File( getWorkDirectory(), getTestPrefix() + "-encryptPasswordsDefaultTemplate" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );
        File sourceFile = new File( TEST_RESOURCE_BASEFOLDER, getDefaultTemplateFilename() );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        prepareMojo( currentTestWorkDirectory );

        setVariableValueToObject( getMojo(), "encryptPasswords", Boolean.TRUE );
        setVariableValueToObject( getMojo(), "merge", "overwrite" );

        getMojo().execute();

        SettingsXpp3Reader reader = new SettingsXpp3Reader();
        Settings mergedSettings = reader.read( new FileInputStream( getTargetFile() ) );

        assertEquals( "dt_username_defaultTemplate", mergedSettings.getServer( "dt" ).getUsername() );
        assertFalse( "dt_password_defaultTemplate".equals( mergedSettings.getServer( "dt" ).getPassword() ) );
    }

    public void testInvalidPassword()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-invalidPassword" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );

        String invalidSettingsFileArgument = "settings-invalid.xml";
        File sourceFile = new File( TEST_RESOURCE_BASEFOLDER, invalidSettingsFileArgument );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        prepareMojo( currentTestWorkDirectory );

        setVariableValueToObject( getMojo(), SETTINGS_TEMPLATE, invalidSettingsFileArgument );
        setVariableValueToObject( getMojo(), "encryptPasswords", Boolean.TRUE );
        setVariableValueToObject( getMojo(), "merge", "overwrite" );

        try
        {
            getMojo().execute();
            fail( "Test should fail, because settingsfile contains an unencryptable password." );
        }
        catch ( MojoFailureException mfe )
        {
            // nop
        }
    }

    public void testEncryptEncryptedPassword()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-entryptEncryptedPassword" );
        prepareDirectoryStructure( getDirectoryDefaultTargetFile(), currentTestWorkDirectory );

        String encryptedSettingsFileArgument = "settings-encrypted.xml";
        File sourceFile = new File( TEST_RESOURCE_BASEFOLDER, encryptedSettingsFileArgument );
        FileUtils.copyFileToDirectory( sourceFile, currentTestWorkDirectory );

        prepareMojo( currentTestWorkDirectory );

        setVariableValueToObject( getMojo(), SETTINGS_TEMPLATE, encryptedSettingsFileArgument );
        setVariableValueToObject( getMojo(), "encryptPasswords", Boolean.TRUE );
        setVariableValueToObject( getMojo(), "merge", "overwrite" );

        getMojo().execute();

        SettingsXpp3Reader reader = new SettingsXpp3Reader();
        Settings mergedSettings = reader.read( new FileInputStream( getTargetFile() ) );

        assertEquals( "dt_username_defaultTemplate", mergedSettings.getServer( "dt" ).getUsername() );
        assertEquals( "{OxhDEWjvOzsHR3km+NFZXMIR/S2Qucv1WbcRjstHviPZ45uvrJ1l3Jd80EhLBs9q}", 
                      mergedSettings.getServer( "dt" ).getPassword() );

    }

    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        SettingsXpp3Reader reader = new SettingsXpp3Reader();
        Settings settings = reader.read( new FileInputStream( getTargetFile() ) );
        assertEquals( "localRepository_defaultTemplate", settings.getLocalRepository() );
        assertEquals( false, settings.isInteractiveMode() );
        assertEquals( true, settings.isOffline() );
        assertEquals( true, settings.isUsePluginRegistry() );
    }

    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        SettingsXpp3Reader reader = new SettingsXpp3Reader();
        Settings settings = reader.read( new FileInputStream( getTargetFile() ) );
        assertEquals( "localRepository_customTemplate", settings.getLocalRepository() );

        assertEquals( false, settings.isInteractiveMode() );
        assertEquals( true, settings.isOffline() );
        assertEquals( true, settings.isUsePluginRegistry() );
    }

    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        SettingsReader reader = new DefaultSettingsReader();

        Settings settings = reader.read( getTargetFile(), null );
        assertEquals( "localRepository_filteredTemplate", settings.getLocalRepository() );
        assertEquals( false, settings.isInteractiveMode() );
        assertEquals( true, settings.isOffline() );
        assertEquals( true, settings.isUsePluginRegistry() );

    }

    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        assertEquals( false, getTargetFile().exists() );
    }

    @Override
    protected void validateAsMinimumTemplate()
        throws Exception
    {
        SettingsReader reader = new DefaultSettingsReader();

        Settings settings = reader.read( getTargetFile(), null );
        assertEquals( null, settings.getLocalRepository() );
        assertEquals( true, settings.isInteractiveMode() );
        assertEquals( false, settings.isOffline() );
        assertEquals( false, settings.isUsePluginRegistry() );
    }
    
    /**
     * The {@link org.apache.maven.settings.SettingsUtils#merge(Settings, Settings, String)}
     * ignores boolean values from the recessive settings 
     */
    @Override
    protected void validateAsMinimalTargetExpandedWithDefaultTemplate()
        throws Exception
    {
        SettingsReader reader = new DefaultSettingsReader();

        Settings settings = reader.read( getTargetFile(), null );
        assertEquals( "localRepository_defaultTemplate", settings.getLocalRepository() );
        assertEquals( true, settings.isInteractiveMode() );
        assertEquals( false, settings.isOffline() );
        assertEquals( false, settings.isUsePluginRegistry() );
    }
}
