package org.codehau.mojo.enchanter.beanshell;

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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.mojo.enchanter.ScriptRecorder;
import org.codehaus.mojo.enchanter.impl.DefaultStreamConnection;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Executes the passed script using Beanshell
 */
public class Main
{

    /**
     * @param args
     * @throws EvalError 
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws BSFException
     */
    public static void main( String[] args )
        throws EvalError, FileNotFoundException, IOException
    {
        ScriptRecorder rec = new BeanShellScriptRecorder();

        args = rec.processForLearningMode( args );

        String filePath = args[0];

        DefaultStreamConnection streamConnection = new DefaultStreamConnection();

        Interpreter i = new Interpreter();

        // deprecated
        i.set( "ssh", streamConnection );

        i.set( "conn", streamConnection );
        i.set( "args", args );
        i.source( filePath );
    }

}
