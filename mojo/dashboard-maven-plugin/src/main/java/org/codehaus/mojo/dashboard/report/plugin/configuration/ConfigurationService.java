package org.codehaus.mojo.dashboard.report.plugin.configuration;

/*
 * Copyright 2007 David Vicente
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Service Dashboard Configuration
 *
 * @author <a href="dvicente72@gmail.com">David Vicente</a>
 */
public class ConfigurationService implements IConfigurationService
{

    private List warningsMsg = new ArrayList();

    private boolean isValid = true;

    private boolean isValidXSD = true;

    private Configuration dashConfig;

    static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     *
     */
    private String configfile = "";

    /**
     * @see org.codehaus.mojo.dashboard.report.plugin.configuration.IConfigurationService#getConfiguration()
     */
    public ConfigurationService( String configfile ) throws ConfigurationServiceException
    {
        this.configfile = configfile;
        validateConfigFileWithXSD();
        if ( isValidXSD )
        {
            dashConfig = readConfigFile( this.configfile );
            validateConfigFile();
        }
        else
        {
            isValid = false;
            throw new ConfigurationServiceException( "XML config validation with schema failed : " + this.configfile );
        }

    }

    public Configuration getConfiguration()
    {
        return dashConfig;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.mojo.dashboard.report.plugin.configuration.IConfigurationService#getConfigFile()
     */
    public String getConfigFile()
    {
        return configfile;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.mojo.dashboard.report.plugin.configuration.IConfigurationService#setConfigFile(java.lang.String)
     */
    public void setConfigFile( String configFile )
    {
        this.configfile = configFile;
    }

    public boolean isValidConfig()
    {

        return isValid;
    }

    private void validateConfigFileWithXSD()
    {
        InputStream stream =
            Thread.currentThread().getContextClassLoader().getResourceAsStream( "config/maven-dashboard-config.xsd" );
        try
        {
            InputSource xmlFile = new InputSource( new FileInputStream( this.configfile ) );

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments( true );
            dbf.setNamespaceAware( true );
            dbf.setValidating( true );
            dbf.setAttribute( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );
            dbf.setAttribute( JAXP_SCHEMA_SOURCE, stream );
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler( new MyDefaultHandler() );
            db.parse( xmlFile );

        }
        catch ( SAXException e )
        {
            isValidXSD = false;
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            isValidXSD = false;
            e.printStackTrace();
        }
        catch ( ParserConfigurationException e )
        {
            isValidXSD = false;
            e.printStackTrace();
        }

        System.out.println( isValidXSD );
    }

    private void validateConfigFile()
    {
        if ( dashConfig != null )
        {
            List sections = dashConfig.getSections();
            Iterator iter = sections.iterator();
            while ( iter.hasNext() )
            {
                Section section = (Section) iter.next();
                if ( !section.isValidGraphs() )
                {
                    isValid = false;
                    warningsMsg.addAll( section.getWarningMessages() );
                    break;
                }
            }
        }
        else
        {
            isValid = false;
        }
    }

    public List getWarningMessages()
    {
        return warningsMsg;
    }

    private Configuration readConfigFile( String configFile ) throws ConfigurationServiceException
    {
        Configuration dashConfiguration = null;
        try
        {

            // Instanciation de la classe XStream
            XStream xstream = new XStream( new DomDriver() );
            // Instanciation d'un fichier
            File fichier = new File( configFile );

            // Redirection du fichier /target/dashboard-report.xml vers un flux
            // d'entrée fichier
            FileInputStream fis = new FileInputStream( fichier );

            try
            {
                xstream.setMode( XStream.NO_REFERENCES );
                // Convertion du contenu de l'objet Configuration en XML
                xstream.alias( "configuration", Configuration.class );
                xstream.useAttributeFor( "version", String.class );
                xstream.alias( "section", Section.class );
                xstream.alias( "graph", Graph.class );
                xstream.useAttributeFor( "id", String.class );
                xstream.useAttributeFor( "title", String.class );
                xstream.useAttributeFor( "timeUnit", String.class );
                xstream.useAttributeFor( "groupId", String.class );
                xstream.useAttributeFor( "artifactId", String.class );
                xstream.useAttributeFor( "startPeriod", String.class );
                xstream.useAttributeFor( "endPeriod", String.class );
                // Désérialisation du fichier maven-dashboard-config.xml vers un nouvel
                // objet Configuration
                dashConfiguration = (Configuration) xstream.fromXML( fis );

            }
            finally
            {
                // On s'assure de fermer le flux quoi qu'il arrive
                fis.close();
            }
        }
        catch ( FileNotFoundException e )
        {
            dashConfiguration = null;
            throw new ConfigurationServiceException( "readConfigFile() failed : " + this.configfile, e );
        }
        catch ( IOException e )
        {
            dashConfiguration = null;
            throw new ConfigurationServiceException( "readConfigFile() failed : " + this.configfile, e );
        }
        return dashConfiguration;
    }

    class MyDefaultHandler extends DefaultHandler
    {
        private String errMessage = "";

        /*
         * With a handler class, just override the methods you need to use
         */

        // Start Error Handler code here
        public void warning( SAXParseException e )
        {
            System.out.println( "Warning Line " + e.getLineNumber() + ": " + e.getMessage() + "\n" );
        }

        public void error( SAXParseException e )
        {
            errMessage = new String( "Error Line " + e.getLineNumber() + ": " + e.getMessage() + "\n" );
            System.out.println( errMessage );
            isValidXSD = false;
        }

        public void fatalError( SAXParseException e )
        {
            errMessage = new String( "Error Line " + e.getLineNumber() + ": " + e.getMessage() + "\n" );
            System.out.println( errMessage );
            isValidXSD = false;
        }
    }
}