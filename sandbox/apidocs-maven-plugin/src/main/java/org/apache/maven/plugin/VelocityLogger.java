package org.apache.maven.plugin;

/*
 * LICENSE
 */

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

public class VelocityLogger
    implements LogSystem
{
    public VelocityLogger()
    {
    }

    public void init( RuntimeServices runtimeServices )
    {
    }

    /**
     * logs messages
     *
     * @param level   severity level
     * @param message complete error message
     */
    public void logVelocityMessage( int level, String message )
    {
        // TODO: set the velocity log level
//        System.out.println( message );
    }
}
