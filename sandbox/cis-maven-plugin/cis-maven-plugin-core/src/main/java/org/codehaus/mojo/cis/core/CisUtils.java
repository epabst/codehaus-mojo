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

import java.io.File;
import java.io.IOException;


/**
 * This is an interface of utility classes, which the core
 * user must provide. In the case of the Maven plugin, this
 * is implemented using Plexus classes. In the case of the
 * Ant tasks, this is implemented using Ant classes.
 */
public interface CisUtils
{
    /**
     * An interface, which is used for uptodate checks.
     * Basically, the resource has a modification date,
     * which is compared.
     */
    public static interface Resource
    {
        /**
         * Returns the resources modification date,
         * or -1, if the modification date is unknown.
         */
        long getModificationDate() throws IOException;
    }

    /**
     * Checks, whether the given source file is uptodate, compared
     * to the given target file. The project file is not considered
     * for uptodate checks: This is a shortcut for
     * {@link #isUpToDate(CisUtils.Resource, CisUtils.Resource, boolean)
     *   isUpToDate( pSourceFile, pTargetFile, false )}.
     */
    boolean isUpToDate( Resource pSourceFile, Resource pTargetFile )
        throws CisCoreException;

    /**
     * Checks, whether the given source file is uptodate, compared
     * to the given target file.
     * @param pConsiderProjectFile If this parameter is true,
     *   then the project file will be considered as an
     *   additional source file. In other words, the target
     *   will be rebuilt, if the project file is newer than
     *   the target file.
     */
    boolean isUpToDate( Resource pSourceFile, Resource pTargetFile, boolean pConsiderProjectFile )
        throws CisCoreException;

    /**
     * Logs a debugging message.
     */
    void debug( String pMessage );

    /**
     * Logs an informational message.
     */
    void info( String pString );

    /**
     * Copies the given source file to the given target file.
     * The target file will be overwritten, if it exists.
     * Assumes, that the target directory exists.
     * @throws CisCoreException Copying the file failed.
     */
    void copy( File pSourceFile, File pTargetFile ) throws CisCoreException;

    /**
     * Creates the directory of the given file, if it doesn't yet exist.
     */
    void makeDirOf( File pTargetFile ) throws CisCoreException;

    /**
     * Returns the temporary directory to use.
     */
    File getTempDir();

    /**
     * Returns the project file. This is used for cases,
     * when the project files build date shall be considered
     * for uptodate checks.
     */
    File getProjectFile();

    /**
     * "Touches" a file (sets the timestamp to the current time and date.
     */
    void touch( File pMrkrFile ) throws CisCoreException;
}
