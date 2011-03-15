/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.mojo.cis.core;


/**
 * Exception class of the CIS core classes. This exception
 * indicates a system problem. For example, it is thrown in
 * case of I/O errors. There is also the {@link CisCoreErrorMessage},
 * which is thrown in case of configuration problems or similar
 * cases.
 */
public class CisCoreException extends Exception
{
    private static final long serialVersionUID = -3418249901219327949L;

    private static String asMessage(String pMessage, Throwable pCause) {
        if ( pMessage != null )
        {
            return pMessage;
        }
        if ( pCause == null )
        {
            return "Unknown error";
        }
        String message = pCause.getMessage();
        if ( message == null )
        {
            return message;
        }
        return pCause.getClass().getName();
    }

    /**
     * Creates a new instance with the given error message
     * and cause.
     */
    public CisCoreException( String pMessage, Throwable pCause )
    {
        super( asMessage(pMessage, pCause), pCause );
    }

    /**
     * Creates a new instance with the given error message
     * and no cause.
     */
    public CisCoreException( String pMessage )
    {
        this( pMessage, null );
    }

    /**
     * Creates a new instance with the given cause. The causes
     * error message is used as the created instances error
     * message.
     */
    public CisCoreException( Throwable pCause )
    {
        this( null, pCause );
    }
}
