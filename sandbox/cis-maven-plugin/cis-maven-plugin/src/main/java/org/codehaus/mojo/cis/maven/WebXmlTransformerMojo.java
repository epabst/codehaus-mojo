package org.codehaus.mojo.cis.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.cis.core.CisCoreErrorMessage;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.codehaus.mojo.cis.core.WebXmlMergerBean;


/**
 * A mojo for transforming the {@code web.xml} file. The source
 * file is read from the {@code cis.war} file.
 *
 * @goal webXml
 * @phase generate-resources
 */
public class WebXmlTransformerMojo extends AbstractCisMojo
{
    /**
     * Configures the files, which are being added to the
     * {@code web.xml} file.
     *
     * @parameter
     */
    private File[] webXmlFiles;

    /**
     * Sets the generated {@code web.xml} file. Defaults to
     * {@link AbstractCisMojo#getCisHomeDir() cis.home/WEB-INF/web.xml}.
     *
     * @parameter expression="${cis.targetFile}"
     */
    private File targetFile;

    /**
     * Sets the marker file to use. Defaults to
     * {@link AbstractCisMojo#getCisMarkersDir() project.build.directory/cis-maven-plugin/markers}/web.xml.marker.
     *
     * @parameter expression="${cis.targetFile}"
     */
    private File markerFile;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            final File warFile = getCisWebappFile();
            final URL webXmlURL = new URL( "jar:" + warFile.toURI().toURL().toExternalForm()
                                           + "!/WEB-INF/web.xml" );
            final List urls = new ArrayList();
            urls.add( webXmlURL );
            if ( webXmlFiles != null )
            {
                for ( int i = 0;  i < webXmlFiles.length;  i++ )
                {
                    urls.add( webXmlFiles[i].toURI().toURL() );
                }
            }
            final URL[] urlArray = (URL[]) urls.toArray( new URL[ urls.size() ] );
            final WebXmlMergerBean bean = new WebXmlMergerBean();
            bean.setWebXmlFiles( urlArray );
            bean.setCisHomeDir( getCisHomeDir() );
            bean.setCisMarkersDir( getCisMarkersDir() );
            bean.setCisUtils( newCisUtils() );
            bean.setTargetFile( targetFile );
            bean.setMarkerFile( markerFile );
            bean.execute();
        }
        catch ( CisCoreErrorMessage e )
        {
            throw new MojoFailureException( e.getMessage(), e );
        }
        catch ( CisCoreException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }
}
