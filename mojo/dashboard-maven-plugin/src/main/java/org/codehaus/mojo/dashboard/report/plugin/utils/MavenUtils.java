package org.codehaus.mojo.dashboard.report.plugin.utils;

/*
 * Copyright 2008 David Vicente
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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.interpolation.MapBasedValueSource;
import org.codehaus.plexus.util.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.util.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.util.xml.Xpp3Dom;


public class MavenUtils
{
    
    private static MavenUtils mavenUtils = null;
    
    /**
     * Creation forbidden...
     */
    private MavenUtils()
    {
        super();
        
    }
    
    public static MavenUtils getInstance()
    {
        if (mavenUtils == null){
            mavenUtils = new MavenUtils();
        }
        return mavenUtils;
    }
    
    /**
     * 
     * @param project
     * @param pluginArtifact
     * @param optionName
     * @param defaultValue
     * @return
     */
    public String getConfiguration( MavenProject project, String pluginArtifact, String pluginGroupId,
                                     String optionName, String defaultValue )
    {
        String result = null;
        String value = "";
        try
        {
            value = getMavenPluginConfiguration( project, pluginArtifact, pluginGroupId, optionName, "" );
            if ( value != null && value.length() > 0 )
            {
                if ( value.indexOf( "$" ) > -1 )
                {
                    result = getInterpolatorValue( project, value );
                }
                else
                {

                    File dir = new File( value );
                    boolean isExists = dir.exists();
                    if ( !isExists )
                    {
                        File resultFile = FileUtils.resolveFile( project.getBasedir(), value );
                        result = resultFile.getAbsolutePath();
                    }
                    else
                    {
                        result = value;
                    }
                }
            }
            else
            {
                result = getInterpolatorValue( project, defaultValue );
            }
        }
        catch ( IOException e )
        {
            result = null;
        }
        return result;
    }

    /**
     * 
     * @param project
     * @param value
     * @return
     */
    private String getInterpolatorValue( MavenProject project, String value )
    {

        RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
        interpolator.addValueSource( new ObjectBasedValueSource( project ) );
        interpolator.addValueSource( new MapBasedValueSource( project.getProperties() ) );

        String result = interpolator.interpolate( value, "project" );

        return result;
    }

    /**
     * Return the optionName value defined in a project for a given artifactId plugin.
     * 
     * @param project
     *            not null
     * @param pluginArtifact
     *            not null
     * @param optionName
     *            an Xpath expression from the plugin <code>&lt;configuration/&gt;</code>
     * @param defaultValue
     * @return the value for the option name (comma separated value in the case of list) or null if not found
     * @throws IOException
     *             if any
     */
    private String getMavenPluginConfiguration( MavenProject project, String pluginArtifact,
                                                       String pluginGroupId, String optionName, String defaultValue )
        throws IOException
    {
        for ( Iterator it = project.getModel().getBuild().getPlugins().iterator(); it.hasNext(); )
        {
            Plugin plugin = (Plugin) it.next();

            if ( ( plugin.getGroupId().equals( pluginGroupId ) ) && ( plugin.getArtifactId().equals( pluginArtifact ) ) )
            {
                Xpp3Dom pluginConf = (Xpp3Dom) plugin.getConfiguration();

                if ( pluginConf != null )
                {
                    StringBuffer sb = new StringBuffer();
                    try
                    {
                        Document doc =
                            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                                                                                             new StringInputStream(
                                                                                                                    pluginConf.toString() ) );

                        XObject obj = XPathAPI.eval( doc, "//configuration//" + optionName );

                        if ( StringUtils.isNotEmpty( obj.toString() ) )
                        {
                            StringTokenizer token = new StringTokenizer( obj.toString(), "\n " );
                            while ( token.hasMoreTokens() )
                            {
                                sb.append( token.nextToken().trim() );
                                if ( token.hasMoreElements() )
                                {
                                    sb.append( "," );
                                }
                            }
                            return sb.toString();
                        }
                    }
                    catch ( Exception e )
                    {
                        throw new IOException( "Exception occured" + e.getMessage() );
                    }
                }
            }
        }

        return defaultValue;
    }
}
