package org.apache.maven.diagrams.connector_api.manager;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.InputStream;

import org.apache.maven.diagrams.connector_api.ConnectorConfiguration;
import org.apache.maven.diagrams.connector_api.ConnectorException;
import org.apache.maven.diagrams.connector_api.descriptor.ConnectorDescriptor;
import org.apache.maven.diagrams.connector_api.descriptor.Mapping;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * The class is able to create ConnectorConfiguration class from XML file (and the Connector's description) (from XML
 * method).
 * 
 * It also can serialize the ConnectorConfiguration class to the file (toXML method)
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ConnectorManager
{
    public String toXML( ConnectorConfiguration config, ConnectorDescriptor desc ) throws ConnectorException
    {
        XStream xstream = getConfiguredXStream( desc );
        return xstream.toXML( config );
    }

    public ConnectorConfiguration fromXML( InputStream is, ConnectorDescriptor desc ) throws ConnectorException
    {
        XStream xstream = getConfiguredXStream( desc );
        ConnectorConfiguration result;
        try
        {
            result = desc.getConfigurationClass().newInstance();
        }
        catch ( InstantiationException e )
        {
            throw new ConnectorException( "Cannot create instance of class: " + desc.getConfigurationClass().getName(),
                                          e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ConnectorException( "Cannot create instance of class: " + desc.getConfigurationClass().getName(),
                                          e );
        }
        xstream.fromXML( is, result );
        return result;
    };

    private XStream getConfiguredXStream( ConnectorDescriptor desc ) throws ConnectorException
    {
        XStream xstream = new XStream();
        xstream.aliasType( "configuration", ConnectorConfiguration.class );
        if ( desc != null && desc.getMappings() != null )
        {
            for ( Mapping m : desc.getMappings() )
            {
                xstream.aliasType( m.getTagName(), m.getClazz() );
                if ( m.getConverter() != null )
                {

                    try
                    {
                        if ( SingleValueConverter.class.isAssignableFrom( m.getConverter() ) )
                            xstream.registerConverter( (SingleValueConverter) m.getConverter().newInstance() );

                        if ( Converter.class.isAssignableFrom( m.getConverter() ) )
                            xstream.registerConverter( (Converter) m.getConverter().newInstance() );
                    }
                    catch ( InstantiationException e )
                    {
                        throw new ConnectorException( "Cannot create instance of class: " + m.getConverter().getName(),
                                                      e );
                    }
                    catch ( IllegalAccessException e )
                    {
                        throw new ConnectorException( "Cannot create instance of class: " + m.getConverter().getName(),
                                                      e );
                    }

                }
            }
        }
        return xstream;
    }

}
