package org.codehaus.mojo.setup.scm;

import java.io.File;

import org.apache.maven.scm.provider.git.util.GitUtil;
import org.apache.maven.scm.providers.gitlib.settings.Settings;
import org.codehaus.mojo.setup.AbstractSetupMojo;

public class GitSettingsSetupMojoTest
    extends AbstractScmSettingsSetupMojoTest
{
    private GitSettingsSetupMojo mojo = new GitSettingsSetupMojo();

    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();

        File testPom =
            new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        mojo = (GitSettingsSetupMojo) lookupMojo( "git-settings", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        GitUtil.setSettingsDirectory( new File( workDirectory, ".scm" ) );
        setTargetFile( GitUtil.getSettingsFile() ) ;
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return mojo;
    }

    @Override
    protected String getDefaultTemplateFilename()
    {
        return GitSettingsSetupMojo.DEFAULT_TEMLPATE_FILENAME;
    }

    @Override
    protected String getTestPrefix()
    {
        return "git";
    }
    
    @Override
    protected String getTemplateBase()
    {
        return "git-settings";
    }

    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        Settings settings = GitUtil.readSettings();
        assertEquals( true, settings.isCommitNoVerify() );
        assertEquals( "revParseDateFormat_defaultTemplate", settings.getRevParseDateFormat() );
        assertEquals( "traceGitCommand_defaultTemplate", settings.getTraceGitCommand() );
    }
    
    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        Settings settings = GitUtil.readSettings();
        assertEquals( true, settings.isCommitNoVerify() );
        assertEquals( "revParseDateFormat_customTemplate", settings.getRevParseDateFormat() );
        assertEquals( "traceGitCommand_customTemplate", settings.getTraceGitCommand() );
    }
    
    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        Settings settings = GitUtil.readSettings();
        assertEquals( true, settings.isCommitNoVerify() );
        assertEquals( "revParseDateFormat_filteredTemplate", settings.getRevParseDateFormat() );
        assertEquals( "traceGitCommand_filteredTemplate", settings.getTraceGitCommand() );
    }
    
    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        Settings settings = GitUtil.readSettings();
        assertEquals( true, settings.isCommitNoVerify() );
        assertEquals( "revParseDateFormat_currentSettings", settings.getRevParseDateFormat() );
        assertEquals( "traceGitCommand_currentSettings", settings.getTraceGitCommand() );
    }
    
    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        Settings settings = GitUtil.readSettings();
        assertEquals( false, settings.isCommitNoVerify() );
        assertEquals( "yyyy-MM-dd HH:mm:ss", settings.getRevParseDateFormat() );
        assertEquals( "", settings.getTraceGitCommand() );    
    }
}
