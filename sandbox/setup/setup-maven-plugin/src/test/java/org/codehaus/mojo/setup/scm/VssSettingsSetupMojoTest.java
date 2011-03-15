package org.codehaus.mojo.setup.scm;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.maven.scm.provider.vss.commands.VssCommandLineUtils;
import org.apache.maven.scm.providers.vss.settings.Settings;
import org.codehaus.mojo.setup.AbstractSetupMojo;

/*
 * This test can't work unless CvsUtil has a way to get the settingsdirectory
 */
public class VssSettingsSetupMojoTest
    extends AbstractScmSettingsSetupMojoTest
{
    private VssSettingsSetupMojo mojo = new VssSettingsSetupMojo();

    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();

        File testPom = new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        mojo = (VssSettingsSetupMojo) lookupMojo( "vss-settings", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        Method setScmConfDirMethod = VssCommandLineUtils.class.getDeclaredMethod( "setScmConfDir", File.class );

        try
        {
            setScmConfDirMethod.setAccessible( true );
            setScmConfDirMethod.invoke( null, new File( workDirectory, ".scm" ) );
        }
        finally
        {
            setScmConfDirMethod.setAccessible( false );
        }
        setTargetFile( VssCommandLineUtils.getScmConfFile() );
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return mojo;
    }

    protected String getDefaultTemplateFilename()
    {
        return VssSettingsSetupMojo.DEFAULT_TEMPLATE_FILENAME;
    }

    protected String getTestPrefix()
    {
        return "vss";
    }

    @Override
    protected String getTemplateBase()
    {
        return "vss-settings";
    }
    
    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        Settings settings = VssCommandLineUtils.getSettings();
        assertEquals( "vssDirectory_defaultTemplate", settings.getVssDirectory() );
    }
    
    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        Settings settings = VssCommandLineUtils.getSettings();
        assertEquals( "vssDirectory_customTemplate", settings.getVssDirectory() );
    }
    
    @Override
    protected void validateAsMinimumTemplate()
        throws Exception
    {
        Settings settings = VssCommandLineUtils.getSettings();
        assertEquals( null, settings.getVssDirectory() );

    }
    
    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        Settings settings = VssCommandLineUtils.getSettings();
        assertEquals( "vssDirectory_filteredTemplate", settings.getVssDirectory() );
    }
    
    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        Settings settings = VssCommandLineUtils.getSettings();
        assertEquals( "vssDirectory_currentSettings", settings.getVssDirectory() );
    }
    
    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        Settings settings = VssCommandLineUtils.getSettings();
        
        //if there is no settings file, setting will be null!!
        assertEquals( null, settings );
    }
}
