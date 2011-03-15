package org.codehaus.mojo.cis.ant;

import java.io.File;

import org.apache.tools.ant.Task;
import org.codehaus.mojo.cis.core.CisUtils;


/**
 * Abstract base class for deriving cis related Ant tasks.
 */
public abstract class AbstractCisTask extends Task
{
    private File cisHomeDir;

    /**
     * Returns the web application directory.
     */
    public File getCisHomeDir()
    {
        return cisHomeDir;
    }

    /**
     * Sets the web application directory.
     */
    public void setCisHomeDir( File pCisHomeDir )
    {
        cisHomeDir = pCisHomeDir;
    }

    /**
     * Creates a new instance of {@link CisUtils}.
     */
    protected CisUtils newCisUtils()
    {
        return new AntCisUtils(this);
    }
}
