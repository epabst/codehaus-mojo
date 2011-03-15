package org.codehaus.mojo.setup.scm;

import java.io.File;

import org.apache.maven.scm.provider.cvslib.util.CvsUtil;
import org.apache.maven.scm.providers.cvslib.settings.Settings;
import org.codehaus.mojo.setup.AbstractSetupMojo;

/*
 * This test can't work unless CvsUtil has a way to get the settingsdirectory
 */
public class CvsSettingsSetupMojoTest
    extends AbstractScmSettingsSetupMojoTest
{
    private CvsSettingsSetupMojo mojo = new CvsSettingsSetupMojo();

    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();
        
        File testPom =
            new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        mojo = (CvsSettingsSetupMojo) lookupMojo( "cvs-settings", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        CvsUtil.setSettingsDirectory( new File( workDirectory, ".scm" ) );
        setTargetFile( CvsUtil.getSettingsFile() ) ;
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return mojo;
    }

    @Override
    protected String getDefaultTemplateFilename()
    {
        return CvsSettingsSetupMojo.DEFAULT_TEMPLATE_FILENAME;
    }

    @Override
    protected String getTestPrefix()
    {
        return "cvs";
    }
    
    @Override
    protected String getTemplateBase()
    {
        return "cvs-settings";
    }

    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        Settings settings = CvsUtil.readSettings();
        assertEquals( true, settings.isTraceCvsCommand() );
        assertEquals( true, settings.isUseCvsrc() );
        assertEquals( false, settings.isUseForceTag() );
        assertEquals( "changeLogCommandDateFormat_defaultTemplate", settings.getChangeLogCommandDateFormat() );
        assertEquals( "temporaryFilesDirectory_defaultTemplate", settings.getTemporaryFilesDirectory() );
        assertEquals( "cvsVariables_defaultTemplate", settings.getCvsVariables().get( "key" ) );
        assertEquals( 1, settings.getCompressionLevel() );
    }
    
    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        Settings settings = CvsUtil.readSettings();
        assertEquals( true, settings.isTraceCvsCommand() );
        assertEquals( true, settings.isUseCvsrc() );
        assertEquals( false, settings.isUseForceTag() );
        assertEquals( "changeLogCommandDateFormat_customTemplate", settings.getChangeLogCommandDateFormat() );
        assertEquals( "temporaryFilesDirectory_customTemplate", settings.getTemporaryFilesDirectory() );
        assertEquals( "cvsVariables_customTemplate", settings.getCvsVariables().get( "key" ) );
        assertEquals( 2, settings.getCompressionLevel() );

    }
    
    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        Settings settings = CvsUtil.readSettings();
        assertEquals( true, settings.isTraceCvsCommand() );
        assertEquals( true, settings.isUseCvsrc() );
        assertEquals( false, settings.isUseForceTag() );
        assertEquals( "changeLogCommandDateFormat_filteredTemplate", settings.getChangeLogCommandDateFormat() );
        assertEquals( "temporaryFilesDirectory_filteredTemplate", settings.getTemporaryFilesDirectory() );
        assertEquals( "cvsVariables_filteredTemplate", settings.getCvsVariables().get( "key" ) );
        assertEquals( 4, settings.getCompressionLevel() );
    }
    
    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        Settings settings = CvsUtil.readSettings();
        assertEquals( true, settings.isTraceCvsCommand() );
        assertEquals( true, settings.isUseCvsrc() );
        assertEquals( false, settings.isUseForceTag() );
        assertEquals( "changeLogCommandDateFormat_currentSettings", settings.getChangeLogCommandDateFormat() );
        assertEquals( "temporaryFilesDirectory_currentSettings", settings.getTemporaryFilesDirectory() );
        assertEquals( "cvsVariables_currentSettings", settings.getCvsVariables().get( "key" ) );
        assertEquals( 5, settings.getCompressionLevel() );
    }
    
    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        Settings settings = CvsUtil.readSettings();
        assertEquals( false, settings.isTraceCvsCommand() );
        assertEquals( false, settings.isUseCvsrc() );
        assertEquals( true, settings.isUseForceTag() );
        assertEquals( "yyyy-MM-dd HH:mm:ssZ", settings.getChangeLogCommandDateFormat() );
        assertEquals( null, settings.getTemporaryFilesDirectory() );
        assertEquals( null, settings.getCvsVariables().get( "key" ) );
        assertEquals( 3, settings.getCompressionLevel() );
    }
}
