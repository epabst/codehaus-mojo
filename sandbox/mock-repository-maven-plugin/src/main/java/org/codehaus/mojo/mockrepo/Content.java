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

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * Represents content in a repository.
 */
public interface Content
{
    /**
     * Returns an input stream for the content.  The caller is responsible for closing the input stream.
     * @return an input stream of the content.
     */
    InputStream getInputStream()
        throws FileNotFoundException, IOException;

    /**
     * Returns the size of the content.
     * @return the number of bytes in the content.
     */
    long getSize();

    /**
     * Returns the date the content was last modified.
     * @return the date the content was last modified.
     */
    Date getLastModified();
}

