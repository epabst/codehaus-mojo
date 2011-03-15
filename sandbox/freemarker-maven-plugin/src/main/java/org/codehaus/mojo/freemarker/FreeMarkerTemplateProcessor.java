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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.freemarker.configurations.InputConfigurationEntry;
import org.codehaus.mojo.freemarker.configurations.OutputConfigurationEntry;
import org.codehaus.mojo.freemarker.datamodels.DateDataModel;
import org.codehaus.mojo.freemarker.loaders.PropertiesDataModelLoader;
import org.codehaus.mojo.freemarker.loaders.XMLDataModelLoader;
import org.codehaus.plexus.util.FileUtils;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ObjectConstructor;

/**
 * 
 * @author jimisola <public@jimisola.com>
 * 
 */
public class FreeMarkerTemplateProcessor
{
    private Log log;

    private File[] templateDirectories;

    private InputConfigurationEntry[] inputs;

    private OutputConfigurationEntry output;

    public FreeMarkerTemplateProcessor(int indent, Log log, File[] templateDirectories, InputConfigurationEntry[] inputs,
            OutputConfigurationEntry output)
    {
        super();
        this.log = log;
        this.templateDirectories = templateDirectories;
        this.inputs = inputs;
        this.output = output;
    }

    private final Configuration getConfiguration() throws MojoExecutionException
    {
        Configuration cfg = new Configuration();
        File dir = null;
        
        try
        {
            TemplateLoader[] loaders = new TemplateLoader[this.templateDirectories.length];
            
            for (int i = 0; i < this.templateDirectories.length; i++)
            {
                dir = this.templateDirectories[i];

                this.log.info("adding template directory: " + dir.getAbsolutePath());
                
                FileTemplateLoader ftl = new FileTemplateLoader(dir);
                loaders[i] = ftl;
            }
            
            MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
            
            cfg.setTemplateLoader(mtl);

            // specify how templates will see the data model
            // advanced topic - following instructions from freemarker.org
            cfg.setObjectWrapper(new DefaultObjectWrapper());

        }
        catch (IOException ex)
        {
            throw new MojoExecutionException("Unable to create configuration (does directory '" + dir.getAbsolutePath() +"' exist?)", ex);
        }

        return cfg;

    }

    private Template getTemplate(Configuration cfg, File templateFile) throws MojoExecutionException
    {
        try
        {
            return cfg.getTemplate(templateFile.getName());
        }
        catch (IOException ex)
        {
            throw new MojoExecutionException("Unable to create template from file; " + templateFile, ex);
        }
    }
    
    public void generate() throws MojoExecutionException, MojoFailureException
    {
        Map root = new HashMap();

        Configuration cfg = this.getConfiguration();
        

        
        for (int i = 0 ; i < this.inputs.length ; i++)
        {
        	InputConfigurationEntry input = this.inputs[i];
        	
            if (input.getLoader().equals("xml"))
            {
                root.put(input.getContextVariable(), new XMLDataModelLoader(this.log).getModel(input.getInputFile()));
                this.log.info("added xml data model to context variable '" + input.getContextVariable() + "'");            	
            }
            else if (input.getLoader().equals("properties"))
            {
                root.put(input.getContextVariable(), new PropertiesDataModelLoader(this.log).getModel(input.getInputFile()));
                this.log.info("added properties data model to context variable '" + input.getContextVariable() + "'");
            }
            else if (input.getLoader().equals("date"))
            {
                root.put(input.getContextVariable(), new DateDataModel());
                this.log.info("added date data model to context variable '" + input.getContextVariable() + "'");
            }
            else if (input.getLoader().equals("object-constructor"))
            {
                root.put(input.getContextVariable(), new ObjectConstructor());
                this.log.info("added object constructor model to context variable '" + input.getContextVariable() + "'");
            }
            else
            {
                new MojoExecutionException("Unknown loader type: " + input.getLoader() + "for context variable '" + input.getInputFile() +  "' and  file: " + input.getInputFile());
            }
        }

        // create template
        this.log.info("using template: " + this.output.getTemplateFile());
        Template template = this.getTemplate(cfg, this.output.getTemplateFile());
        
        this.writeOutput(root, template, this.output.getOutputFile());
    }

    private void writeOutput(Map/*<String, Object>*/ root, Template template, File outputFile) throws MojoExecutionException
    {
        try
        {
            Writer out;
            boolean closeOut = false;
            
            if (outputFile.getName().equals("stdout"))
            {
                this.log.info("writing result to <stdout>");        

            	out = new OutputStreamWriter(System.out);
            }
            else if (outputFile.getName().equals("stderr"))
            {
                this.log.info("writing result to <stderr>");        

            	out = new OutputStreamWriter(System.err);
            }
            else
            {
                this.log.info("writing result to file: " + outputFile.getAbsolutePath());        
            	
                FileUtils.mkdir(FileUtils.dirname(outputFile.getAbsolutePath()));
                
                out = new OutputStreamWriter(new FileOutputStream(outputFile));
                closeOut = true;
            }

            template.process(root, out);
            out.flush();
            
            if (closeOut)
            {
                out.close();
            }
            
        }
        catch (IOException ex)
        {
            throw new MojoExecutionException("Unable to write output to file due to an IO error; " + outputFile, ex);
        }
        catch (TemplateException ex)
        {
            throw new MojoExecutionException("Unable to write output to file due to a template error; " + outputFile, ex);            
        }
    }
}