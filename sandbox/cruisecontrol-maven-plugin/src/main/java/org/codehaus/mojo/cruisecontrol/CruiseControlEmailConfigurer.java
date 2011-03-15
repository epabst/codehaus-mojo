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
import org.codehaus.mojo.cruisecontrol.configelement.EmailMapper;
import org.codehaus.mojo.cruisecontrol.configelement.EmailPublisher;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * Helper class to write the different supported publishers.
 * 
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 *
 */
public class CruiseControlEmailConfigurer
{

    public static void addHtmlEMailPublisher( XMLWriter writer, EmailPublisher config, MavenProject reactorProject )
    {
        writer.startElement( "htmlemail" );
        addHtmlEmailAttributes( writer, config );

        addCommonMailAttributes( writer, config );
        writer.endElement();

    }

    public static void addHtmlEmailAttributes( XMLWriter writer, EmailPublisher config )
    {
        WriterUtil.addIfNotNull( writer, "xsldir", config.getXsldir() );
        WriterUtil.addIfNotNull( writer, "css", config.getCss() );
    }

    public static void addEMailPublisher( XMLWriter writer, EmailPublisher config, MavenProject reactorProject )
    {
        writer.startElement( "email" );
        addCommonMailAttributes( writer, config );
        writer.endElement();

    }

    public static void addCommonMailAttributes( XMLWriter writer, EmailPublisher config )
    {

        WriterUtil.addIfNotNull( writer, "mailhost", config.getMailhost() );
        WriterUtil.addIfNotNull( writer, "mailport", config.getMailport() );
        WriterUtil.addIfNotNull( writer, "returnaddress", config.getReturnaddress() );

        if ( null != config.getSuccesses() && config.getSuccesses().length > 0 )
        {
            for ( int i = 0; i < config.getSuccesses().length; i++ )
            {
                String address = config.getSuccesses()[i];
                writer.startElement( "success" );
                writer.addAttribute( "address", address );
                writer.endElement();
            }
        }
        if ( null != config.getFailures() && config.getFailures().length > 0 )
        {
            for ( int i = 0; i < config.getFailures().length; i++ )
            {
                String address = config.getFailures()[i];
                writer.startElement( "failure" );
                writer.addAttribute( "address", address );
                writer.endElement();
            }
        }
        if ( null != config.getMaps() && config.getMaps().length > 0 )
        {
            for ( int i = 0; i < config.getMaps().length; i++ )
            {
                EmailMapper map = config.getMaps()[i];
                addMailAlias( writer, map.getAlias(), map.getAddress() );
            }
        }

    }

    private static void addMailAlias( XMLWriter writer, String alias, String address )
    {
        writer.startElement( "map" );
        writer.addAttribute( "alias", alias );
        writer.addAttribute( "address", address );
        writer.endElement();

    }
}
