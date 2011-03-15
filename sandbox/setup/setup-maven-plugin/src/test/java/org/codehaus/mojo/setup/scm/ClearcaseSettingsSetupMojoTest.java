package org.codehaus.mojo.setup.scm;

import java.io.File;

import org.apache.maven.scm.provider.clearcase.util.ClearCaseUtil;
import org.apache.maven.scm.providers.clearcase.settings.Settings;
import org.codehaus.mojo.setup.AbstractSetupMojo;
import org.codehaus.plexus.util.FileUtils;

public class ClearcaseSettingsSetupMojoTest
    extends AbstractScmSettingsSetupMojoTest
{
    private ClearcaseSettingsSetupMojo mojo = new ClearcaseSettingsSetupMojo();

    final String settingsPath = ".scm/clearcase-settings.xml";

    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();
        
        File testPom = new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        mojo = (ClearcaseSettingsSetupMojo) lookupMojo( "clearcase-settings", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        ClearCaseUtil.setSettingsDirectory( new File( workDirectory, ".scm" ) );
        
        //better would be ClearCaseUtil.getSettingsFile();
        setTargetFile( FileUtils.resolveFile( workDirectory, settingsPath ) );
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return mojo;
    }

    @Override
    protected String getDefaultTemplateFilename()
    {
        return ClearcaseSettingsSetupMojo.DEFAULT_TEMPLATE_FILENAME;
    }

    @Override
    protected String getTestPrefix()
    {
        return "clearcase";
    }

    @Override
    protected String getTemplateBase()
    {
        return "clearcase-settings";
    }
    
    @Override
    protected void validateAsDefaultTemplate() throws Exception 
    {
        Settings settings = ClearCaseUtil.getSettings();
        assertEquals( false, settings.isUseVWSParameter() );
        assertEquals( "changelogUserFormat_defaultTemplate", settings.getChangelogUserFormat() );
        assertEquals( "clearcaseType_defaultTemplate", settings.getClearcaseType() );
        assertEquals( "viewstore_defaultTemplate", settings.getViewstore() );
    }
    
    @Override
    protected void validateAsCustomTemplate() throws Exception 
    {
        Settings settings = ClearCaseUtil.getSettings();
        assertEquals( false, settings.isUseVWSParameter() );
        assertEquals( "changelogUserFormat_customTemplate", settings.getChangelogUserFormat() );
        assertEquals( "clearcaseType_customTemplate", settings.getClearcaseType() );
        assertEquals( "viewstore_customTemplate", settings.getViewstore() );
    }
    
    @Override
    protected void validateAsFilteredTemplate() throws Exception 
    {
        Settings settings = ClearCaseUtil.getSettings();
        assertEquals( false, settings.isUseVWSParameter() );
        assertEquals( "changelogUserFormat_filteredTemplate", settings.getChangelogUserFormat() );
        assertEquals( "clearcaseType_filteredTemplate", settings.getClearcaseType() );
        assertEquals( "viewstore_filteredTemplate", settings.getViewstore() );
    }
    
    @Override
    protected void validateAsDefaultSettings() throws Exception
    {
        Settings settings = ClearCaseUtil.getSettings();
        assertEquals( false, settings.isUseVWSParameter() );
        assertEquals( "changelogUserFormat_currentSettings", settings.getChangelogUserFormat() );
        assertEquals( "clearcaseType_currentSettings", settings.getClearcaseType() );
        assertEquals( "viewstore_currentSettings", settings.getViewstore() );
    }
    
    @Override
    protected void validateAsNoSettings() throws Exception
    {
        Settings settings = ClearCaseUtil.getSettings();
        assertEquals( true, settings.isUseVWSParameter() );
        assertEquals( null, settings.getChangelogUserFormat() );
        assertEquals( null, settings.getClearcaseType() );
        assertEquals( null, settings.getViewstore() );
    }
}
