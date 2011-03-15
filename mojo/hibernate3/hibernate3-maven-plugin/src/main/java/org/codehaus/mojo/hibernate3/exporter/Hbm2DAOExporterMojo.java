package org.codehaus.mojo.hibernate3.exporter;

/*
 * Copyright 2007 Johann Reyes.
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
import org.hibernate.tool.hbm2x.DAOExporter;

/**
 * Base class for the different hibernate3 goals based on the Ant tasks of hibernate tools.
 *
 * @author <a href="mailto:jreyes@hiberforum.org">Johann Reyes</a>
 * @version $Id$
 * @goal hbm2dao
 * @phase generate-sources
 * @execute phase="process-resources"
 */
public class Hbm2DAOExporterMojo
    extends Hbm2JavaGeneratorMojo
{
    /**
     * Default constructor.
     */
    public Hbm2DAOExporterMojo()
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
        return "hbm2dao";
    }

    /**
     * Instantiates a org.hibernate.tool.hbm2x.DAOExporter object.
     *
     * @return DAOExporter
     */
    protected Exporter createExporter()
    {
        return new DAOExporter();
    }
}
