package org.codehaus.mojo.cis.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.cis.core.AbstractCisUtils;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.codehaus.mojo.cis.core.CisUtils;
import org.codehaus.plexus.util.FileUtils;


/**
 * Maven specific implementation of {@link CisUtils}. Provides
 * logging and similar services.
 */
public class MavenCisUtils extends AbstractCisUtils
{
    private AbstractCisMojo mojo;
    private File tempDir;

    /**
     * Creates a new instance with the given Mojo. The Mojo is used
     * for logging and similar purposes.
     */
    public MavenCisUtils( AbstractCisMojo pMojo )
    {
        mojo = pMojo;
    }

    public void debug( String pMessage )
    {
        mojo.getLog().debug( pMessage );
    }

    public void info( String pMessage )
    {
        mojo.getLog().info( pMessage );
    }

    private File initTempDir()
    {
        final MavenProject project = mojo.getProject();
        if ( project == null )
        {
            return null;
        }
        final Build build = project.getBuild();
        if ( build == null )
        {
            return null;
        }
        final String dir = build.getDirectory();
        if ( dir == null )
        {
            return null;
        }
        return new File( new File( dir, "cis-maven-plugin"), "tmp" );
    }

    public File getTempDir()
    {
        if ( tempDir == null )
        {
            tempDir = initTempDir();
            if ( tempDir == null )
            {
                tempDir = new File( System.getProperty( "java.io.tmpdir" ) );
            }
        }
        return tempDir;
    }

    public void copy( File pSourceFile, File pTargetFile ) throws CisCoreException
    {
        try
        {
            FileUtils.copyFile( pSourceFile, pTargetFile );
        }
        catch ( IOException e )
        {
            throw new CisCoreException( "Copying source file " + pSourceFile.getPath()
                                        + " to target file " + pTargetFile.getPath()
                                        + " failed: " + e.getMessage(), e );
        }
    }

    public File getProjectFile()
    {
        return mojo.getProject().getFile();
    }

    public void touch( File pFile ) throws CisCoreException
    {
        if ( pFile.isFile() )
        {
            if ( !pFile.setLastModified( System.currentTimeMillis() ) )
            {
                throw new CisCoreException( "Failed to set modification time of file "
                                            + pFile.getPath() );
            }
        }
        else
        {
            makeDirOf( pFile );
            try
            {
                final FileOutputStream fos = new FileOutputStream( pFile );
                fos.close();
            }
            catch ( IOException e )
            {
                throw new CisCoreException( "Unable to create file " + pFile
                                            + ": " + e.getMessage(), e );
            }
        }
    }
}
