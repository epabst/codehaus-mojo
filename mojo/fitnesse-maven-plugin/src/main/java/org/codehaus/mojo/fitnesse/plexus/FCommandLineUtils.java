package org.codehaus.mojo.fitnesse.plexus;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.InputStream;

import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamFeeder;
import org.codehaus.plexus.util.cli.StreamPumper;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id$
 */
public abstract class FCommandLineUtils
{
    public static class StringStreamConsumer
        implements StreamConsumer
    {
        private StringBuffer string = new StringBuffer();

        private String ls = System.getProperty( "line.separator" );

        public void consumeLine( String line )
        {
            string.append( line + ls );
        }

        public String getOutput()
        {
            return string.toString();
        }
    }

    public static int executeCommandLine( FCommandline cl, StreamConsumer systemOut, StreamConsumer systemErr )
        throws FCommandLineException
    {
        return executeCommandLine( cl, null, systemOut, systemErr );
    }

    public static int executeCommandLine( FCommandline cl, InputStream systemIn, StreamConsumer systemOut,
                                          StreamConsumer systemErr )
        throws FCommandLineException
    {
        if ( cl == null )
        {
            throw new IllegalArgumentException( "cl cannot be null." );
        }

        Process p = null;

        p = cl.execute();
        Thread tKiller = new KillerThread( p );
        Runtime.getRuntime().addShutdownHook( tKiller );
        StreamFeeder inputFeeder = null;

        if ( systemIn != null )
        {
            inputFeeder = new StreamFeeder( systemIn, p.getOutputStream() );
        }

        StreamPumper outputPumper = new StreamPumper( p.getInputStream(), systemOut );

        StreamPumper errorPumper = new StreamPumper( p.getErrorStream(), systemErr );

        if ( inputFeeder != null )
        {
            inputFeeder.start();
        }

        outputPumper.start();

        errorPumper.start();

        try
        {
            int returnValue = p.waitFor();
            if ( inputFeeder != null )
            {
                synchronized ( inputFeeder )
                {
                    if ( !inputFeeder.isDone() )
                    {
                        inputFeeder.wait();
                    }
                }
            }

            if ( outputPumper != null )
            {
                synchronized ( outputPumper )
                {
                    if ( !outputPumper.isDone() )
                    {
                        outputPumper.wait();
                    }
                }
            }

            if ( errorPumper != null )
            {
                synchronized ( errorPumper )
                {
                    if ( !errorPumper.isDone() )
                    {
                        errorPumper.wait();
                    }
                }
            }
            Runtime.getRuntime().removeShutdownHook( tKiller );

            return returnValue;
        }
        catch ( InterruptedException ex )
        {
            throw new FCommandLineException( "Error while executing external command.", ex );
        }
        finally
        {
            if ( inputFeeder != null )
            {
                inputFeeder.close();
            }

            outputPumper.close();

            errorPumper.close();
        }
    }

    public static class KillerThread
        extends Thread
    {
        public KillerThread( Process pProcess )
        {
            mProcessToKill = pProcess;
        }

        Process mProcessToKill;

        public void run()
        {
            super.run();
            if ( mProcessToKill != null )
            {
                System.err.println( "Sub process has been closed by destroy()" );
                System.err.flush();
                mProcessToKill.destroy();
            }
            else
            {
                System.err.println( "Should kill Sub process but was null" );
                System.err.flush();
            }
        }

    }
}