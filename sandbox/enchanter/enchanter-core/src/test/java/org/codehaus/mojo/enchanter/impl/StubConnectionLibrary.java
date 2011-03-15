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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.OperationNotSupportedException;

import org.codehaus.mojo.enchanter.ConnectionLibrary;

public class StubConnectionLibrary
    implements ConnectionLibrary
{

    InputStream inputStream;

    OutputStream outputStream = new ByteArrayOutputStream();

    public void connect( String host, String username )
        throws IOException
    {
    }

    public void connect( String host, int port, String username, String password )
        throws IOException
    {
    }

    public void disconnect()
    {
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    public void setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
    }

    public void setOutputStream( OutputStream outputStream )
    {
        this.outputStream = outputStream;
    }

    public String dumpOut()
    {
        String data = ( (ByteArrayOutputStream) outputStream ).toString();
        outputStream = new ByteArrayOutputStream();
        return data;
    }

    public void connect( String host )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    public void connect( String host, int port, String username, String password, String privateKeyPath )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    public void connect( String host, int port )
        throws IOException, OperationNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public void setReadTimeout( int msec )
        throws IOException, OperationNotSupportedException
    {
    }

}