package org.codehaus.mojo.exception;

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

import java.io.Serializable;

/**
 * A key for a particular CodedException. This key is used to look up the actual
 * error message text.
 *
 * @author <a href="mailto:kris.bravo@corridor-software.us">Kris Bravo</a>
 * @version $Id$
 */
public class Error
        implements Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 2107339436186274590L;

    /**
     * The error code's key. Used to look up the error text
     */
    private String errorKey;

    /**
     * Creates a new error code with a lookup value of <code>key</code>.
     *
     * @param key The error code key.
     */
    public Error( final String key )
    {
        errorKey = key;
    }

    /**
     * Returns the error code key.
     *
     * @return The error key.
     */
    public final String getErrorKey()
    {
        return errorKey;
    }

    /**
     * Displays the Error object as a string, namely it's errorKey value.
     *
     * @return The error key.
     */
    public final String toString()
    {
        return errorKey;
    }
}
