package org.codehaus.mojo.analytics;

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

import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.util.IOUtil;

/**
 * Allows postprocessing the the generated website.
 * Ought to be executed after site:site and before site:deploy, or added to the site phase.
 * @author mkleint@codehaus.org
 */
public abstract class AbstractPostProcessMojo
    extends AbstractMojo
{

    /**
     * Specifies the output encoding.
     *
     * @parameter expression="${outputEncoding}" default-value="ISO-8859-1"
     */
    protected String encoding;
    
    /**
     * Directory containing the generated project sites and report distributions.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     */
    protected File outputDirectory;
    

    protected void processChildrenHtml(File outputDirectory, String toReplace, String replaceWith) throws MojoExecutionException {
        getLog().debug("Processing directory: " + outputDirectory.getAbsolutePath());
        File[] childs = outputDirectory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".html");
            }
        });
        for (int i = 0; i < childs.length; i++) {
            if (childs[i].isDirectory()) {
                processChildrenHtml(childs[i], toReplace, replaceWith);
            } else {
                try {
                    String file = fileRead(childs[i]);
                    file = file.replaceAll(toReplace, replaceWith); 
                    fileWrite(childs[i], file);
                } catch (IOException ex) {
                    throw new MojoExecutionException("Error in IO for file " + childs[i].getAbsolutePath());
                }
            }
        }
    }

    protected String fileRead( File file ) throws IOException {
        StringBuffer buf = new StringBuffer();
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream( file ), encoding);
            int count;
            char[] b = new char[512];
            while ( ( count = in.read( b ) ) > 0 )  // blocking read
            {
                buf.append(b, 0, count);
            }
        }
        finally
        {
            IOUtil.close( in );
        }
        return buf.toString();
    }

    protected void fileWrite(File fileName, String data ) throws IOException {
        OutputStreamWriter out = null;
        try
        {
            out = new OutputStreamWriter(new FileOutputStream(fileName), encoding);
            out.write(data);
        }
        finally
        {
            IOUtil.close( out );
        }
    }
    
    
}
