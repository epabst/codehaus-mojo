package org.codehaus.mojo.enchanter;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.OperationNotSupportedException;

public interface ConnectionLibrary
{

    /**
     * Connect to the remote StreamConnection server using no authentication and default port
     * 
     * @param host The remote StreamConnection server
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host )
        throws IOException, OperationNotSupportedException;

    /**
     * Connect to the remote StreamConnection server using no authentication
     * 
     * @param host The remote StreamConnection server
     * @param port The port to use
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host, int port )
        throws IOException, OperationNotSupportedException;

    /**
     * Connect to the remote StreamConnection server using public key authentication
     * 
     * @param host The remote StreamConnection server
     * @param username The user name on the server
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host, String username )
        throws IOException, OperationNotSupportedException;

    /**
     * Connect to the remote StreamConnection server using public key or password authentication
     * 
     * @param host The remote StreamConnection server
     * @param username The user name on the server
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host, int port, String username, String password )
        throws IOException, OperationNotSupportedException;

    /**
     * Connect to the remote StreamConnection server using public key or password authentication
     * 
     * @param host The remote StreamConnection server
     * @param port The remote StreamConnection server port
     * @param username The user name on the server
     * @param password The password to use for the public key or password authentication
     * @param privateKeyPath The path to the private key
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host, int port, String username, String password, String privateKeyPath )
        throws IOException, OperationNotSupportedException;

    /**
     * Gets the inputstream of the current connection
     */
    public InputStream getInputStream();

    /**
     * Gets the output stream of the current connection
     */
    public OutputStream getOutputStream();

    /**
     * Disconnects from the remote StreamConnection server
     * @throws IOException 
     */
    public void disconnect()
        throws IOException;

    /**
     * Disconnects from the remote StreamConnection server
     * @throws IOException 
     */
    public void setReadTimeout( int msec )
        throws IOException, OperationNotSupportedException;

}
