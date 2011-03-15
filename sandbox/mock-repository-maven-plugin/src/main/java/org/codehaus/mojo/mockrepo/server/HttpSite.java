package org.codehaus.mojo.mockrepo.server;

/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since Sep 1, 2009 2:23:17 PM
 */
public interface HttpSite
{
    /**
     * Checks if the supplied requestPath is a valid path. A valid path may not exist, but an invalid path never exists.
     * That is <code> assert !isValidPath(path) && null == getContent(path)</code>
     *
     * @param requestPath The requestPath to check.
     * @return <code>true</code> if the request path is valid.
     */
    boolean isValidPath( String requestPath );

    /**
     * Returns the content at the specified path, or null if either the content does not exist or the path is invalid.
     *
     * @param requestPath The request path.
     * @return The content.
     */
    HttpContent getContent( String requestPath );
}
