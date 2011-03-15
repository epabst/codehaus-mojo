package org.codehaus.mojo.cruisecontrol;

/**
 * Copyright 2006 The Codehaus.
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

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * Helper class to write the different supported modificationsets.
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 *
 */
public class CruiseControlModificationSetConfigurer
{
    static final Map scmPrefixes;
    static
    {
        scmPrefixes = new HashMap();
        scmPrefixes.put( "svn", "scm:svn:" );
    }

    public static void addModificationSet( XMLWriter writer, MavenProject reactorProject )
    {
        writer.startElement( "modificationset" );
        {
            String scmUrl = reactorProject.getScm().getConnection();
            if ( scmUrl.contains( (String) scmPrefixes.get( "svn" ) ) )
            {
                addSvnModificationSet( writer, getRativeScmUrl( scmUrl, (String) scmPrefixes.get( "svn" ) ) );
            }

        }
        writer.endElement();

    }

    private static void addSvnModificationSet( XMLWriter writer, String svnUrl )
    {
        writer.startElement( "svn" );
        writer.addAttribute( "RepositoryLocation", svnUrl );
        writer.endElement();

    }

    private static String getRativeScmUrl( String scmUrl, String prefix )
    {
        return scmUrl.substring( prefix.length(), scmUrl.length() );
    }

}
