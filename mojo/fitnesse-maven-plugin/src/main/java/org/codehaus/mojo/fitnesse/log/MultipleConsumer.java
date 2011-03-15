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

import org.codehaus.plexus.util.cli.StreamConsumer;

public class MultipleConsumer
    implements FitnesseStreamConsumer
{
    LogConsumer mLog;

    FileConsumer mFile;

    private boolean mHasGeneratedResultFile = false;

    public MultipleConsumer( LogConsumer pLog, FileConsumer pFile )
    {
        super();
        mLog = pLog;
        mFile = pFile;
    }

    public void consumeLine( String pMessage )
    {
        mLog.consumeLine( pMessage );
        mFile.consumeLine( pMessage );
        mHasGeneratedResultFile = mHasGeneratedResultFile || pMessage.startsWith( "Formatting as html" );
    }

    public boolean hasGeneratedResultFile()
    {
        return mHasGeneratedResultFile;
    }

    public StreamConsumer getLogConsumer()
    {
        return mLog;
    }

    public FileConsumer getFileConsumer()
    {
        return mFile;
    }

}
