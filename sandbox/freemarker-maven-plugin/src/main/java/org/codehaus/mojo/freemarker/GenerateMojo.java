/*
The MIT License

Copyright (c) 2006, The Codehaus http://www.codehaus.org/

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package org.codehaus.mojo.freemarker;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.freemarker.configurations.InputConfigurationEntry;
import org.codehaus.mojo.freemarker.configurations.OutputConfigurationEntry;

/**
 * 
 * generates output using the specified template and configured data model
 * 
 * @goal generate
 * @description FreeMarker plugin
 * @author jimisola <public@jimisola.com>
 *
 */
public class GenerateMojo extends AbstractMojo
{
    /**
     * Configures input data for the templates (i.e. input files, their types and assigned contextVariable) [note: it's an array]
     * @parameter property="inputs"
     * @require
     */
    private InputConfigurationEntry[] cfgInputs;

    /**
     * Configures output data (i.e. template file and output file) [note: it's an array]
     * @parameter property="output"
     * @require
     */
    private OutputConfigurationEntry cfgOutput;
    
    /**
     * Sets one or more template directories (template files are specified relative to this)
     * @parameter
     * @require
     */
    private File[] templateDirectories;
        
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        // output configuration
        this.getLog().info("Processing Freemarker inputs and template...");
        
        if (((this.cfgInputs == null) || (this.cfgInputs.length < 1)))
        {
            throw new MojoExecutionException("unable to continue: input(s) not configured");
        }
        
        if (this.cfgOutput == null)
        {
            throw new MojoExecutionException("unable to continue: output not configured");
        }
        
        FreeMarkerTemplateProcessor fmtp = new FreeMarkerTemplateProcessor(1, this.getLog(), this.templateDirectories, this.cfgInputs, this.cfgOutput);
        fmtp.generate();
    }
    
    public void setInputs(InputConfigurationEntry[] inputs)
    {
        this.cfgInputs = inputs;
    }

    public void setOutput(OutputConfigurationEntry output)
    {
        this.cfgOutput = output;
    }
}