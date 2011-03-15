package org.codehaus.mojo.setup.scm;

import java.io.File;

import org.apache.maven.scm.provider.starteam.util.StarteamUtil;
import org.apache.maven.scm.providers.starteam.settings.Settings;
import org.codehaus.mojo.setup.AbstractSetupMojo;

public class StarteamSettingsSetupMojoTest
    extends AbstractScmSettingsSetupMojoTest
{
    private AbstractSetupMojo mojo = new StarteamSettingsSetupMojo();

    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();
        
        File testPom =
            new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        mojo = (AbstractSetupMojo) lookupMojo( "starteam-settings", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        StarteamUtil.setSettingsDirectory( new File( workDirectory, ".scm" ) );
        setTargetFile( StarteamUtil.getSettingsFile() );
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return mojo;
    }

    protected String getDefaultTemplateFilename()
    {
        return StarteamSettingsSetupMojo.DEFAULT_TEMPLATE_FILENAME;
    }

    protected String getTestPrefix()
    {
        return "starteam";
    }
    @Override
    protected String getTemplateBase()
    {
        return "starteam-settings";
    }

    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        Settings settings = StarteamUtil.getSettings();
        assertEquals( true, settings.isCompressionEnable() );
        assertEquals( "eol_defaultTemplate", settings.getEol() );
    }
    
    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        Settings settings = StarteamUtil.getSettings();
        assertEquals( true, settings.isCompressionEnable() );
        assertEquals( "eol_customTemplate", settings.getEol() );
    }
    
    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        Settings settings = StarteamUtil.getSettings();
        assertEquals( true, settings.isCompressionEnable() );
        assertEquals( "eol_filteredTemplate", settings.getEol() );
    }
    
    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        Settings settings = StarteamUtil.getSettings();
        assertEquals( true, settings.isCompressionEnable() );
        assertEquals( "eol_currentSettings", settings.getEol() );
    }
    
    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        Settings settings = StarteamUtil.getSettings();
        assertEquals( false, settings.isCompressionEnable() );
        assertEquals( "on", settings.getEol() );
    }
}
