package org.codehaus.mojo.gae;

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


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.jvnet.animal_sniffer.Clazz;

/**
 * Creates an animal-sniffer SIG file for GAE using original Java6 signature and GAE white list
 * 
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 */
public class SigFileCreator
{
    private final Map<String, Clazz> java6 = new HashMap<String, Clazz>();

    private final Map<String, Clazz> gae = new HashMap<String, Clazz>();

    private void loadSig()
        throws Exception
    {
        try
        {
            ObjectInputStream ois =
                new ObjectInputStream( new GZIPInputStream( getClass().getResourceAsStream( "java1.6-1.0.sig.gz" ) ) );
            while ( true )
            {
                Clazz c = (Clazz) ois.readObject();
                if ( c == null )
                    break; // finished
                java6.put( c.name, c );
            }
        }
        catch ( ClassNotFoundException e )
        {
            throw new NoClassDefFoundError( e.getMessage() );
        }
    }

    private void applyWhiteList()
        throws Exception
    {
        BufferedReader reader =
            new BufferedReader( new InputStreamReader( getClass().getResourceAsStream( "whiteList-1.2.0.txt" ) ) );
        String line;
        while ( ( line = reader.readLine() ) != null )
        {
            line = line.replace( '.', '/' );
            Clazz clazz = java6.get( line );
            if (clazz == null)
            {
                System.err.println( "GAE white list refers to an unknown Java6 class " + line );
            }
            else
            {
                gae.put( line, clazz );
            }
        }

        ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( "signatures" ) );
        for ( Clazz clazz : gae.values() )
        {
            oos.writeObject( clazz );
        }
        oos.writeObject( null ); // EOF marker
        oos.close();
    }

    public static void main( String[] args )
        throws Exception
    {
        SigFileCreator creator = new SigFileCreator();
        creator.loadSig();
        creator.applyWhiteList();
    }

}
