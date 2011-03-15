/*
 * Copyright 2005 The Codehaus.
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
package org.codehaus.mojo.castor;

import java.util.Iterator;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * A mojo that uses Castor MappingTool to generate mapping files from a set of Classes. <a
 * href="http://castor.codehaus.org/javadoc/org/exolab/castor/tools/MappingTool.html"> MappingTool</a>.
 * 
 * @goal mappings
 * @phase process-classes
 * @author nicolas <nicolas@apache.org>
 */
public class MappingsMojo
    extends AbstractMappingMojo
{
    /**
     * A set of Java classes for which a mapping file should be generated.
     * 
     * @parameter
     * @required
     */
    private Map<String, String> classes;

    /**
     * A Java class name.
     */
    private String className;

    /**
     * Name of the mapping file to be generated.
     */
    private String mappingName;

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( classes.isEmpty() )
        {
            getLog().warn( "No mapping set" );
        }

        for ( Iterator<Map.Entry<String, String>> iterator = classes.entrySet().iterator(); iterator.hasNext(); )
        {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            className = (String) entry.getKey();
            mappingName = (String) entry.getValue();
            super.execute();
        }
    }

    /**
     * Returns the class name.
     * 
     * @return the classname
     */
    protected String getClassName()
    {
        return className;
    }

    /**
     * Returns the mapping file name.
     * 
     * @return the mappingName
     */
    protected String getMappingName()
    {
        return mappingName;
    }
}
