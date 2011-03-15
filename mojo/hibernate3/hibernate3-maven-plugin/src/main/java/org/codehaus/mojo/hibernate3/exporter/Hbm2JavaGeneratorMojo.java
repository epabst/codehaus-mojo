package org.codehaus.mojo.hibernate3.exporter;

/*
 * Copyright 2005 Johann Reyes.
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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.hibernate3.HibernateExporterMojo;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.POJOExporter;

/**
 * Generates Java classes from set of *.hbm.xml files
 *
 * @author <a href="mailto:jreyes@hiberforum.org">Johann Reyes</a>
 * @version $Id$
 * @goal hbm2java
 * @phase generate-sources
 * @execute phase="process-test-resources"
 */
public class Hbm2JavaGeneratorMojo
    extends HibernateExporterMojo
{
    /**
     * Default constructor.
     */
    public Hbm2JavaGeneratorMojo()
    {
        addDefaultComponent( "target/hibernate3/generated-sources", "configuration", false );
        addDefaultComponent( "target/hibernate3/generated-sources", "annotationconfiguration", true );
    }

// --------------------- Interface ExporterMojo ---------------------

    /**
     * Returns <b>hbm2java</b>.
     *
     * @return String goal's name
     */
    public String getName()
    {
        return "hbm2java";
    }

    /**
     * @see HibernateExporterMojo#configureExporter(org.hibernate.tool.hbm2x.Exporter)
     */
    protected Exporter configureExporter( Exporter exp )
        throws MojoExecutionException
    {
        super.getComponent().setCompileSourceRoot( true );
        POJOExporter exporter = (POJOExporter) super.configureExporter( exp );
        
        // now set the extra properties for the POJO Exporter
        exporter.getProperties().setProperty( "ejb3", getComponentProperty( "ejb3", "false" ) );
        exporter.getProperties().setProperty( "jdk5", getComponentProperty( "jdk5", "false" ) );
        return exporter;
    }

    /**
     * Instantiates a org.hibernate.tool.hbm2x.POJOExporter object.
     *
     * @return POJOExporter
     */
    protected Exporter createExporter()
    {
        return new POJOExporter();
    }
}