package org.codehaus.mojo.sqlj;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Cleans out generated stale resources.
 * 
 * @goal clean
 * @phase clean
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 */
public class CleanMojo
    extends AbstractSqljMojo
{

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            FileUtils.deleteDirectory( getGeneratedSourcesDirectory() );
            FileUtils.deleteDirectory( getGeneratedResourcesDirectory() );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }
    }

}
