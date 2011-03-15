package org.codehaus.mojo.fitnesse.log;

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

import junit.framework.TestCase;

public class FileConsumerTest
    extends TestCase
{

    public void testConsumeLine()
    {
        File tTmpFile = new File( "target/tmpFile.txt" );
        if ( tTmpFile.exists() )
        {
            tTmpFile.delete();
        }
        FileConsumer tConsumer = new FileConsumer( tTmpFile );
        tConsumer.consumeLine( "AaAaAa" );
        tConsumer.consumeLine( "Bababa" );

        tConsumer.close();
        tTmpFile = new File( "target/tmpFile.txt" );

        long tSize = tTmpFile.length();
        assertTrue( "File lenght should be at least 12, but was only [" + tSize + "]", tSize > 12 );
    }

}
