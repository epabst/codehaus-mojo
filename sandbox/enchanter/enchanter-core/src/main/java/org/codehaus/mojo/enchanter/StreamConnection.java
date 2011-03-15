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

/**
 * Represents an StreamConnection server connection implementation
 */
public interface StreamConnection
{

    /**
     * Connect to the remote StreamConnection server using no authentication
     * 
     * @param host The remote StreamConnection server
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host )
        throws IOException;

    /**
     * Connect to the remote StreamConnection server using no authentication
     * 
     * @param host The remote StreamConnection server
     * @param port The port to use
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host, int port )
        throws IOException;

    /**
     * Connect to the remote StreamConnection server using public key authentication
     * 
     * @param host The remote StreamConnection server
     * @param username The user name on the server
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host, String username )
        throws IOException;

    /**
     * Connect to the remote StreamConnection server using public key or password authentication
     * 
     * @param host The remote StreamConnection server
     * @param username The user name on the server
     * @throws IOException If a connection cannot be made
     */
    public void connect( String host, int port, String username, String password )
        throws IOException;

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
        throws IOException;

    /**
     * Sets the timeout for all wait calls
     * 
     * @param timeout The timeout in milliseconds
     */
    public void setTimeout( int timeout );

    /**
     * Get the current internal timeout value the timeout for all wait calls
     * 
     * @return value in milliseconds
     */
    public int getTimeout();
    
    /**
     * Sends the response whenever the prompt is encountered. Only activated
     * during a waitFor, waitForMux, or getLine call. If the response is null,
     * the respond trigger is removed.
     * 
     * @param prompt The text to match for the response
     * @param response The response to send
     */
    public void respond( String prompt, String response );

    /**
     * Sets the text to the remote server
     * 
     * @param text The text to send
     * @throws IOException
     */
    public void send( String text )
        throws IOException;

    /**
     * Sends the text to the remote server followed by an end of line marker
     * 
     * @param text The text to send
     * @throws IOException
     */
    public void sendLine( String text )
        throws IOException;

    /**
     * Waits for multiple strings, returning the index of the first match
     * 
     * @param text An array of prompts
     * @return The index of the prompt matched, -1 if the timeout was reached
     * @throws IOException
     */
    public int waitForMux( String... text )
        throws IOException;

    /**
     * Waits for multiple strings, returning the index of the first match. Can
     * optionally read the whole line before returning.
     * 
     * @param text An array of prompts
     * @param readLineOnMatch If true, the whole line containing the first match
     *            will be read and available via {@link #lastLine()}
     * @return The index of the prompt matched, -1 if the timeout was reached
     * @throws IOException
     */
    public int waitForMux( String[] text, boolean readLineOnMatch )
        throws IOException;

    /**
     * Waits for a prompt and returns if it was matched.
     * 
     * @param text The prompt
     * @return True if matched, false if the timeout was reached
     * @throws IOException
     */
    public boolean waitFor( String text )
        throws IOException;

    /**
     * Waits for a prompt and returns if it was matched. Can optionally read the
     * whole line before returning.
     * 
     * @param text The prompt
     * @param readLineOnMatch If true, the whole line containing the first match
     *            will be read and available via {@link #lastLine()}
     * @return True if matched, false if the timeout was reached
     * @throws IOException
     */
    public boolean waitFor( String text, boolean readLineOnMatch )
        throws IOException;

    /**
     * Gets the last line matched
     * 
     * @return The last line matched
     */
    public String lastLine();

    /**
     * Gets the next full line ending with the end of line marker
     * 
     * @return The next full line
     * @throws IOException
     */
    public String getLine()
        throws IOException;

    /**
     * Sleeps for the specified number of milliseconds
     * 
     * @param millis The sleep time in milliseconds
     * @throws InterruptedException
     */
    public void sleep( int millis )
        throws InterruptedException;

    /**
     * Disconnects from the remote StreamConnection server
     * @throws IOException 
     */
    public void disconnect()
        throws IOException;

    /**
     * Adds a stream listener to be notified of each byte read and written.
     * 
     * @param listener The StreamListener implementation
     */
    public void addStreamListener( StreamListener listener );

    /**
     * Removes a stream listener 
     * 
     * @param listener The StreamListener implementation
     */
    public void removeStreamListener( StreamListener listener );
    
    /**
     * Sets whether to be in debugging mode or not. Debugging mode usually means
     * all the output will be copied to the console.
     * 
     * @param debug True for debugging mode
     */
    public void setDebug( boolean debug );

    public void setEndOfLine( String eol );
    
    /**
     * Remove all data in output stream. Use this before send command if neccessary
     */
    public void clear() throws IOException;
}
