package org.codehaus.mojo.keytool;

/*
 * Copyright 2005-2008 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License" );
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.apache.maven.plugin.logging.Log;

public class KeyToolMojoSupport
{

    /**
     * Constructs the operating system specific File path of the JDK command given the specified command name.
     * @param command the executable name 
     * @return a File representing the path to the command.
     */
    public static File getJDKCommandExe( String command )
    {
        String fullCommand = command + ( SystemUtils.IS_OS_WINDOWS ? ".exe" : "" );

        File exe;

        // For IBM's JDK 1.2
        if ( SystemUtils.IS_OS_AIX )
        {
            exe = new File( SystemUtils.getJavaHome() + "/../sh", fullCommand );
        }
        else if ( SystemUtils.IS_OS_MAC_OSX ) // what about IS_OS_MAC_OS ??
        {
            exe = new File( SystemUtils.getJavaHome() + "/bin", fullCommand );
        }
        else
        {
            exe = new File( SystemUtils.getJavaHome() + "/../bin", fullCommand );
        }

        return exe;
    }

    /**
     * Constructs the operating system specific absolute path of the JDK command given the specified command name.
     * @param command the executable name 
     * @return a String representing the absolute path to the command.
     */
    public static String getJDKCommandPath( String command, Log logger )
    {
        String path = getJDKCommandExe( command ).getAbsolutePath();
        logger.debug( command + " executable=[" + path + "]" );
        return path;
    }

    /**
     * Constructs the operating system specific File path of the JRE cacerts file.
     * @param command the executable name 
     * @return a File representing the path to the command.
     */
    public static File getJRECACerts()
    {

        File cacertsFile = null;

        String cacertsFilepath = "lib/security/cacerts";

        // For IBM's JDK 1.2
        if ( SystemUtils.IS_OS_AIX )
        {
            cacertsFile = new File( SystemUtils.getJavaHome() + "/", cacertsFilepath );
        }
        else if ( SystemUtils.IS_OS_MAC_OSX ) // what about IS_OS_MAC_OS ??
        {
            cacertsFile = new File( SystemUtils.getJavaHome() + "/", cacertsFilepath );
        }
        else
        {
            cacertsFile = new File( SystemUtils.getJavaHome() + "/", cacertsFilepath );
        }

        return cacertsFile;

    }

}
