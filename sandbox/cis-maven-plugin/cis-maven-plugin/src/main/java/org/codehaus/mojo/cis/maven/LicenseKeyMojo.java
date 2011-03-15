package org.codehaus.mojo.cis.maven;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.cis.core.CisCoreErrorMessage;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.codehaus.mojo.cis.core.LicenseKeyBean;


/**
 * A mojo for copying the license key file.
 * 
 * @goal licenseKey
 * @phase generate-resources
 */
public class LicenseKeyMojo extends AbstractCisMojo {
    /**
     * Sets the license key file.
     *
     * @parameter expression="${cis.licenseKeyFile}" default-value="src/main/cis/license.xml"
     * @required
     */
    private File licenseKeyFile;

    /**
     * Sets the license keys target location. An attempt is made
     * to guess the target location, if this parameter isn't set.
     *
     * @parameter expression="${cis.targetFile}"
     */
    private File targetFile;

    private File findTargetFile()
        throws MojoFailureException, MojoExecutionException
    {
        if ( targetFile != null )
        {
            return targetFile;
        }
        final File cisHomeDir = getCisHomeDir();
        if ( cisHomeDir != null )
        {
            final Artifact cisJarFile = getCisJar( false );
            if ( cisJarFile != null )
            {
                final String version = cisJarFile.getVersion();
                if ( version != null  &&  version.length() > 0)
                {
                    final int offset1 = version.indexOf('.');
                    int offset2 = version.indexOf('.', offset1+1);
                    if ( offset2 == -1 )
                    {
                        offset2 = version.length();
                    }
                    if ( offset1 > 0  &&  offset2 > offset1)
                    {
                        final String name = "cit" + version.substring( 0, offset1 ) + version.substring( offset1+1, offset2 ) + ".xml";
                        return new File( new File( new File( cisHomeDir, "cis"), "licensekey" ), name );
                    }
                }
                throw new MojoFailureException( "Unable to derive the license files target location from the artifact "
                                                + cisJarFile.getGroupId() + ":" + cisJarFile.getArtifactId()
                                                + ":" + cisJarFile.getVersion() );
            }
        }
        throw new MojoFailureException( "Unable to guess the location of the license key file."
                                        + " Use the parameter targetFile." );
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if ( licenseKeyFile == null )
        {
            throw new MojoFailureException("The license key file is not set.");
        }
        final LicenseKeyBean bean = new LicenseKeyBean();
        bean.setCisHomeDir( getCisHomeDir() );
        bean.setCisUtils( newCisUtils() );
        bean.setCisMarkersDir( getCisMarkersDir() );
        bean.setLicenseFile( licenseKeyFile );
        bean.setTargetFile( findTargetFile() );
        try
        {
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
    }
}
