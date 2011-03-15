package org.codehaus.mojo.smc;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.apache.maven.plugin.logging.Log;

import java.util.List;
import java.io.File;

/**
 * Graphviz companion class :)
 *
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 * @version $Id$
 */
public class DotConvertor {
    /**
     * Convert the specified .dot files to the specified formats using the graphviz tool
     * @see #convert(java.io.File, String, org.apache.maven.plugin.logging.Log)
     */
    public static void convert(List files, String[] formats, Log log) throws CommandLineException
    {
        for (int i = 0; i < files.size(); i++) {
            File file = (File) files.get(i);
            for (int j = 0; j < formats.length; j++) {
                String format = formats[j];
                convert(file, format, log);
            }
        }
    }

    /**
     * Convert the specified .dot files to the specified formats using the graphviz tool
     * @param file
     * @param format e.g. "png", "gif"
     * @param log
     */
    public static int convert(File file, String format, Log log) throws CommandLineException {
        Commandline cl = new Commandline();
        cl.setExecutable( "dot" );
        cl.createArgument().setValue( "-T" + format );
        cl.createArgument().setValue( "-o" );
        cl.createArgument().setValue( file.getAbsolutePath().replace( ".dot", "." + format ) );
        cl.createArgument().setValue( file.getAbsolutePath() );

        log.debug( "executing: " + cl.toString() );

        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();
        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        int exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );

        String output = stdout.getOutput();
        if ( output.length() > 0 )
        {
            log.debug( output );
        }
        String errOutput = stderr.getOutput();
        if ( errOutput.length() > 0 )
        {
            log.warn( errOutput );
        }
        return exitCode;
    }
}
