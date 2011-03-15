package org.codehaus.mojo.runtime.builder;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractMavenRuntimeBuilder
    implements MavenRuntimeBuilder
{
    protected static void mkdir( File dir )
        throws MavenRuntimeBuilderException
    {
        if ( !dir.exists() && !dir.mkdirs() )
        {
            throw new MavenRuntimeBuilderException( "Error creating directory: " + dir.getAbsolutePath() );
        }
    }
}
