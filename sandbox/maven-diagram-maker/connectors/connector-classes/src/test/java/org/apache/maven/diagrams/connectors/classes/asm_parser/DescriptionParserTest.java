package org.apache.maven.diagrams.connectors.classes.asm_parser;

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
import java.text.ParseException;
import java.util.List;

import org.apache.maven.diagrams.connector_api.DiagramConnector;
import org.apache.maven.diagrams.connectors.classes.ClassesConnector;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class DescriptionParserTest extends PlexusTestCase
{
    public void testReadParamsList() throws ParseException
    {
        DescriptionParser dp = new DescriptionParser( "()" );
        assertEquals( 0, dp.readParamsList().size() );

        dp = new DescriptionParser( "(V[[I[[[Lorg/apache/maven/Test;F[J)" );
        List<String> paramsList = dp.readParamsList();
        assertEquals( 5, paramsList.size() );
        assertEquals( "void", paramsList.get( 0 ) );
        assertEquals( "int[][]", paramsList.get( 1 ) );
        assertEquals( "org.apache.maven.Test[][][]", paramsList.get( 2 ) );
        assertEquals( "float", paramsList.get( 3 ) );
        assertEquals( "long[]", paramsList.get( 4 ) );
    }

    public void testGetConnectorsDescriptor() throws Exception
    {
        ClassesConnector cc = (ClassesConnector) lookup( DiagramConnector.class, "connector-classes" );
        assertNotNull( cc.getConnectorDescriptor() );
    }
}
