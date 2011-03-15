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
package org.codehaus.mojo.freemarker.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;

/**
 * 
 * @author jimisola <public@jimisola.com>
 * 
 */
public class PropertiesDataModelLoader extends DataModelLoader
{
    public PropertiesDataModelLoader(Log log)
    {
		super(log);
	}

	public TemplateModel getModel(File inputFile) throws MojoExecutionException
    {
        Properties properties = new Properties();
        
        try
        {
            this.log.info("creating properties data model from file: " + inputFile);

            FileInputStream fis = new FileInputStream(inputFile);
            
            properties.load(fis);
            
            SimpleHash sh = new SimpleHash(properties);

            fis.close();
            
            return sh;
        }
        catch (Exception ex)
        {
            throw new MojoExecutionException("Unable to create properties model from file: " + inputFile, ex);
        }
    }
	
	public String getLoaderType()
	{
		return "properties";
	}
	
}