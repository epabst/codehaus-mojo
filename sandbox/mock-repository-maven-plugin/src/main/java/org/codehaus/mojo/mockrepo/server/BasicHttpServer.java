package org.codehaus.mojo.mockrepo.server;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class BasicHttpServer
    implements Runnable
{
    private final ExecutorService executorService = Executors.newCachedThreadPool( new ThreadFactory()
    {
        private final ThreadFactory delegate = Executors.defaultThreadFactory();

        public Thread newThread( Runnable runnable )
        {
            Thread thread = delegate.newThread( runnable );
            thread.setDaemon( true );
            thread.setName( "mock-repo-" + thread.getName() );
            return thread;
        }
    } );

    private final Log log;

    private final int port;

    /* the web server's virtual root */
    private final HttpSite site;

    /* timeout on client connections */
    private static int timeout = 5000;

    private boolean shutdownRequested = false;

    private boolean shutdownCompleted = false;

    private int boundPort = 0;

    private final Object lock = new Object();

    public BasicHttpServer( Log log, int port, HttpSite site )
    {
        this.log = log;
        this.port = port;
        this.site = site;
    }

    public void start()
        throws InterruptedException
    {
        executorService.submit( this );
        synchronized ( lock )
        {
            while ( boundPort == 0 )
            {
                lock.wait( timeout );
            }
        }
    }

    public void stop()
    {
        synchronized ( lock )
        {
            shutdownRequested = true;
            long giveUp = System.currentTimeMillis() + timeout * 2;
            while ( !shutdownCompleted && System.currentTimeMillis() < giveUp )
            {
                try
                {
                    lock.wait( giveUp - System.currentTimeMillis() );
                }
                catch ( InterruptedException e )
                {
                    // ignore
                }
            }
        }
    }

    public void run()
    {
        try
        {
            ServerSocket ss = new ServerSocket( port );
            synchronized ( lock )
            {
                boundPort = ss.getLocalPort();
                lock.notify();
            }
            try
            {
                ss.setSoTimeout( timeout );
                while ( ss.isBound() && !shutdownRequested )
                {
                    try
                    {
                        Socket s = ss.accept();
                        s.setSoTimeout( timeout );
                        executorService.submit( new BasicHttpWorker( s, site, log ) );
                    }
                    catch ( SocketTimeoutException e )
                    {
                        // ignore
                    }
                }
            }
            finally
            {
                ss.close();
                synchronized ( lock )
                {
                    boundPort = 0;
                }
            }
        }
        catch ( IOException e )
        {
            log.error( e );
        }
        finally
        {
            synchronized ( lock )
            {
                shutdownCompleted = true;
                lock.notifyAll();
            }
            executorService.shutdown();
        }
    }

    public int getBoundPort()
    {
        synchronized ( lock )
        {
            return boundPort;
        }
    }

    public void join()
        throws InterruptedException
    {
        synchronized ( lock )
        {
            while ( !shutdownCompleted )
            {
                lock.wait();
            }
        }
    }

}