package org.codehaus.mojo.was6;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.dom4j.Document;

/**
 * Cleans out temporary resources and generated sources.
 * 
 * @goal clean
 * @phase clean
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class CleanMojo
    extends AbstractEjbMojo
{

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        nullSafeDeleteDirectory( getWorkingDirectory() );
        nullSafeDeleteDirectory( getGeneratedSourcesDirectory() );
    }

    /**
     * Deletes a directory.
     * 
     * @param directory to be deleted, null is handled gracefully.
     */
    private void nullSafeDeleteDirectory( File directory )
    {
        if ( directory != null )
        {
            try
            {
                getLog().info( "Deleting directory: " + directory.getAbsolutePath() );
                FileUtils.deleteDirectory( directory );
            }
            catch ( IOException e )
            {
                getLog().error( e );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
    }
}
