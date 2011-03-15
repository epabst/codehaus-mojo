package org.apache.maven.wagon.providers.cifs;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import org.apache.maven.wagon.WagonTestCase;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

import java.io.IOException;
import java.net.Socket;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class CifsWagonTest
    extends WagonTestCase
{
    // ----------------------------------------------------------------------
    // Test Settings.
    //
    // Adjust these to match your environment when testing.
    // ----------------------------------------------------------------------

    private static String HOST = "192.168.1.15";

    private static int PORT = 445; // microsoft-ds    445/tcp                         # Microsoft Naked

    private static String USERNAME = "tom";

    private static String PASSWORD = "tom";

    protected AuthenticationInfo getAuthInfo()
    {
        AuthenticationInfo authenticationInfo = new AuthenticationInfo();

        authenticationInfo.setUserName( USERNAME );
        authenticationInfo.setPassword( PASSWORD );

        return authenticationInfo;
    }

    protected String getProtocol()
    {
        return "cifs";
    }

    protected String getTestRepositoryUrl()
        throws IOException
    {
        return "smb://" + HOST + "/data1/maven-repo/foo/bar";
    }

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public void testWagon()
        throws java.lang.Exception
    {
        if ( !isOnline() )
        {
            return;
        }

        super.testWagon();
    }

    public void testWagonPutDirectory()
        throws java.lang.Exception
    {
        if ( !isOnline() )
        {
            return;
        }

        super.testWagonPutDirectory();
    }

    public void testFailedGet()
        throws java.lang.Exception
    {
        if ( !isOnline() )
        {
            return;
        }

        super.testFailedGet();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Tests to see if the host that we're testing against actually is up.
     */
    public static boolean isOnline()
    {
        try
        {
            Socket socket = new Socket( HOST, PORT );

            socket.getOutputStream();

            socket.close();

            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }
}
