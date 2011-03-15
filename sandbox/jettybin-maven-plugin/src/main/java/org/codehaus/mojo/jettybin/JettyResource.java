package org.codehaus.mojo.jettybin;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.digest.DigesterException;
import org.codehaus.plexus.digest.Sha1Digester;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JettyResource - a reference to a jetty resource. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JettyResource
{
    private String expectedSha1;

    private String name;

    private URL resourceUrl;

    private String actualSha1;

    public static JettyResource parseRawJettyResourceLine( String rawline )
    {
        Pattern pat = Pattern.compile( "([0-9a-fA-F]{40})  (.*)" );
        Matcher mat = pat.matcher( rawline );
        if ( !mat.lookingAt() )
        {
            throw new IllegalArgumentException( "Unexpected jetty-file.sha1 line format on [" + rawline + "]" );
        }

        String sha1 = mat.group( 1 );
        String filename = mat.group( 2 );

        return new JettyResource( sha1, filename );
    }

    public JettyResource( String expectedSha1, String resourceName )
    {
        this.expectedSha1 = expectedSha1;
        this.name = resourceName;

        // cleanup junk from find command.
        if ( this.name.startsWith( "./" ) )
        {
            this.name = this.name.substring( 1 );
        }

        // ensure starts with '/' character so that resource can be found.
        if ( !this.name.startsWith( "/" ) )
        {
            this.name = "/" + this.name;
        }

        resourceUrl = this.getClass().getResource( name );
    }

    public String getActualSha1()
    {
        return actualSha1;
    }

    public void setActualSha1( String actualSha1 )
    {
        this.actualSha1 = actualSha1;
    }

    public String getExpectedSha1()
    {
        return expectedSha1;
    }

    public String getName()
    {
        return name;
    }

    public URL getResourceUrl()
    {
        return resourceUrl;
    }

    public void copyTo( File destDir, Sha1Digester digest )
        throws IOException
    {
        // Strip first path entry for output file.
        int pathIdx = this.name.indexOf( "/", 1 );

        File outputFile = new File( destDir, this.name.substring( pathIdx ) );

        JettyIOUtil.ensureParentDirectoryExists( outputFile );

        // Copy the resource to the file.
        if ( this.resourceUrl == null )
        {
            throw new IOException( "Unable to find resource " + this.name );
        }

        JettyIOUtil.copyResourceToFile( resourceUrl, outputFile );

        // Now perform an sha1 test against the written file.
        try
        {
            this.actualSha1 = digest.calc( outputFile );

            if ( !StringUtils.equalsIgnoreCase( this.expectedSha1, this.actualSha1 ) )
            {
                throw new IOException( "Failed to copy file " + outputFile.getAbsolutePath() + ", expected sha1 <"
                    + this.expectedSha1 + ">, actual <" + this.actualSha1 + ">" );
            }
        }
        catch ( DigesterException e )
        {
            throw new IOException( "Unable to process sha1 hashcode for file " + outputFile.getAbsolutePath() + " : "
                + e.getMessage() );
        }
    }

}
