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

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringOutputStream;
import org.apache.maven.plugin.logging.Log;

import java.util.List;
import java.util.Properties;
import java.io.*;

/**
 * Util functions for the smc plugin
 *
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 * @version $Id$
 */
public class Util {
    static List getSmFiles(File sourceDirectory) throws IOException {
        return FileUtils.getFiles( sourceDirectory, "**/*.sm", null );
    }

    static Properties getSmFileHeader(File smFile) throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader( smFile );
        BufferedReader reader = new BufferedReader( fileReader );
        try {
            String line;
            do {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("%class ")) {
                    properties.setProperty("class", line.substring("%class ".length()));
                }
                else if (line.startsWith("%package ")) {
                    properties.setProperty("package", line.substring("%package ".length()));
                }
            } while (true);
            return properties;
        } finally {
            reader.close();
        }
    }

    /**
     * Uses the default charset
     * @param stream
     * @param outputFilePath
     * @throws FileNotFoundException if the output file path is invalid
     * @throws IOException
     */
    public static void copyStreamToFile( InputStream stream, String outputFilePath ) throws FileNotFoundException, IOException {
        BufferedInputStream is = new BufferedInputStream(stream);
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(outputFilePath));
            byte[] b = new byte[1024];
            int read;
            while ((read = is.read(b)) != -1) {
                os.write(b, 0, read);
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * execute the net.sf.smc.Smc#main method given the specified arguments.
     * Standard Output and Err messages are redirected to <code>log.info()</code> and <code>log.error</code>.
     * @param arguments
     * @param log
     * @throws Exception thrown if log.error() is not empty
     */
    static void executeSmc(List arguments, Log log) throws Exception {
        final StringOutputStream out = new StringOutputStream();
        final StringOutputStream err = new StringOutputStream();
        executeSmc(arguments, out, err);
        if (out.toString().length() > 0) {
            log.info(out.toString());
        }
        if (err.toString().length() > 0) {
            log.error( ".sm file contains errors: \n" + err.toString() );
            throw new Exception( "Error while converting files." );
        }
    }

    /**
     * Executes the net.sf.smc.Smc#main method, redirecting standard output and error to the specified OutputStreams,
     * and reverting back to the originals later on.
     *
     * @param arguments
     * @param out
     * @param err
     */
    static void executeSmc(List arguments, OutputStream out, OutputStream err) {
        PrintStream oldOut = System.out;
        PrintStream newOut = new PrintStream(out);
        System.setErr(newOut);
        PrintStream oldErr = System.err;
        PrintStream newErr = new PrintStream(err);
        System.setErr(newErr);
        try {
            net.sf.smc.Smc.main((String[]) arguments.toArray(new String[arguments.size()]));
        } finally {
            System.setErr(oldErr);
            System.setOut(oldOut);
        }
    }
}
