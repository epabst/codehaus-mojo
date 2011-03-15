package org.codehaus.mojo.setup;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;

public abstract class SetupManagerTestCase
    extends TestCase
{

    abstract SetupManager getSetupManager();
    
    protected SetupExecutionRequest getSetupExecutionRequest() 
    {
        return new DefaultSetupExecutionRequest();
    }
    
    protected MavenExecutionRequest getMavenExecutionRequest() 
    {
        return new DefaultMavenExecutionRequest();
    }
    
    public void testPrototype() throws Exception
    {
        assertNotNull( "getPrototypeInputStream() should not return null", getSetupManager().getPrototypeInputStream() );
    }
    
    public void testNullRequest() throws Exception
    {
        try
        {
            getSetupManager().process( null );
            fail( "SetupManager must fail with a null-request");
        }
        catch ( SetupExecutionException e )
        {
            //nop
        }
    }

    public void testEmptyRequest() throws Exception
    {
        try
        {
            getSetupManager().process( getSetupExecutionRequest() );
            fail( "SetupManager must fail with an empty request");
        }
        catch ( SetupExecutionException e )
        {
            //nop
        }
    }
    
    public void testEmptyAdditionalProperties() throws Exception
    {
        SetupExecutionRequest request = getSetupExecutionRequest();
        request.setAdditionalProperties( new Properties() );
        try
        {
            getSetupManager().process( request );
            fail( "SetupManager must fail with an empty additional properties");
        }
        catch ( SetupExecutionException e )
        {
            //nop
        }
    }
    
    public void testDirectoryAsTemplateFile() throws Exception
    {
        SetupExecutionRequest request = getSetupExecutionRequest();
        request.setTemplateFile( new File("/") );
        try
        {
            getSetupManager().process( request );
            fail( "SetupManager must fail with a directory as templateFile");
        }
        catch ( SetupExecutionException e )
        {
            //nop
        }
    }
    
    public void testNonexistingTemplateFile() throws Exception
    {
        SetupExecutionRequest request = getSetupExecutionRequest();
        request.setTemplateFile( new File("setup-123.props") );
        try
        {
            getSetupManager().process( request );
            fail( "SetupManager must fail when templateFile does not exist");
        }
        catch ( SetupExecutionException e )
        {
            //nop
        }
    }
    
    
}
