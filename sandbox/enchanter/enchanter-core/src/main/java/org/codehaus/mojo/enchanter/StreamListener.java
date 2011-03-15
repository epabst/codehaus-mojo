package org.codehaus.mojo.enchanter;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.PrintWriter;

/**
 * The stream filter is the base interface for classes that want a copy of the
 * bytes read from and written to the stream.
 */
public interface StreamListener
{

    /**
     * Receives the block of data read from the back end.
     * 
     * @param b the data
     * @return the amount of bytes actually read
     */
    public void hasRead( byte b );

    /**
     * Called when data has been written to the stream from a filter
     * 
     * @param s The data
     */
    public void hasWritten( byte[] b );

    /**
     * Initializes listener
     * @param writer the writer to write to the ssh session with
     */
    public void init( PrintWriter writer );

}