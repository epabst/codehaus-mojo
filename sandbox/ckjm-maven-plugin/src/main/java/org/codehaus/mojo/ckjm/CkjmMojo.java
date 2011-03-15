package org.codehaus.mojo.ckjm;

/*
 * Copyright 2007 Wayne Fay. Created August 16, 2007.
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import gr.spinellis.ckjm.MetricsFilter;
import gr.spinellis.ckjm.PrintPlainResults;
import gr.spinellis.ckjm.ant.PrintXmlResults;

//import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * Goal which executes Ckjm against the compiled classes in the project.
 *
 * @goal ckjm
 * @description Executes Ckjm against the classes.
 * @execute phase="compile"
 * @requiresDependencyResolution
 * @requiresProject
 *
 * @author <a href="http://www.codehaus.org/~wfay/">Wayne Fay</a>
 */
public class CkjmMojo extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;
	
	/**
	 * Format of the report. Valid options are plain and xml.
	 * @parameter default-value="plain"
	 */
	private String format;
	
	/**
	 * Name of the output file.
	 * @parameter default-value="ckjm.txt"
	 */
	private String outputName;
	
	/**
	 * Verbose. Default is false.
	 * @parameter expression="${verbose}" default-value="false"
	 */
	private boolean verbose;

    public void execute() throws MojoExecutionException
    {		
		if (format.equals("xml") && (!outputName.endsWith("xml")))
		{
			outputName = outputName + ".xml";
		}
		File outputFile = new File(outputDirectory, outputName);
		File classDir = new File(outputDirectory, "classes");
		
		String files[] = FileUtils.getFilesFromExtension(classDir.getPath(), new String[]{"class"} );
        if (files == null || files.length == 0) 
		{
            System.out.println("No class files in specified directory " + classDir);
        }
		else 
		{
            try 
			{
                OutputStream outputStream = new FileOutputStream(outputFile);

                if (format.equals("xml")) 
				{
                    PrintXmlResults outputXml = new PrintXmlResults(new PrintStream(outputStream));
                    outputXml.printHeader();
                    MetricsFilter.runMetrics(files, outputXml);
                    outputXml.printFooter();
                }
				else 
				{
                    PrintPlainResults outputPlain = new PrintPlainResults(new PrintStream(outputStream));
                    MetricsFilter.runMetrics(files, outputPlain);
                }

                outputStream.close();
            }
			catch (IOException ioe) 
			{
				throw new MojoExecutionException( "Ckjm error: " + ioe.toString(), ioe );
            }
        }
    }
}
