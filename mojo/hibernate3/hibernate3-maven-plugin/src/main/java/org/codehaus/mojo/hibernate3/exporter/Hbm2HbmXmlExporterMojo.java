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
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.codehaus.mojo.hibernate3.HibernateExporterMojo;

/**
 * Generates a set of hbm.xml files
 *
 * @author <a href="mailto:jreyes@hiberforum.org">Johann Reyes</a>
 * @version $Id$
 * @goal hbm2hbmxml
 * @execute phase="generate-resources"
 */
public class Hbm2HbmXmlExporterMojo
    extends HibernateExporterMojo
{
    /**
     * Default constructor.
     */
    public Hbm2HbmXmlExporterMojo()
    {
        addDefaultComponent( "target/hibernate3/generated-mappings", "jdbcconfiguration", false );
        addDefaultComponent( "target/hibernate3/generated-mappings", "jdbcconfiguration", true );
    }

// --------------------- Interface ExporterMojo ---------------------

    /**
     * Returns <b>hbm2hbmxml</b>.
     *
     * @return String goal's name
     */
    public String getName()
    {
        return "hbm2hbmxml";
    }

    /**
     * Instantiates a org.hibernate.tool.hbm2x.HibernateMappingExporter object.
     *
     * @return HibernateMappingExporter
     */
    protected final Exporter createExporter()
    {
        return new HibernateMappingExporter();
    }
}
