package org.codehaus.mojo;

/*
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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.net.URL;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Abstract class to easily implement a Jelly plugin.
 * A typical 'one goal' jelly plugin project can be fired
 * with the provided maven archetype 'jellymojo-maven-archetype'
 *
 * @description Abstract Jelly Mojo
 * @author <a href="mailto:eburghard@free.fr">Éric BURGHARD</a>
 * @version $Id$
 */
public abstract class AbstractJellyMojo
    extends AbstractMojo
{
    protected HashMap params = null;
 
    /**
     * Used to populate a map containing the plugin's parameters
     * in concrete classes. Plugin's parameters are declared
     * by the means of private members's javadoc annotations
     */
    public void setParams()
    {
    }
    
    /**
     * Execute the .jelly script associated with the concrete class
     */
    public void execute()
        throws MojoExecutionException
    {
        try
        {
    		final String scriptName = this.getClass().getSimpleName() + ".jelly";
        	if (scriptName == null || scriptName.equals(""))
        		throw new MojoExecutionException( "script name unspecified");
        	URL fileName=this.getClass().getResource(scriptName);
        	if (fileName == null)
        		throw new MojoExecutionException( scriptName + " not found");
        	
        	setParams();
        	
            // add plugin's parameters to the groovy context
        	JellyContext context = new JellyContext();
            if (params != null) {
            	Iterator i = params.keySet().iterator();
            	while (i.hasNext()) {
            		String key = (String) i.next();
            		context.setVariable(key, params.get(key));
            	}
            }
            
            XMLOutput xmlOutput = XMLOutput.createXMLOutput(System.out);
            context.runScript(fileName, xmlOutput);
            xmlOutput.flush();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Mojo error occurred:", e );
        }
    }
}