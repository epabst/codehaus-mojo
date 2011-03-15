package org.codehaus.mojo.fitnesse.runner;

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

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.fitnesse.ClassPathSubstitution;

import fitnesse.components.FitProtocol;
import fitnesse.util.StreamReader;

public class ClassPathBuilder
{

    private String mHostName;

    private int mPort;

    private String mPage;

    private Log mLog;

    ClassPathBuilder()
    {
    }

    ClassPathBuilder( Log pLog )
    {
        mLog = pLog;
    }

    public ClassPathBuilder( String hostName, int port, String page, Log pLog )
    {
        super();
        mHostName = hostName;
        mPort = port;
        mPage = page;
        mLog = pLog;
    }

    public String getPath( List pSubstitutions, Log pLog )
        throws MojoExecutionException
    {
        String tOriginalPath;
        String tUrl = "GET /" + mPage + "?responder=fitClient&includePaths=yes HTTP/1.1\r\n\r\n";
        pLog.debug( "Use URL for classPath download [" + tUrl + "]" );
        try
        {
            StreamReader tSocketReader = establishConnection( tUrl );
            int tNbBytes = FitProtocol.readSize( tSocketReader );
            if ( tNbBytes != 0 )
            {
                throw new MojoExecutionException( "Unable to connect to server." );
            }
            tNbBytes = FitProtocol.readSize( tSocketReader );
            tOriginalPath = FitProtocol.readDocument( tSocketReader, tNbBytes );
            pLog.debug( "Download classpath is [" + tOriginalPath + "]" );
            String tPath = transformPath( tOriginalPath, pSubstitutions );
            pLog.info( "Use path [" + tPath + "]" );
            return tPath;
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Unable to download path from FitNesse Server", e );
        }
    }

    private StreamReader establishConnection( String pUrl )
        throws UnknownHostException, IOException
    {
        Socket socket = new Socket( mHostName, mPort );
        OutputStream socketOutput = socket.getOutputStream();
        StreamReader socketReader = new StreamReader( socket.getInputStream() );
        byte[] tBytes = pUrl.getBytes( "UTF-8" );
        socketOutput.write( tBytes );
        socketOutput.flush();
        return socketReader;
    }

    String transformPath( String pOriginalPath, List pSubstitutions )
    {
        String curPath = pOriginalPath;
        ClassPathSubstitution curSub;
        for ( Iterator tIt = pSubstitutions.iterator(); tIt.hasNext(); )
        {
            curSub = (ClassPathSubstitution) tIt.next();
            mLog.debug( "use subtitution [" + curSub.getSearch() + "=" + curSub.getReplaceWith() );
            curPath = replaceAll( curPath, curSub.getSearch(), curSub.getReplaceWith() );
        }
        curPath = curPath.replaceAll( " +\";", "\";" );
        curPath = curPath.replaceAll( ";\" +", ";\"" );
        curPath = curPath.replaceAll( "\"", "" );
        //        
        // curPath = curPath.replaceAll( " +;", ";" );
        // curPath = curPath.replaceAll( "; +", ";" );
        // curPath = curPath.replaceAll( "\" +;", "\";" );
        // curPath = curPath.replaceAll( ";\" +", ";\"" );
        //        
        return curPath;
    }

    String replaceAll( String pPath, String pKey, String pValue )
    {
        StringBuffer tempNewPath = new StringBuffer();
        int curStart = 0;
        int curEnd = pPath.indexOf( pKey );
        while ( curEnd != -1 )
        {
            tempNewPath.append( pPath.substring( curStart, curEnd ) );
            tempNewPath.append( pValue );
            curStart = curEnd + pKey.length();
            curEnd = pPath.indexOf( pKey, curStart );
        }
        tempNewPath.append( pPath.substring( curStart, pPath.length() ) );
        return tempNewPath.toString();
    }
}
