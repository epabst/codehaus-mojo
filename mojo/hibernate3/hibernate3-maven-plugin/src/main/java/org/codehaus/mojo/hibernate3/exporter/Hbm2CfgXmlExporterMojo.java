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

import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.codehaus.mojo.hibernate3.HibernateExporterMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generates hibernate.cfg.xml
 *
 * @author <a href="mailto:jreyes@hiberforum.org">Johann Reyes</a>
 * @version $Id$
 * @goal hbm2cfgxml
 * @execute phase="generate-resources"
 */
public class Hbm2CfgXmlExporterMojo
    extends HibernateExporterMojo
{
    /**
     * Default constructor.
     */
    public Hbm2CfgXmlExporterMojo()
    {
        addDefaultComponent( "target/hibernate3/generated-mappings", "jdbcconfiguration", false );
        addDefaultComponent( "target/hibernate3/generated-mappings", "jdbcconfiguration", true );
    }

// --------------------- Interface ExporterMojo ---------------------

    /**
     * Returns <b>hbm2cfgxml</b>.
     *
     * @return String goal's name
     */
    public String getName()
    {
        return "hbm2cfgxml";
    }

    /**
     * @see HibernateExporterMojo#configureExporter(org.hibernate.tool.hbm2x.Exporter)
     */
    protected Exporter configureExporter( Exporter exporter )
        throws MojoExecutionException
    {
        HibernateConfigurationExporter hce = (HibernateConfigurationExporter) super.configureExporter( exporter );
        hce.getProperties().setProperty( "ejb3", getComponentProperty( "ejb3", "false" ) );
        return hce;
    }

    /**
     * Instantiates a org.hibernate.tool.hbm2x.HibernateConfigurationExporter object.
     *
     * @return HibernateConfigurationExporter
     */
    protected Exporter createExporter()
    {
        return new HibernateConfigurationExporter();
    }
}
