package org.codehaus.mojo.javancss;

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

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * @author Jean-Laurent de Morlhon
 */
public class JavaNcssReportTest extends TestCase
{

    public void testSort() throws DocumentException
    {
        String toBeSorted =
            "<test><person><name>Arthur</name><age>5</age></person><person><name>Blake</name><age>400</age></person><person><name>John</name><age>30</age></person></test>";
        SAXReader reader = new SAXReader();
        Document document;
        document = reader.read( new StringReader( toBeSorted ) );// JavaNcssReportTest.class.getClassLoader().getResourceAsStream("javancss-raw-report.xml"));
        List nodeList = document.selectNodes( "//test/person", "number(age)" );
        Collections.sort( nodeList, new NumericNodeComparator( "age" ) );
        assertEquals( "400", ( (Node) nodeList.get( 0 ) ).valueOf( "age" ) );
        assertEquals( "30", ( (Node) nodeList.get( 1 ) ).valueOf( "age" ) );
        assertEquals( "5", ( (Node) nodeList.get( 2 ) ).valueOf( "age" ) );
    }

}
