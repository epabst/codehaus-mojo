package org.codehaus.mojo.setup.scm;

import java.io.File;

import org.apache.maven.scm.provider.svn.util.SvnUtil;
import org.apache.maven.scm.providers.svn.settings.Settings;
import org.codehaus.mojo.setup.AbstractSetupMojo;

public class SvnSettingsSetupMojoTest
    extends AbstractScmSettingsSetupMojoTest
{
    private AbstractSetupMojo mojo = new SvnSettingsSetupMojo();

    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();
        
        File testPom =
            new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        mojo = (AbstractSetupMojo) lookupMojo( "svn-settings", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        SvnUtil.setSettingsDirectory( new File( workDirectory, ".scm" ) );
        setTargetFile( SvnUtil.getSettingsFile() );
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return mojo;
    }

    @Override
    protected String getDefaultTemplateFilename()
    {
        return SvnSettingsSetupMojo.DEFAULT_TEMPLATE_FILENAME;
    }
    @Override
    protected String getTestPrefix()
    {
        return "svn";
    }

    @Override
    protected String getTemplateBase()
    {
        return "svn-settings";
    }

    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        Settings settings = SvnUtil.readSettings();
        assertEquals( "configDirectory_defaultTemplate", settings.getConfigDirectory() );
        assertEquals( true, settings.isUseCygwinPath() );
        assertEquals( "cygwinMountPath_defaultTemplate", settings.getCygwinMountPath() );
        assertEquals( false, settings.isUseNonInteractive() );
    }
    
    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        Settings settings = SvnUtil.readSettings();
        assertEquals( "configDirectory_customTemplate", settings.getConfigDirectory() );
        assertEquals( true, settings.isUseCygwinPath() );
        assertEquals( "cygwinMountPath_customTemplate", settings.getCygwinMountPath() );
        assertEquals( false, settings.isUseNonInteractive() );
    }

    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        Settings settings = SvnUtil.readSettings();
        assertEquals( "configDirectory_filteredTemplate", settings.getConfigDirectory() );
        assertEquals( true, settings.isUseCygwinPath() );
        assertEquals( "cygwinMountPath_filteredTemplate", settings.getCygwinMountPath() );
        assertEquals( false, settings.isUseNonInteractive() );
    }
    
    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        Settings settings = SvnUtil.readSettings();
        assertEquals( "configDirectory_currentSettings", settings.getConfigDirectory() );
        assertEquals( true, settings.isUseCygwinPath() );
        assertEquals( "cygwinMountPath_currentSettings", settings.getCygwinMountPath() );
        assertEquals( false, settings.isUseNonInteractive() );
    }
    
    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        Settings settings = SvnUtil.readSettings();
        assertEquals( null, settings.getConfigDirectory() );
        assertEquals( false, settings.isUseCygwinPath() );
        assertEquals( "/cygwin", settings.getCygwinMountPath() );
        assertEquals( true, settings.isUseNonInteractive() );
    }
}
