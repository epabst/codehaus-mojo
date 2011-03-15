package org.codehaus.mojo.mockrepo;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.repository.metadata.Metadata;

import java.io.InputStream;
import java.util.List;

/**
 * Represents a Maven repository.
 */
public interface Repository
{
    /**
     * Gets the metadata for the specified path.
     *
     * @param path The path to get the metadata for.
     * @return the metadata or <code>null</code> if there is no metadata availble.
     */
    Metadata getMetadata( String path );

    /**
     * Gets an input stream for the specified path.
     *
     * @param path The path to get the input stream for.
     * @return an input stream or <code>null</code> if the content does not exist.  The caller is responsible for closing.
     */
    Content getContent( String path );

    /**
     * Gets the known child paths of the specified path.  Known sub-folders will end with
     * a '/' while direct child content will not end with a '/'.  Implementations are not
     * required to know the child paths.
     *
     * @param path The path.
     * @return The (possibly) empth list of child paths.  An empty list does not imply that
     *         there are no child paths.
     */
    List/*<String>*/ getChildPaths( String path );
}
