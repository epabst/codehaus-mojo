package org.codehaus.mojo.setup.settings;

import java.io.File;
import java.io.FileInputStream;

import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.SettingsReader;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.mojo.setup.AbstractSetupMojo;

public class GlobalSettingsSetupMojoTest
    extends AbstractSettingsSetupMojoTest
{
    private GlobalSettingsSetupMojo setupMojo;

    private final String settingsPath = "apache-maven-3/conf/settings.xml";

    private File targetFile;

    @Override
    protected void onSetUp()
        throws Exception
    {
        File testPom =
            new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        setupMojo = (GlobalSettingsSetupMojo) lookupMojo( "global-settings", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        File settingsFile = new File( workDirectory.getPath() + File.separator + settingsPath );

        MavenSession session = (MavenSession) getVariableValueFromObject( getMojo(), "session" );
        session.getRequest().setGlobalSettingsFile( settingsFile );

        targetFile = session.getRequest().getGlobalSettingsFile();

        if ( targetFile.exists() )
        {
            // lookup components
            MavenExecutionRequestPopulator populator = lookup( MavenExecutionRequestPopulator.class );
            SettingsReader settingsReader = lookup( SettingsReader.class );
            populator.populateFromSettings( session.getRequest(), settingsReader.read( settingsFile, null ) );
        }
    }

    @Override
    protected String getSettingsPathVariableName()
    {
        return "globalSettingsPath";
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return setupMojo;
    }

    @Override
    protected String getTestPrefix()
    {
        return "globalsettings";
    }

    @Override
    protected String getPropertyValueSuffix()
    {
        return "global";
    }

    @Override
    protected File getDirectoryDefaultTargetFile()
    {
        return getTestFile( "src/test/resources/defaultGlobalHome" );
    }

    @Override
    protected File getDirectoryMissingTargetFile()
    {
        return getTestFile( "src/test/resources/emptyGlobalHome" );
    }

    @Override
    protected File getDirectoryMinimumTargetFile()
    {
        return getTestFile( "src/test/resources/minimalGlobalHome" );
    }

    @Override
    protected File getTargetFile()
    {
        return targetFile;
    }

    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        SettingsXpp3Reader reader = new SettingsXpp3Reader();
        Settings settings = reader.read( new FileInputStream( getTargetFile() ) );
        assertEquals( "localRepository_currentGlobalSettings", settings.getLocalRepository() );
        assertEquals( false, settings.isInteractiveMode() );
        assertEquals( true, settings.isOffline() );
        assertEquals( true, settings.isUsePluginRegistry() );
    }
}
