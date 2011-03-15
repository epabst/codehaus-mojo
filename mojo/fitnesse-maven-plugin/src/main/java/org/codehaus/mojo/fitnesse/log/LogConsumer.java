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

import java.util.logging.Level;

import org.apache.maven.plugin.logging.Log;

public class LogConsumer
    implements FitnesseStreamConsumer
{

    private Log mLog;

    private Level mLevel;

    private boolean mHasGeneratedResultFile = false;

    /** Only for test. */
    public LogConsumer()
    {
    }

    public LogConsumer( Log pLog, Level pLevel )
    {
        super();
        mLog = pLog;
        mLevel = pLevel;
    }

    public void consumeLine( String pMessage )
    {
        if ( Level.INFO.equals( mLevel ) )
        {
            mLog.info( pMessage );
        }
        else
        {
            mLog.error( pMessage );
        }
        mHasGeneratedResultFile = mHasGeneratedResultFile || pMessage.startsWith( "Formatting as html" );

    }

    public boolean hasGeneratedResultFile()
    {
        return mHasGeneratedResultFile;
    }

    public Level getLevel()
    {
        return mLevel;
    }

}
