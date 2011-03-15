package org.codehaus.mojo.cis.core;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.mojo.cis.core.CisUtils.Resource;

/**
 * Default implementation of {@link CisUtils.Resource}.
 */
public class DefaultResource implements Resource {
    private boolean initialized;
    private long modificationDate;
    private final File f;
    private final URL u;

    /**
     * Creates a new instance with the given files
     * modification date.
     */
    public DefaultResource(File pFile) {
        if ( pFile == null )
        {
            throw new IllegalArgumentException( "The resource file must not be null." );
        }
        f = pFile;
        u = null;
    }

    /**
     * Creates a new instance with the given URL's
     * modification date.
     */
    public DefaultResource(URL pURL) {
        if ( pURL == null )
        {
            throw new IllegalArgumentException( "The resource URL must not be null." );
        }
        f = null;
        u = pURL;
    }

    /**
     * Creates a new instance with the given modification date.
     */
    public DefaultResource(long pModificationDate) {
        f = null;
        u = null;
        modificationDate = pModificationDate;
        initialized = true;
    }

    public long getModificationDate()
        throws IOException
    {
        if ( !initialized )
        {
            if ( f != null )
            {
                modificationDate = f.lastModified();
                if ( modificationDate == 0 )
                {
                    modificationDate = -1;
                }
            }
            else if ( u != null )
            {
                final URLConnection uc = u.openConnection();
                if ( uc instanceof HttpURLConnection )
                {
                    modificationDate = ((HttpURLConnection) uc).getLastModified();
                    if ( modificationDate == 0 )
                    {
                        modificationDate = -1;
                    }
                }
                else
                {
                    modificationDate = -1;
                }
            }
            else
            {
                throw new IllegalStateException( "Don't know how to get a modification date. " );
            }
            initialized = true;
        }
        return modificationDate;
    }
}