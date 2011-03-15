package org.codehaus.mojo.setup.security;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.setup.AbstractSetupMojoTest;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecUtil;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

public class SettingsSecuritySetupMojoTest
    extends AbstractSetupMojoTest
{
    private SettingsSecuritySetupMojo setupMojo;
    
    private final String settingsPath = ".m2/settings-security.xml";
    
    private File testResourceDefaultSettingsSecuritys;
    private File testResourceNoSettingsSecurity;
    private File testResourceMinimalSettingsSecurity;
    
    private File targetFile;
    
    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();
        
        testResourceDefaultSettingsSecuritys = getTestFile( "src/test/resources/defaultUserHome" );
        testResourceNoSettingsSecurity = getTestFile( "src/test/resources/emptyUserHome" );
        testResourceMinimalSettingsSecurity = getTestFile( "src/test/resources/minimalUserHome" );
        
        File testPom =
            new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        setupMojo = (SettingsSecuritySetupMojo) lookupMojo( "settings-security", testPom );
    }

    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );

        targetFile = new File( workDirectory + File.separator + settingsPath );
        
        DefaultSecDispatcher secDispatcher = (DefaultSecDispatcher) lookup( SecDispatcher.class.getName() );
        secDispatcher.setConfigurationFile( targetFile.getAbsolutePath() );
    }
    
    public void testMaster()
        throws Exception
    {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-master" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        
        setVariableValueToObject( getMojo(), "password",  "password" );

        assertFalse( getTargetFile().exists() );
        assertFalse( FileUtils.resolveFile( currentTestWorkDirectory, getMojo().getDefaultTemplateFilename() ).exists() );

        // execute the mojo
        getMojo().execute();

        assertTrue( targetFile.getAbsolutePath() + " doesn't exist", targetFile.exists() );
    }
    
    public void testInvalidPassword() throws Exception {
        File currentTestWorkDirectory = new File( getWorkDirectory(), getTestPrefix() + "-invalidPassword" );
        prepareDirectoryStructure( getDirectoryMissingTargetFile(), currentTestWorkDirectory );
        prepareMojo( currentTestWorkDirectory );
        
        String invalidPassword = "-D";
        setVariableValueToObject( getMojo(), "password", invalidPassword ); //

        try {
            getMojo().execute();
            fail( "Test should because of an invalid password: " + invalidPassword );
        }
        catch ( MojoExecutionException mee )
        {
            //nop
        }
    }
    
    @Override
    protected File getDirectoryDefaultTargetFile()
    {
        return testResourceDefaultSettingsSecuritys;
    }

    @Override
    protected File getDirectoryMissingTargetFile()
    {
        return testResourceNoSettingsSecurity;
    }
    
    @Override
    protected File getDirectoryMinimumTargetFile()
    {
        return testResourceMinimalSettingsSecurity;
    }

    @Override
    protected SettingsSecuritySetupMojo getMojo()
    {
        return setupMojo;
    }

    @Override
    protected String getTestPrefix()
    {
        return "settingssecurity";
    }
    
    @Override
    protected File getTargetFile()
    {
        return targetFile;
    }
    
    @Override
    protected String getTemplateBase()
    {
        return "settings-security";
    }

    @Override
    public void testDefaultTargetDefaultTemplateMergeEXPAND()
        throws Exception
    {
        try
        {
            super.testDefaultTargetDefaultTemplateMergeEXPAND();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}

    }
    
    @Override
    public void testDefaultTargetDefaultTemplateMergeUPDATE()
        throws Exception
    {
        try 
        {
            super.testDefaultTargetDefaultTemplateMergeUPDATE();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}
    }
    
    @Override
    public void testMinimumTargetDefaultTemplateMergeEXPAND()
        throws Exception
    {
        try 
        {
            super.testMinimumTargetDefaultTemplateMergeEXPAND();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}
    }
    
    @Override
    public void testMinimumTargetDefaultTemplateMergeUPDATE()
        throws Exception
    {
        try
        {
            super.testMinimumTargetDefaultTemplateMergeUPDATE();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}
    }
    
    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        SettingsSecurity sec = SecUtil.read( targetFile.getAbsolutePath() , false );
        assertEquals( "master_defaultTemplate", sec.getMaster() );
        assertEquals( "relocation_defaultTemplate", sec.getRelocation() );
        assertEquals( 0, sec.getConfigurations().size() );
        
    }
    
    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        SettingsSecurity sec = SecUtil.read( targetFile.getAbsolutePath() , false );
        assertEquals( "master_customTemplate", sec.getMaster() );
        assertEquals( "relocation_customTemplate", sec.getRelocation() );
        assertEquals( 0, sec.getConfigurations().size() );
    }
    
    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        SettingsSecurity sec = SecUtil.read( targetFile.getAbsolutePath() , false );
        assertEquals( "master_filteredTemplate", sec.getMaster() );
        assertEquals( "relocation_filteredTemplate", sec.getRelocation() );
        assertEquals( 0, sec.getConfigurations().size() );
    }
    
    @Override
    protected void validateAsMinimumTemplate()
        throws Exception
    {
        SettingsSecurity sec = SecUtil.read( targetFile.getAbsolutePath() , false );
        assertEquals( null, sec.getMaster() );
        assertEquals( null, sec.getRelocation() );
        assertEquals( 0, sec.getConfigurations().size() );
    }
    
    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        SettingsSecurity sec = SecUtil.read( targetFile.getAbsolutePath() , false );
        assertEquals( "master_currentSettings", sec.getMaster() );
        assertEquals( "relocation_currentSettings", sec.getRelocation() );
        assertEquals( 0, sec.getConfigurations().size() );
    }
    
    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        assertEquals( false, targetFile.exists() );
    }
}
