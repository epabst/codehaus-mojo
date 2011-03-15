package org.codehaus.mojo.pde.descriptor;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

import org.codehaus.plexus.util.ReaderFactory;

/**
 * MANIFEST.MF java object
 */
public class ManifestBean
{

    /**
     * Manifest id.
     */
    private String id;

    /**
     * Manifest version.
     */
    private String version;

    /**
     * Create ManifestBean
     * 
     * @param dir pde root directory for locating META-INF/MANIFEST.MF
     */
    public ManifestBean( File dir )
    {
        this.load( dir );
    }

    /**
     * Load the META-INF/MANIFEST.MF.
     * 
     * @param dir pde root directory for locating META-INF/MANIFEST.MF
     */
    private void load( File dir )
    {
        BufferedReader reader;
        try
        {
            reader = new BufferedReader( ReaderFactory.newReader( new File( dir, "META-INF/MANIFEST.MF" ), "UTF-8" ) );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        // load the entired file into a array of lines
        ArrayList lines = new ArrayList();
        try
        {
            String line;
            do
            {
                line = reader.readLine();
                if ( line != null )
                {
                    lines.add( line );
                }
            }
            while ( line != null );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        this.id = this.getValue( "Bundle-SymbolicName:", lines );

        this.version = this.getValue( "Bundle-Version:", lines );
    }

    /**
     * Read key,value out of the META-INF/MANIFEST.MF
     * 
     * @param key the key to look for
     * @param lines the META-INF/MANIFEST.MF as an array of strings
     * @return null if the key is not found, otherwise the trimmed value from the
     *         META-INF/MANIFEST.MF
     */
    private String getValue( String key, ArrayList lines )
    {
        String retValue = null;

        for ( int i = 0; i < lines.size(); ++i )
        {
            String line = lines.get( i ).toString();

            if ( line.startsWith( key ) )
            {
                retValue = line.substring( key.length() );

                if ( retValue.indexOf( ";" ) != -1 )
                {
                    retValue = retValue.substring( 0, retValue.indexOf( ";" ) );
                }

            }
        }

        if ( retValue != null )
        {
            retValue = retValue.trim();
        }

        return retValue;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param theId the id to set
     */
    public void setId( String theId )
    {
        this.id = theId;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param theVersion the version to set
     */
    public void setVersion( String theVersion )
    {
        this.version = theVersion;
    }

}
