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
import java.io.FileWriter;
import java.io.IOException;

public class FileConsumer
    implements FitnesseStreamConsumer
{

    FileWriter mOutputWriter;

    private boolean mHasGeneratedResultFile = false;

    private String mLineSep = System.getProperty( "line.separator" );

    /** Only for test. */
    public FileConsumer()
    {
    }

    public FileConsumer( File pOutputFile )
    {
        super();
        try
        {
            mOutputWriter = new FileWriter( pOutputFile );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Unable to write into file" );
        }
    }

    public synchronized void consumeLine( String pMessage )
    {
        try
        {
            mOutputWriter.write( pMessage + mLineSep );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Unable to write into file" );
        }
        mHasGeneratedResultFile = mHasGeneratedResultFile || pMessage.startsWith( "Formatting as html" );
    }

    public boolean hasGeneratedResultFile()
    {
        return mHasGeneratedResultFile;
    }

    public void close()
    {
        try
        {
            mOutputWriter.flush();
            mOutputWriter.close();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Unable to write into file" );
        }
    }

}
