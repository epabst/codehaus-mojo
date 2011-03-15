package org.codehaus.mojo.syslog;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.truezip.util.DefaultTrueZip;
import org.codehaus.mojo.truezip.util.TrueZip;
import org.codehaus.mojo.truezip.util.TrueZipFileSet;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogMessageProcessorIF;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileReader;

/**
 * Send a syslog message, or messages in a TrueZip fileset
 * @goal send
 * @requiresProject false
 */
public class SendSyslogMojo
    extends AbstractSyslogMojo
{

    /**
     * Single message to send. It is ignored when <i>directory</i> configured
     * @parameter expression="${message}" 
     */
    private String message;

    /**
     * Syslog host to send to
     * @parameter expression="${host}" default-value="127.0.0.1"
     */
    private String host;
    
    /**
     * The original host that message created. 
     * @parameter expression="${messageHost}" 
     */
    private String messageHost;
    

    /**
     * Regular or TrueZIP directory to pickup data files
     * @parameter expression="${directory}
     */
    private java.io.File directory;

    /**
     * comma separate list of ANT's includes expression
     * @parameter expression="${includes}"
     */
    private String includes;

    /**
     * comma separate list of ANT's exclude expression
     * @parameter expression="${excludes}"
     */
    private String excludes;

    /**
     * Number of delay milliseconds between syslog sends
     * @parameter expression="${delay}" default-value="0"
     */
    private int delay = 0;

    private SyslogIF syslog;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        
        syslog = Syslog.getInstance( protocol );
        
        if ( StringUtils.isBlank( this.messageHost ) ) {
            RedirectSyslogMessageStructureProcessor msgProcessor = new  RedirectSyslogMessageStructureProcessor();
            msgProcessor.setFromHost( this.messageHost );
            syslog.setMessageProcessor( msgProcessor );
        }
        
        syslog.getConfig().setHost( host );
        syslog.getConfig().setPort( port );
        syslog.getConfig().setUseStructuredData( true );

        if ( directory == null )
        {
            sendSingleMessage( message );
            return;
        }

        sendFiles();
    }

    private void sendSingleMessage( String message )
    {
        if ( message == null )
        {
            message = "testing send message";
            sendLine( message );
        }
        return;
    }

    private void sendFiles()
        throws MojoExecutionException, MojoFailureException
    {
        TrueZipFileSet fileSet = new TrueZipFileSet();
        fileSet.setDirectory( directory.getAbsolutePath() );
        if ( includes != null )
        {
            fileSet.setIncludes( tokenize( includes ) );
        }

        if ( excludes != null )
        {
            fileSet.setExcludes( tokenize( excludes ) );
        }

        TrueZip truezip = new DefaultTrueZip();
        List fileList = truezip.list( fileSet );

        for ( int i = 0; i < fileList.size(); ++i )
        {
            this.getLog().info( "Sending " + fileList.get( i ) );
            sendFile( (File) fileList.get( i ) );
        }

    }

    private static List tokenize( String values )
    {
        String[] tokens = StringUtils.split( values, ", " );
        List list = new ArrayList();
        for ( int i = 0; i < tokens.length; ++i )
        {
            list.add( tokens[i] );
        }

        return list;
    }

    private void sendFile( File file )
        throws MojoExecutionException, MojoFailureException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader( new FileReader( file ) );

            while ( true )
            {
                String line = reader.readLine();
                if ( line == null )
                {
                    break;
                }
                if ( !StringUtils.isBlank( line ) )
                {
                    sendLine( line );
                    if ( delay != 0 )
                    {
                        Thread.sleep( delay );
                    }
                }
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( InterruptedException e )
        {

        }
        finally
        {
            IOUtil.close( reader );
        }
    }

    private void sendLine( String line )
    {
        syslog.info( line );
    }

}
