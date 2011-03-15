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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author <a href="mailto:m2@corridor-software.us">Kris Bravo</a>
 * @version $Id$
 */
public abstract class CodedException
        extends MojoExecutionException
{

    /**
     * The error message used when a key is missing from the resource bundles.
     */
    protected static final Error MISSING_ERROR_KEY = new Error( "missing.error.key" );

    /**
     * The error code for the specific error.
     */
    protected Error error;

    /**
     * Any fields which would need to be filled in within the error message.
     */
    protected String[] errorFields;

    /**
     * The shadow handle to the resource bundle. Since statics can only
     * be shadowed and not overridden, this provides the mechanism needed
     * to access a subclasses resource bundle from a super-class non-static
     * method.
     */
    protected ResourceBundle errors = null;

    /**
     * Empty Constructor. Required for subclasses.
     */
    public CodedException()
    {
        super( "" );
        init( null, null );
    }

    /**
     * An exception with a specific error code.
     *
     * @param code The code key used to look up the error text.
     */
    public CodedException( final Error code )
    {
        super( "" );
        init( code, null );
    }

    /**
     * An exception with an error code and fields in the
     * message.
     *
     * @param code   The code key used to look up the error text.
     * @param fields An array of field values to replace fields in the error message.
     */
    public CodedException( final Error code, final String[] fields )
    {
        super( "" );
        init( code, fields );
    }

    /**
     * An exception with an error code and fields in the
     * message.
     *
     * @param code  The code key used to look up the error text.
     * @param field A single value with which to replace the error message field.
     */
    public CodedException( final Error code, final String field )
    {
        super( "" );
        String[] fields = new String[1];
        fields[0] = field;
        init( code, fields );
    }

    /**
     * Creates a new Coded Exception, given an error code and root cause.
     *
     * @param code      The {@link Error} of this particular exception.
     * @param exception The root cause of the problem.
     */
    public CodedException( final Error code, final Throwable exception )
    {
        super( "", ( Exception ) exception );
        init( code, null );
    }

    /**
     * Creates a coded exception with fields and a root cause.
     *
     * @param code      The code key used to look up the error text.
     * @param field     A single value with which to replace the error message field.
     * @param exception The source of the error.
     */
    public CodedException( final Error code, final String field, final Throwable exception )
    {
        super( "", ( Exception ) exception );
        String[] fields = new String[1];
        fields[0] = field;
        init( code, fields );
    }

    /**
     * Creates a coded exception with fields and a root cause.
     *
     * @param code      The code key used to look up the error text.
     * @param fields    An array of field values to replace fields in the error message.
     * @param exception The source of the error.
     */
    public CodedException( final Error code, final String[] fields, final Throwable exception )
    {
        super( "", ( Exception ) exception );
        init( code, fields );
    }

    /**
     * Initialize the instance.
     *
     * @param code   The code key used to look up the error text.
     * @param fields An array of field values to replace fields in the error message.
     */
    protected final void init( final Error code, final String[] fields )
    {
        if ( code != null )
        {
            error = code;
        }
        errorFields = fields;
    }

    /**
     * <code>getMessage</code> - Check the error code to see if
     * it's an <code>ExtendedError</code>.
     * Based on the result, pull the error string from the appropriate
     * resource bundle (business logic errors or general CAM errors).
     *
     * @return a <code>String</code> value
     */
    public final String getMessage()
    {
        String message;
        // determine if it's a valid extended error.
        if ( ( errors != null ) && ( error != null ) )
        {
            try
            {
                message = errors.getString( error.toString() ); // use the subclass resource bundle

                // if there are error fields, fill them in.
                if ( errorFields != null )
                {
                    message = MessageFormat.format( message, errorFields );
                }
            }
            catch ( MissingResourceException e )
            {
                message = errors.getString( MISSING_ERROR_KEY.getErrorKey() + ":" + e.getKey() );
            }

        }
        else
        {
            message = "An unknown error occurred.";
        }
        return message;
    }
}
