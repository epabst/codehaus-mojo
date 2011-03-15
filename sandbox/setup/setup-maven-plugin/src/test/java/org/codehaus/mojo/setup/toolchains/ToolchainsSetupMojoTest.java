package org.codehaus.mojo.setup.toolchains;

import java.io.File;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.toolchain.ToolchainsBuilder;
import org.apache.maven.toolchain.model.PersistedToolchains;
import org.apache.maven.toolchain.model.ToolchainModel;
import org.codehaus.mojo.setup.AbstractSetupMojo;
import org.codehaus.mojo.setup.AbstractSetupMojoTest;

public class ToolchainsSetupMojoTest
    extends AbstractSetupMojoTest
{
    private ToolchainsBuilder toolchainsBuilder;
    
    private ToolchainsSetupMojo setupMojo;
    
    private final String settingsPath = ".m2/toolchains.xml";

    public static final String PARAMETER_MERGE = "merge";
    
    private File testResourceDefaultToolchains;
    private File testResourceNoToolchains;
    private File testResourceMinimumToolchains;
    private File targetFile;

    @Override
    protected void onSetUp()
        throws Exception
    {
        super.onSetUp();
        
        testResourceDefaultToolchains = getTestFile( "src/test/resources/defaultUserHome" );
        testResourceNoToolchains = getTestFile( "src/test/resources/emptyUserHome" );
        testResourceMinimumToolchains = getTestFile( "src/test/resources/minimalUserHome" );

        File testPom =
            new File( getBasedir(),
                      "src/test/resources/unit/default-configuration/default-configuration-plugin-config.xml" );
        setupMojo = (ToolchainsSetupMojo) lookupMojo( "toolchains", testPom );
        toolchainsBuilder = (ToolchainsBuilder) lookup( ToolchainsBuilder.class );
    }
    
    @Override
    protected void prepareMojo( File workDirectory )
        throws Exception
    {
        super.prepareMojo( workDirectory );
        DefaultMavenExecutionRequest mavenRequest = new DefaultMavenExecutionRequest();
        mavenRequest.setUserToolchainsFile( new File( workDirectory.getPath() + File.separator + settingsPath ) );
        mavenRequest.setSystemProperties( System.getProperties() );
        setVariableValueToObject( getMojo(), "session", new MavenSession( this.getContainer(),
                                                                          new MavenRepositorySystemSession(),
                                                                          mavenRequest,
                                                                          new DefaultMavenExecutionResult() ) );

        targetFile = mavenRequest.getUserToolchainsFile();
    }
    
    @Override
    protected File getDirectoryDefaultTargetFile()
    {
        return testResourceDefaultToolchains;
    }

    @Override
    protected File getDirectoryMissingTargetFile()
    {
        return testResourceNoToolchains;
    }
    
    @Override
    protected File getDirectoryMinimumTargetFile()
    {
        return testResourceMinimumToolchains;
    }

    @Override
    protected AbstractSetupMojo getMojo()
    {
        return setupMojo;
    }

    @Override
    protected String getTemplateBase()
    {
        return "toolchains";
    }
        
    @Override
    protected String getTestPrefix()
    {
        return "toolchains";
    }

    @Override
    protected File getTargetFile()
    {
        return targetFile;
    }
    
    @Override
    public void testDefaultTargetDefaultTemplateMergeEXPAND()
        throws Exception
    {
        try {
            super.testDefaultTargetDefaultTemplateMergeEXPAND();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}
    }
    
    @Override
    public void testDefaultTargetDefaultTemplateMergeUPDATE()
        throws Exception
    {
        try {
            super.testDefaultTargetDefaultTemplateMergeUPDATE();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}
    }
    
    @Override
    public void testMinimumTargetDefaultTemplateMergeEXPAND()
        throws Exception
    {
        try {
            super.testMinimumTargetDefaultTemplateMergeEXPAND();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}
    }
    
    @Override
    public void testMinimumTargetDefaultTemplateMergeUPDATE()
        throws Exception
    {
        try {
            super.testMinimumTargetDefaultTemplateMergeUPDATE();
            fail("unsupported");
        }
        catch ( MojoExecutionException e) {}
    }
    
    @Override
    protected void validateAsDefaultTemplate()
        throws Exception
    {
        PersistedToolchains toolchains = toolchainsBuilder.build( targetFile );
        ToolchainModel toolchain = toolchains.getToolchains().get( 0 );
        assertEquals( "type_defaultTemplate" , toolchain.getType() );
    }
    
    @Override
    protected void validateAsCustomTemplate()
        throws Exception
    {
        PersistedToolchains toolchains = toolchainsBuilder.build( targetFile );
        ToolchainModel toolchain = toolchains.getToolchains().get( 0 );
        assertEquals( "type_customTemplate" , toolchain.getType() );
    }
    
    @Override
    protected void validateAsFilteredTemplate()
        throws Exception
    {
        PersistedToolchains toolchains = toolchainsBuilder.build( targetFile );
        ToolchainModel toolchain = toolchains.getToolchains().get( 0 );
        assertEquals( "type_filteredTemplate" , toolchain.getType() );
    }
    
    @Override
    protected void validateAsMinimumTemplate()
        throws Exception
    {
        PersistedToolchains toolchain = toolchainsBuilder.build( targetFile );
        assertEquals( 0, toolchain.getToolchains().size() );
    }
    
    @Override
    protected void validateAsDefaultSettings()
        throws Exception
    {
        PersistedToolchains toolchains = toolchainsBuilder.build( targetFile );
        ToolchainModel toolchain = toolchains.getToolchains().get( 0 );
        assertEquals( "type_currentSettings" , toolchain.getType() );
    }
    
    @Override
    protected void validateAsNoSettings()
        throws Exception
    {
        PersistedToolchains toolchain = toolchainsBuilder.build( targetFile );
        assertEquals( null , toolchain );
    }
}
