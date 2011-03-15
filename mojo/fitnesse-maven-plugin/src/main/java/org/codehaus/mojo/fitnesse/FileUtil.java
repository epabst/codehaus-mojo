package org.codehaus.mojo.fitnesse;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtil
{

    private FileUtil()
    {
        super();
    }

    public static String getString( File pIn )
        throws IOException
    {

        FileInputStream tFileInputStream = null;
        try
        {
            tFileInputStream = new FileInputStream( pIn );
            String tResult = getString( tFileInputStream );
            return tResult;
        }
        finally
        {
            if ( tFileInputStream != null )
            {
                tFileInputStream.close();
            }
        }
    }

    public static String getString( InputStream pIn )
        throws IOException
    {
        StringBuffer tBuf = new StringBuffer();
        byte[] tbytes = new byte[512];
        int tReadBytes = pIn.read( tbytes );
        while ( tReadBytes >= 0 )
        {
            tBuf.append( new String( tbytes, 0, tReadBytes, "UTF-8" ) );
            tReadBytes = pIn.read( tbytes );
        }
        return tBuf.toString();
    }

}
