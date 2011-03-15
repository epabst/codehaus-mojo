package org.codehaus.mojo.ruby;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.jruby.JRubyInvoker;
import org.codehaus.plexus.component.jruby.JRubyRuntimeInvoker;
import org.codehaus.plexus.util.StringOutputStream;

/**
 * @goal run
 * @requiresDependencyResolution
 * @description Runs a given ruby script
 * @author eredmond
 * @version $Id$
 */
public class RunScriptMojo
	extends AbstractMojo
{
	/**
	 * A path to a ruby script file to be executed.
	 * @parameter
	 */
	private File script;

	/**
	 * A block of ruby code to be executed.
	 * @parameter 
	 */
	private String ruby;

    /**
     * Specifies $LOAD_PATH directories.
	 * @parameter
     */
	private String[] libraryPaths;

	/**
	 * Require the library, before executing your script.
	 * @parameter
	 */
    private String[] requires;

    /**
     * Assume 'while gets(); ... end' loop around your script.
	 * @parameter default-value=false
     */
    private boolean assumeLoop;

    /**
     * Assume loop like 'assumeLoop' but print line also like sed.
	 * @parameter default-value=false
     */
    private boolean assumePrintLoop;

    /**
     * Autosplit mode with 'assumeLoop' or 'assumePrintLoop' (splits $_ into $F)
	 * @parameter default-value=false
     */
    private boolean autoSplit;

    /**
     * Enable line ending processing.
	 * @parameter default-value=false
     */
    private boolean processLineEnds;

    /**
     * Set warning level; 0=silence, 1=medium, 2=verbose
	 * @parameter default-value=1
     */
    private int warning;

    /**
     * Sets debugging flags (set $DEBUG to true).
	 * @parameter default-value=false
     */
    private boolean debug;

    /**
     * @parameter default-value="${basedir}"
     */
    private File currentDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
    	JRubyInvoker invoker = new JRubyRuntimeInvoker( null );

		if ( script != null )
        {
			try
			{
	        	InputStream scriptStream = new FileInputStream( script );
	            invoker.setReader( new InputStreamReader( scriptStream ) );
			}
			catch ( FileNotFoundException e )
			{
				throw new MojoFailureException( e.getMessage() );
			}
        }
        else if ( ruby != null )
        {
        	invoker.setReader( new StringReader( ruby ) );
        }
        else
        {
        	throw new MojoFailureException( "Cannot execute [rubyscript:run] without configuring either 'script' or 'ruby'" );
        }

        invoker.setCurrentDirectory( currentDirectory.getAbsolutePath() );
        invoker.setAssumePrintLoop( assumePrintLoop );
        invoker.setAssumeLoop( assumeLoop );
        invoker.setAutoSplit( autoSplit );
        invoker.setProcessLineEnds( processLineEnds );
        invoker.setWarning( warning );
        invoker.setDebug( debug );
        invoker.setLibraryPaths( libraryPaths );
        invoker.setRequires( requires );

        //invoker.setDebug( true );
        try
        {
            StringOutputStream stdout = new StringOutputStream();
            StringOutputStream stderr = new StringOutputStream();

            invoker.invoke( stdout, stderr );

            logOutput( stdout.toString(), false );
            logOutput( stderr.toString(), true );
        }
        catch ( Throwable e )
        {
        	throw new MojoFailureException( e.getMessage() );
        }
    }

    /**
     * Outputs Strings as info or error to the mojo's log.
     * 
     * @param out 
     * @param error true if error
     */
    private void logOutput(  String output, boolean error )
    {
        if ( output != null && output.length() > 0 )
        {
            for ( StringTokenizer tokens = new StringTokenizer( output, "\n" ); tokens.hasMoreTokens(); )
            {
                if ( error )
                {
                    getLog().error( tokens.nextToken() );
                }
                else
                {
                    getLog().info( tokens.nextToken() );
                }
            }
        }
    }
}
