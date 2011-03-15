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

import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.cruisecontrol.configelement.EmailPublisher;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * Helper class to write configuration the different supported plugins.
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 *
 */
public class CruiseControlPluginConfigurer
{

    public static void addEmailPlugin( PrettyPrintXMLWriter writer, EmailPublisher config, MavenProject project )
    {
        String ciUrl = null == project ? null : null == project.getCiManagement() ? null : project.getCiManagement()
            .getUrl()
            + "buildresults/${project.name}";

        String type = config.isHtmlemail() ? "htmlemail" : "email";
        writer.startElement( "plugin" );
        writer.addAttribute( "name", type );
        WriterUtil.addIfNotNull( writer, "buildresultsurl", ciUrl );
        if ( config.isHtmlemail() )
        {
            CruiseControlEmailConfigurer.addHtmlEmailAttributes( writer, config );
        }
        CruiseControlEmailConfigurer.addCommonMailAttributes( writer, config );

        writer.endElement();
    }

    public static void addProjectPlugin( PrettyPrintXMLWriter writer )
    {
        writer.startElement( "plugin" );
        writer.addAttribute( "name", "project" );
        addListeners( writer );
        addLog( writer );
        writer.endElement();
    }

    private static void addListeners( XMLWriter writer )
    {
        writer.startElement( "listeners" );
        {
            writer.startElement( "currentbuildstatuslistener" );
            writer.addAttribute( "file", "logs/${project.name}/status.txt" );
            writer.endElement();
        }
        writer.endElement();
    }

    private static void addLog( XMLWriter writer )
    {
        writer.startElement( "log" );
        {
            writer.startElement( "merge" );
            writer.addAttribute( "dir", "logs/${project.name}" );
            writer.endElement();
        }
        writer.endElement();

    }
}
