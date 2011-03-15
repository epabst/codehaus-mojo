package org.codehaus.mojo.cis.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.cis.core.CisConfig;
import org.codehaus.mojo.cis.core.CisConfigTransformer;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.codehaus.mojo.cis.core.CisUtils;
import org.codehaus.mojo.cis.core.DefaultResource;
import org.xml.sax.InputSource;


/**
 * A mojo for transforming the {@code cisconfig.xml} file. The source
 * file can either be read from the {@code cis.war} file or from a
 * copy of the original {@code cisconfig.xml} file.
 *
 * @goal cisConfig
 * @phase generate-resources
 */
public class CisConfigTransformerMojo extends AbstractCisMojo
{
    /**
     * The {@code cisconfig.xml} file. If this is configured, then it will
     * be taken as the transformations source file.
     * @parameter
     */
    private File cisConfigSourceFile;

    /**
     * The created {@code cisconfig.xml} file. This is required, unless
     * you specify the {@code cisHome CIS home directory}, in which case
     * the file {@code cis/config/cisconfig.xml} in the CIS home directory
     * will be used as a default.
     * @parameter
     */
    private File cisConfigTargetFile;

    /**
     * The configuration changes, which are being applied.
     *
     * @parameter
     * @required
     */
    private CisConfig cisConfig;

    /**
     * Returns the {@code cisconfig.xml} file. If this is configured, then it will
     * be taken as the transformations source file.
     */
    protected File getCisConfigSourceFile() {
        return cisConfigSourceFile;
    }

    /**
     * Returns the created {@code cisconfig.xml} file. This is required, unless
     * you specify the {@code cisHome CIS home directory}, in which case
     * the file {@code cis/config/cisconfig.xml} in the CIS home directory
     * will be used as a default.
     */
    protected File getCisConfigTargetFile() {
        return cisConfigTargetFile;
    }


    /**
     * Returns the file, which is being used as the source file.
     * <ul>
     *   <li>If the {@link #cisConfigSourceFile} is specified,
     *     returns this file.</li>
     *   <li>If the {@link #cisWebappArtifact} is specified,
     *     ensures that this artifact is present in the local
     *     repository and uses the specified war file.</li>
     *   <li>Otherwise, searches for a CIS jar artifact in the
     *     projects dependencies. If such an artifact is found,
     *     uses the artifacts group, name and version to derive
     *     a corresponding CIS war artifact. In that case,
     *     ensures that this artifact is present in the local
     *     repository and uses the the specified war file.</li>
     *   <li>Otherwise, aborts with an error message that no source
     *     file is found.</li>
     * </ul>
     */
    protected File getSourceFile()
            throws MojoExecutionException, MojoFailureException {
        if ( cisConfigSourceFile != null )
        {
            return cisConfigSourceFile;
        }
        return getCisWebappFile();
    }

    private File getTargetFile()
        throws MojoFailureException
    {
        if ( cisConfigTargetFile == null )
        {
            File cisHome = getCisHomeDir();
            if ( cisHome == null )
            {
                throw new MojoFailureException( "Unable to determine a target file. "
                                                + " Use either of the parameters "
                                                + " cisConfigTargetFile or"
                                                + " cisHomeDir to configure it." );
            }
            cisConfigTargetFile = new File( new File( new File( cisHome, "cis" ), "config"), "cisconfig.xml" );
        }
        return cisConfigTargetFile;
    }

    private Result getResult()
    {
        return new StreamResult( cisConfigTargetFile );
    }

    private InputSource getSource( File sourceFile )
        throws MojoFailureException, MojoExecutionException
    {
        try
        {
            final InputStream istream;
            final String systemId;
            if ( cisConfigSourceFile == null )
            {
                final ZipFile zipFile = new ZipFile( sourceFile );
                final ZipEntry zipEntry = zipFile.getEntry( "cis/config/cisconfig.xml" );
                systemId = "jar:" + sourceFile.toURI().toURL().toExternalForm()
                    + "!" + zipEntry.getName();
                istream = zipFile.getInputStream( zipEntry );
            }
            else
            {
                systemId = sourceFile.toURI().toURL().toExternalForm();
                istream = new FileInputStream( sourceFile );
            }
            final InputSource isource = new InputSource( istream );
            isource.setSystemId( systemId );
            return isource;
        }
        catch ( FileNotFoundException e )
        {
            throw new MojoFailureException( "The source file " + sourceFile.getPath()
                                            + " does not exist or could not be opened.", e );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to open the source file "
                                              + sourceFile.getPath() + ": "
                                              + e.getMessage(), e );
        }
    }

    private void close( InputSource pSource )
        throws IOException
    {
        InputStream is = pSource.getByteStream();
        if ( is != null )
        {
            pSource.setByteStream( null );
            is.close();
        }
    }

    private void close( Result pResult )
        throws IOException
    {
        if ( pResult instanceof StreamResult )
        {
            StreamResult sr = (StreamResult) pResult;
            final OutputStream os = sr.getOutputStream();
            if ( os != null )
            {
                sr.setOutputStream( null );
                os.close();
            }
            final Writer w = sr.getWriter();
            if ( w != null )
            {
                sr.setWriter( null );
                w.close();
            }
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        final File sourceFile = getSourceFile();
        final File targetFile = getTargetFile();
        final CisUtils cisUtils = newCisUtils();
        final CisUtils.Resource sourceResource = new DefaultResource( sourceFile );
        final CisUtils.Resource targetResource = new DefaultResource( targetFile );
        final boolean uptodate;
        InputSource source = null;
        Result result = null;
        try
        {
            uptodate = cisUtils.isUpToDate( sourceResource, targetResource, true );
            if ( uptodate )
            {
                getLog().debug( "Target file " + targetFile.getPath()
                        + " is uptodate." );
            }
            else
            {
                cisUtils.makeDirOf( targetFile );
                source = getSource( sourceFile );
                result = getResult();
                final CisConfigTransformer cisConfigTransformer = new CisConfigTransformer();
                cisConfigTransformer.setCisConfig( cisConfig );
                cisConfigTransformer.transform( source, result );
                close( source );
                source = null;
                close( result );
                result = null;
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Failed to transform cisconfig.xml: "
                    + e.getMessage(), e );
        }
        catch ( CisCoreException e )
        {
            throw new MojoExecutionException( "Failed to transform cisconfig.xml: "
                    + e.getMessage(), e );
        }
        finally
        {
            if ( source != null )
            {
                try
                {
                    close( source );
                }
                catch ( Throwable t )
                {
                    /* Ignore me */
                }
            }
            if ( result != null )
            {
                try
                {
                    close( result );
                }
                catch ( Throwable t )
                {
                    /* Ignore me */
                }
            }
        }
    }
}
