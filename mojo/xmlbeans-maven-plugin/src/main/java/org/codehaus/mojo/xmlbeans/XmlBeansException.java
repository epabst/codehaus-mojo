package org.codehaus.mojo.xmlbeans;

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

import java.util.ResourceBundle;

import org.codehaus.mojo.exception.CodedException;
import org.codehaus.mojo.exception.Error;

/**
 * An error occurring within the XmlBeans plugin. Look, hardcoding strings, bad
 * idea, correct? This subclass of {@link CodedException} has all varieties of this
 * exception indicated by a static {@link Error} object. To throw an exception, pass the
 * specific {@link Error} constant as the first parameter in it's constructor. All
 * of the error messages themselves may be found in XmlBeansException.properties
 *
 * @author <a href="mailto:kris.bravo@corridor-software.us">Kris Bravo</a>
 * @version $Id$
 */
public class XmlBeansException
        extends CodedException
{
    /**
     *
     */
    private static final long serialVersionUID = -7072954264233215003L;

    /**
     * The publisher specified in the configuration isn't valid.
     */
    public static final Error REQ_FILE_MISSING = new Error( "file.requirements.missing" );

    /**
     * setProject was never called on XmlBeansPlugin so the PluginProperties was never set.
     */
    public static final Error UNSET_PROPERTIES = new Error( "properties.unset" );

    /**
     * We need the schema directory to continue.
     */
    public static final Error MISSING_SCHEMA_DIRECTORY = new Error( "directory.schema.missing" );

    /**
     * Errors occurred during the compile phase of the plugins process.
     */
    public static final Error COMPILE_ERRORS = new Error( "compile.errors" );

    /**
     * Something went wrong while looking up the classpath.
     */
    public static final Error CLASSPATH_DEPENDENCY = new Error( "classpath.dependency" );

    public static final Error MISSING_FILE = new Error( "file.missing" );

    public static final Error INVALID_CONFIG_FILE = new Error( "file.config.missing" );

    public static final Error XSD_ARTIFACT_JAR = new Error( "xsd.artifact.missing" );

    public static final Error ARTIFACT_FILE_PATH = new Error( "xsd.artifact.file.missing" );

    public static final Error INVALID_ARTIFACT_REFERENCE = new Error( "artifact.path.invalid" );

    /**
     * The handle to the resource bundle containing the errors.
     */
    private static final ResourceBundle ERROR_TABLE = ResourceBundle.getBundle( XmlBeansException.class.getName() );

    public static final Error STALE_FILE_TOUCH = new Error( "file.stale.io" );

    public static final Error MISSING_SCHEMA_FILE = new Error( "missing.schema.file" );

    public static final Error COPY_CLASSES = new Error( "copy.classes" );

    /**
     * A part specific exception with a specific error code.
     *
     * @param code The {@link Error} of this particular exception.
     */
    public XmlBeansException( Error code )
    {
        super( code );
        initResource();
    }

    /**
     * A part specific exception with an error code and fields in the
     * message.
     *
     * @param code   The {@link Error} of this particular exception.
     * @param fields An array of field values to replace fields in the error message.
     */
    public XmlBeansException( Error code, String[] fields )
    {
        super( code, fields );
        initResource();
    }

    /**
     * A part specific exception with an error code and fields in the
     * message.
     *
     * @param code The {@link Error} of this particular exception.
     * @param field A single value with which to replace the error message field.
     */
    public XmlBeansException( Error code, String field )
    {
        super( code, field );
        initResource();
    }

    /**
     * Creates a new Coded CAM Exception, given an error code and root cause.
     *
     * @param code      The {@link Error} of this particular exception.
     * @param exception The root cause of the problem.
     */
    public XmlBeansException( Error code, Throwable exception )
    {
        super( code, exception );
        initResource();
    }

    /**
     * Creates a coded exception with fields and a root cause.
     *
     * @param code      The {@link Error} of this particular exception.
     * @param field     A single value with which to replace the error message field.
     * @param exception The source of the error.
     */
    public XmlBeansException( Error code, String field, Throwable exception )
    {
        super( code, field, exception );
        initResource();
    }

    /**
     * Creates a coded exception with fields and a root cause.
     *
     * @param code      The {@link Error} of this particular exception.
     * @param fields    An array of field values to replace fields in the error message.
     * @param exception The source of the error.
     */
    public XmlBeansException( Error code, String[] fields, Throwable exception )
    {
        super( code, fields, exception );
        initResource();
    }

    /**
     * Set the resource bundle table for this subclass of CodedException.
     */
    private void initResource()
    {
        errors = ERROR_TABLE;
    }
}
