package org.apache.maven.plugin.jdiff;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;


public class JavadocBean
{
    private Commandline cmd = new Commandline();
    
    public JavadocBean()
    {
        cmd.setExecutable( getJavadocPath() );
    }
    
    public void addArgumentPair( String argKey, String argValue )
    {
        cmd.createArgument().setValue( "-" + argKey );
        
        cmd.createArgument().setValue( argValue );
    }
    
    public void addArgument( String arg )
    {
        cmd.createArgument().setValue( arg );
    }
    
    public void execute( String workingDir ) throws MavenReportException
    {
        File dir = new File( workingDir );
        
        if ( !dir.exists() ) dir.mkdirs();
        
        cmd.setWorkingDirectory( dir.getAbsolutePath() );
        
        int exitCode = 0;
                
        try
        {
            exitCode = CommandLineUtils.executeCommandLine( cmd, 
                                                            new DefaultConsumer(), 
                                                            new DefaultConsumer() );
            
            //Process p = Runtime.getRuntime().exec( cmd.toString() );
            
            //p.waitFor();
            
            //System.out.println( IOUtil.toString( p.getInputStream() ) );
            
            //exitCode = p.exitValue();
        }
        catch ( Exception ex )
        {
            throw new MavenReportException( "generateJDiff doclet failed.", ex );
        }
        
        if ( exitCode != 0 )
        {
            throw new MavenReportException( "generate JDiff doclet failed." );
        }
    }

    private String getJavadocPath()
    {
        final String javadocCommand = "javadoc" + ( SystemUtils.IS_OS_WINDOWS ? ".exe" : "" );
        // For IBM's JDK 1.2
        final File javadocExe = ( SystemUtils.IS_OS_AIX 
                                    ? new File( SystemUtils.getJavaHome() + "/../sh", javadocCommand ) 
                                    : new File( SystemUtils.getJavaHome() + "/../bin", javadocCommand )
                                );

        //return javadocExe.getAbsolutePath();
        
        return "javadoc";
    }
}