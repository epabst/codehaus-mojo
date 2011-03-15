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
 * Error message class of the CIS core classes. This exception
 * indicates a configuration problem or another error caused by
 * the user. There is also the {@link CisCoreException}, which
 * is thrown in case of internal problems, like I/O errors.
 */
public class CisCoreErrorMessage extends CisCoreException
{
    private static final long serialVersionUID = -7306832911432027139L;

    /**
     * Creates a new instance with the given error message
     * and cause.
     */
    public CisCoreErrorMessage( String pMessage, Throwable pCause )
    {
        super( pMessage, pCause );
    }

    /**
     * Creates a new instance with the given error message
     * and no cause.
     */
    public CisCoreErrorMessage( String pMessage )
    {
        this( pMessage, null );
    }

    /**
     * Creates a new instance with the given cause. The causes
     * error message is used as the created instances error
     * message.
     */
    public CisCoreErrorMessage( Throwable pCause )
    {
        this( null, pCause );
    }
}
