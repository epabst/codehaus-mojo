package org.codehaus.mojo.enchanter.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.codehaus.mojo.enchanter.impl.DefaultStreamConnection;

import junit.framework.TestCase;

public class DefaultStreamConnectionTest
    extends TestCase
{

    DefaultStreamConnection conn;

    StubConnectionLibrary lib;

    public DefaultStreamConnectionTest( String arg0 )
    {
        super( arg0 );
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        conn = new DefaultStreamConnection();
        lib = new StubConnectionLibrary();
        conn.setConnectionLibrary( lib );
        conn.connect( "host", "username" );
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    public void testSend()
        throws IOException
    {
        conn.send( "foo" );
        assertEquals( "foo", lib.dumpOut() );

        conn.connect( null, null );
        conn.send( "foo^C" );
        assertEquals( "foo" + ( (char) 3 ), lib.dumpOut() );

        conn.connect( null, null );
        conn.send( "foo^M" );
        assertEquals( "foo\r\n", lib.dumpOut() );
    }

    public void testSendLine()
        throws IOException
    {
        lib.setInputStream( new ByteArrayInputStream( "foo\r\n".getBytes() ) );
        conn.connect( null, null );
        conn.sendLine( "foo" );
        assertEquals( "foo\r\n", lib.dumpOut() );
    }

    public void testSleep()
        throws InterruptedException
    {
        long start = System.currentTimeMillis();
        int sleepTime = 100;
        conn.sleep( sleepTime );
        long now = System.currentTimeMillis() + 1; //not very precise sleep
        long diff = now - ( start + sleepTime );
        assertTrue( "" + now + " not >= " + start + sleepTime + " diff is: " + diff, diff >= 0 );
    }

    public void testWaitForStringBoolean()
        throws IOException
    {
        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar\r\njoo".getBytes() ) );
        conn.connect( null, null );
        assertTrue( conn.waitFor( "bar", false ) );
        assertTrue( conn.waitFor( "jo", false ) );
        assertFalse( conn.waitFor( "asdf", false ) );

        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar\r\njoo".getBytes() ) );
        conn.connect( null, null );
        assertTrue( conn.waitFor( "bar", true ) );
        assertTrue( conn.waitFor( "jo", true ) );
        assertFalse( conn.waitFor( "asdf", true ) );
    }

    public void testWaitForWithRespond()
        throws IOException
    {
        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar\r\njoo".getBytes() ) );
        conn.connect( null, null );
        conn.respond( "bar", "jim" );
        assertTrue( conn.waitFor( "oo", false ) );
        assertEquals( "foo", conn.lastLine() );
        assertTrue( conn.waitFor( "jo", false ) );
        assertFalse( conn.waitFor( "asdf", false ) );

        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar\r\njoo".getBytes() ) );
        conn.connect( null, null );
        assertTrue( conn.waitFor( "bar", true ) );
        assertTrue( conn.waitFor( "jo", true ) );
        assertFalse( conn.waitFor( "asdf", true ) );
    }

    public void testWaitForMuxStringArrayBoolean()
        throws IOException
    {
        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar ds\r\njoo dsf".getBytes() ) );
        conn.connect( null, null );
        assertEquals( 1, conn.waitForMux( "bsar", "bar" ) );
        assertEquals( 0, conn.waitForMux( "jo", "fdo" ) );
        assertEquals( -1, conn.waitForMux( "asdf" ) );

    }

    public void testLastLine()
        throws IOException
    {
        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar ds\r\njoo dsf".getBytes() ) );
        conn.connect( null, null );
        conn.waitFor( "bar" );
        assertEquals( "bar", conn.lastLine() );

        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar ds\r\njoo dsf".getBytes() ) );
        conn.connect( null, null );
        conn.waitFor( "bar", true );
        assertEquals( "bar ds", conn.lastLine() );
    }

    public void testGetLine()
        throws IOException
    {
        lib.setInputStream( new ByteArrayInputStream( "foo\r\nbar ds\r\njoo dsf".getBytes() ) );
        conn.connect( null, null );
        assertEquals( "foo", conn.getLine() );
    }

}
