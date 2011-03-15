package org.codehaus.mojo.cis.core;

import java.io.File;


/**
 * Abstract base class for deriving cis related beans, which perform
 * particular tasks.
 */
public abstract class AbstractCisBean
{
    private CisUtils cisUtils;
    private File cisHomeDir;
    private File cisMarkersDir;

    /**
     * Returns the CIS utilities. These are used for dependency checks,
     * copying files, and similar stuff.
     */
    public CisUtils getCisUtils()
    {
        return cisUtils;
    }

    /**
     * Sets the CIS utilities. These are used for dependency checks,
     * copying files, and similar stuff.
     */
    public void setCisUtils( CisUtils pCisUtils )
    {
        cisUtils = pCisUtils;
    }

    /**
     * Returns the CIS home directory. This is the directory, where the
     * web application has been created. In particular, this directory
     * must contain a subdirectory WEB-INF/lib with the cis jar files.
     */
    public File getCisHomeDir()
    {
        return cisHomeDir;
    }

    /**
     * Sets the CIS home directory. This is the directory, where the
     * web application has been created. In particular, this directory
     * must contain a subdirectory WEB-INF/lib with the cis jar files.
     */
    public void setCisHomeDir( File pCisHomeDir )
    {
        cisHomeDir = pCisHomeDir;
    }

    /**
     * Returns the directory, which is being used for creating
     * marker files.
     */
    public File getCisMarkersDir()
    {
        return cisMarkersDir;
    }

    /**
     * Sets the directory, which is being used for creating
     * marker files.
     */
    public void setCisMarkersDir( File pCisMarkersDir )
    {
        cisMarkersDir = pCisMarkersDir;
    }

    /**
     * Checks, whether the value of {@link #getCisHomeDir()} is valid
     * and returns it.
     */
    protected File checkCisHomeDir() throws CisCoreErrorMessage
    {
        File cisHomeDirectory = getCisHomeDir();
        if ( cisHomeDirectory == null )
        {
            throw new CisCoreErrorMessage( "The CIS home directory is not set." );
        }
        if ( !cisHomeDirectory.isDirectory() )
        {
            throw new CisCoreErrorMessage( "The configured CIS home directory "
                                           + cisHomeDirectory
                                           + " does not exist or is not a directory." );
        }
        return cisHomeDirectory;
    }

    /**
     * Called to perform the actual action.
     */
    public abstract void execute() throws CisCoreException;
}
