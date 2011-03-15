package org.codehaus.mojo.push;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.wagon.AbstractWagon;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.PathUtils;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.repository.Repository;

/**
 * Pushes a project's artifacts to a set of locations using
 * the chosen Wagon mechanism. Currently provides no support
 * for security - operation is not atomic.
 * 
 * Currently supports the following protocols: file
 * 
 * @author Eric Redmond
 * 
 * @goal push
 * @phase deploy
 */
public class PushMojo
    extends AbstractMojo
{
    /**
     * List of destinations in wagon provider form.
     * e.g. ftp://remote/copy/to/here
     * 
     * @parameter
     */
    private String[] destinations;

    /**
     * The files to push.
     * 
     * @parameter
     */
    private File[] files;

    /**
     * The mechanism of moving projects througha filesystem.
     * 
     * @component role="org.apache.maven.wagon.Wagon" role-hint="file"
     * @required
     */
    private Wagon fileWagon;

    // TODO: No need for this complication until push supports all wagons
//    /**
//     * The mechanism of moving projects.
//     * 
//     * @component role="org.apache.maven.artifact.manager.WagonManager"
//     * @required
//     * @readonly
//     */
//    private WagonManager wagonManager;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
//        Set protocols = new HashSet();
//
//        for( int i = 0; i < destinations.length; i++ )
//        {
//            protocols.add( PathUtils.protocol( destinations[i] ) );
//        }
//
//        Map protocolMap = new HashMap();
//
//        String currentProtocol = null;
//        try
//        {
//            for( Iterator iter = protocols.iterator(); iter.hasNext(); )
//            {
//                currentProtocol = (String)iter.next();
//
//                Wagon wagon = wagonManager.getWagon( currentProtocol );
//
//                if( wagon == null )
//                {
//                    throw new MojoExecutionException( "Protocol support not found by WagonManager: " + currentProtocol );
//                }
//
//                protocolMap.put( currentProtocol, wagon );
//            }
//        }
//        catch( UnsupportedProtocolException e )
//        {
//            throw new MojoExecutionException( currentProtocol, e );
//        }

        for( int i = 0; i < destinations.length; i++ )
        {
            String url = destinations[i];

            String protocol = PathUtils.protocol( url );

            if( getLog().isDebugEnabled() )
            {
                getLog().debug( "Protocol: " + protocol );
                getLog().debug( "BaseDir: " + PathUtils.basedir( url ));
                getLog().debug( "Dirname: " + PathUtils.dirname( url ));
                getLog().debug( "Filename: " + PathUtils.filename( url ));
                getLog().debug( "Host: " + PathUtils.host( url ));
                getLog().debug( "Port: " + PathUtils.port( url ));
            }

//            Wagon wagon = (Wagon)protocolMap.get( protocol );

            Repository repository = new Repository( "tmp", url );

            AbstractWagon wagon = (AbstractWagon)fileWagon;

            try
            {
                wagon.connect( repository );

                for( int j = 0; j < files.length; j++ )
                {
                    File file = files[j];
                    try
                    {
                        wagon.put( file, file.getName() );
                    }
                    catch( TransferFailedException e )
                    {
                        getLog().error( "Cannot transfer file " + file.getName() + " because "+ e.getMessage() );
                    }
                    catch( ResourceDoesNotExistException e )
                    {
                        getLog().error( "Cannot transfer file " + file.getName() + " because " + e.getMessage() );
                    }
                }
            }
            catch( ConnectionException e )
            {
                getLog().error( "Attempting to push to next server", e );
            }
            catch( AuthenticationException e )
            {
                getLog().error( "Attempting to push to next server", e );
            }
            catch( AuthorizationException e )
            {
                getLog().error( "Attempting to push to next server", e );
            }
            finally
            {
                try
                {
                    if( wagon != null )
                    {
                        wagon.disconnect();
                    }
                }
                catch( ConnectionException e )
                {
                    getLog().debug( e );
                }
            }
        }
    }
}
