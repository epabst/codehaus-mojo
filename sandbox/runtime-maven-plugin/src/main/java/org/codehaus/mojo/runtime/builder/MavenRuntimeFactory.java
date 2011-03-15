package org.codehaus.mojo.runtime.builder;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.runtime.model.Runtime;

/**
 * @author <a href="jesse.mcconnell@gmail.com">Jesse McConnell</a>
 * @version $Id$
 */
public class MavenRuntimeFactory
{
    private static Map builderMap = new HashMap();

    public static MavenRuntimeBuilder getRuntimeBuilder( Runtime runtime )
    {
        if ( runtime.getShell() != null )
        {
            if ( builderMap.containsKey( "shell" ) )
            {
                return (MavenRuntimeBuilder) builderMap.get( "shell" );
            }
            else
            {
                MavenRuntimeBuilder shellBuilder = new MavenShellRuntimeBuilder();
                builderMap.put( "shell", shellBuilder );
                return shellBuilder;
            }
        }
        else if ( runtime.getJar() != null )
        {
            if ( builderMap.containsKey( "jar" ) )
            {
                return (MavenRuntimeBuilder) builderMap.get( "jar" );
            }
            else
            {
                MavenRuntimeBuilder jarBuilder = new MavenJarRuntimeBuilder();
                builderMap.put( "jar", jarBuilder );
                return jarBuilder;
            }
        }
        else
        {
            return null;
        }
    }
}
