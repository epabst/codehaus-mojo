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

import junit.framework.TestCase;

import org.dom4j.Node;
import org.easymock.MockControl;

/**
 * Test for NumericNodeComparator class.
 *
 * @author <a href="jeanlaurent@gmail.com">Jean-Laurent de Morlhon</a>
 */
public class NumericNodeComparatorTest extends TestCase
{
    private static final String NODE_PROPERTY = "foobar";

    private static final Integer SMALL_VALUE = new Integer( 10 );

    private static final Integer BIG_VALUE = new Integer( 42 );

    private MockControl control;

    private Node bigNodeMock;

    private Node smallNodeMock;

    private NumericNodeComparator nnc;

    public void setUp()
    {
        control = MockControl.createControl( Node.class );
        nnc = new NumericNodeComparator( NODE_PROPERTY );
        bigNodeMock = (Node) control.getMock();
        smallNodeMock = (Node) control.getMock();
    }

    public void testComparePositive()
    {
        bigNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( BIG_VALUE );
        smallNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( SMALL_VALUE );
        control.replay();
        assertTrue( nnc.compare( smallNodeMock, bigNodeMock ) > 0 );
        control.verify();
    }

    public void testCompareNegative()
    {
        bigNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( SMALL_VALUE );
        smallNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( BIG_VALUE );
        control.replay();
        assertTrue( nnc.compare( smallNodeMock, bigNodeMock ) < 0 );
        control.verify();
    }

    public void testCompareEqual()
    {
        bigNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( SMALL_VALUE );
        smallNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( SMALL_VALUE );
        control.replay();
        assertEquals( 0, nnc.compare( smallNodeMock, bigNodeMock ) );
        control.verify();
    }

    // should throw npe whenever one of the node is null
    public void testCompareWithBigNull()
    {
        smallNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( SMALL_VALUE );
        control.replay();
        boolean caught = false;
        try
        {
            nnc.compare( null, smallNodeMock );
        }
        catch ( NullPointerException npe )
        {
            caught = true;
        }
        assertTrue( caught );
        control.verify();
    }

    public void testCompareWithSmallNull()
    {
        bigNodeMock.numberValueOf( NODE_PROPERTY );
        control.setReturnValue( BIG_VALUE, MockControl.ZERO_OR_MORE );
        control.replay();
        boolean caught = false;
        try
        {
            nnc.compare( bigNodeMock, null );
        }
        catch ( NullPointerException npe )
        {
            caught = true;
        }
        assertTrue( caught );
        control.verify();
    }

}
