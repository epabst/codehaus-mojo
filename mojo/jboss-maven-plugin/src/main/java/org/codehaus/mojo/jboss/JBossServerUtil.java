package org.codehaus.mojo.jboss;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Provides generic methods supporting plugin functionality
 * 
 * @author pgier
 */
public class JBossServerUtil
{
    
    /**
     * Dump output coming from the stream
     * 
     * @param input The input stream to dump
     */
    public static void dump( final InputStream input )
    {
        final int streamBufferSize = 1000;
        new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
                    byte[] b = new byte[streamBufferSize];
                    while ( ( input.read( b ) ) != -1 )
                    {
                    }
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        } ).start();
    }

    /**
     * Create a policyFile that will allow the plugin to execute remote RMI code.
     * 
     * @param policyFile
     * @throws IOException
     */
    public static void writeSecurityPolicy( File policyFile )
        throws IOException
    {
        PrintWriter writer = new PrintWriter( new FileWriter( policyFile ) );
        writer.println( "grant {" );
        writer.println( "    permission java.security.AllPermission;" );
        writer.println( "};" );
        writer.close();
    }

}
