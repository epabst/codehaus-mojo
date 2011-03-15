package org.codehaus.mojo.cis.core;

import java.io.File;
import java.io.IOException;



/**
 * Base implementation of {@link CisUtils} for deriving concrete
 * implementations.
 */
public abstract class AbstractCisUtils implements CisUtils
{
    private Long projectFileModified;

    public boolean isUpToDate( Resource pSourceFile, Resource pTargetFile )
        throws CisCoreException
    {
        return isUpToDate( pSourceFile, pTargetFile, false );
    }

    public boolean isUpToDate( Resource pSourceFile, Resource pTargetFile,
                               boolean pConsiderProjectFile )
        throws CisCoreException
    {
        debug("isUpToDate: -> " + pSourceFile + ", " + pTargetFile + ", " + pConsiderProjectFile);
        final long sourceModified;
        final long targetModified;
        try
        {
            sourceModified = pSourceFile.getModificationDate();
            if ( sourceModified == -1 )
            {
                debug("isUpToDate: <- false (sourceModified = -1)");
                return false;
            }
            targetModified = pTargetFile.getModificationDate();
            if ( targetModified == -1 )
            {
                debug("isUpToDate: <- false (targetModified = -1)");
                return false;
            }
        }
        catch (IOException e)
        {
            throw new CisCoreException( e );
        }
        if ( sourceModified >= targetModified )
        {
            debug("isUpToDate: <- false (sourceModified >= targetModified)");
            return false;
        }
        if ( !pConsiderProjectFile )
        {
            debug("isUpToDate: <- true (!considerProjectFile)");
            return true;
        }
        final File f = getProjectFile();
        if ( f == null )
        {
            debug("isUpToDate: <- true (projectFile = null)");
            return true;
        }
        if ( projectFileModified == null )
        {
            projectFileModified = new Long( f.lastModified() );
        }
        long l = projectFileModified.longValue();
        if ( l == -1 )
        {
            debug("isUpToDate: <- false (projectFileModified = -1)");
            return false;
        }
        final boolean result = l < targetModified;
        debug("isUpToDate: <- " + result);
        return result;
    }

    public void makeDirOf( File pTargetFile )
        throws CisCoreException
    {
        final File dir = pTargetFile.getParentFile();
        if ( dir != null  &&  !dir.isDirectory()  &&  !dir.mkdirs() )
        {
            throw new CisCoreException( "Failed to create directory "
                                        + dir.getPath() );
        }
    }
}
