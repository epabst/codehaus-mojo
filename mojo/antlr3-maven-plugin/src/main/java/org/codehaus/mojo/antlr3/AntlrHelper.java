package org.codehaus.mojo.antlr3;

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

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:dave@badgers-in-foil.co.uk">David Holroyd</a>
 * @version $Id $
 */
public class AntlrHelper
{
    /**
     * Checks to see if the list of outputFiles all exist, and have
     * last-modified timestamps which are later than the last-modified
     * timestamp of the grammar file. If these conditions hold, the method
     * returns false, otherwise, it returns true.
     *
     * @param grammarFileName
     * @param outputFiles
     * @return
     */
    public static boolean buildRequired( String grammarFileName, List outputFiles )
    {
        File grammarFile = new File( grammarFileName );
        long grammarLastModified = grammarFile.lastModified();
        for ( Iterator i = outputFiles.iterator(); i.hasNext(); )
        {
            File outputFile = (File) i.next();
            if ( !outputFile.exists() || grammarLastModified > outputFile.lastModified() )
            {
                return true;
            }
        }
        return false;
    }
}
