package org.codehaus.mojo.setup;

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

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Robert Scholte
 * @since 1.0.0
 */
public interface SetupManager
{

    /**
     * This method is called to process the request
     * 
     * @param request the request
     * @throws SetupExecutionException a possible exception during processing
     */
    void process( SetupExecutionRequest request )
        throws SetupExecutionException;

    /**
     * Every manager must provide a prototype.
     * This is a well documented (xml) file, which the user can use to build his own file
     * 
     * @return an inputstream containing the prototype
     * @throws IOException
     */
    InputStream getPrototypeInputStream()
        throws IOException;
}
