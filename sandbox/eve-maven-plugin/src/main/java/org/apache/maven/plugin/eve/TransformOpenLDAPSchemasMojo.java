package org.apache.maven.plugin.eve;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.io.File;
import java.util.List;
import java.util.Iterator;
import java.util.Properties;

import org.apache.eve.tools.schema.EveSchemaToolTask;
import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PropertyUtils;

/**
 * @goal transform-open-ldap-schemas
 *
 * @requiresDependencyResolution
 *
 * @description Transforms a OpenLDAP schema to a Eve schema bean.
 *
 * @parameter
 *  name="schemaDirectory"
 *  type="java.lang.String"
 *  required="true"
 *  validator=""
 *  expression="#basedir/src/schema"
 *  description=""
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @version $Id$
 */
public class TransformOpenLDAPSchemasMojo
    extends AbstractPlugin
{
    /** the property key for the schema's owner */
    private static final String OWNER_KEY = "schema.owner";
    /** the property key for the schema's package name */
    private static final String PKGNAME_KEY = "schema.package.name";
    /** the property key for the schema's dependencies */
    private static final String DEPS_KEY = "schema.dependencies";


    public void execute( PluginExecutionRequest request, PluginExecutionResponse response )
        throws Exception
    {
        File schemaDirectory = new File( (String) request.getParameter( "schemaDirectory" ) );

        if ( !schemaDirectory.exists() )
        {
            System.err.println( "Schema directory '" + schemaDirectory.getAbsolutePath() + "' doesn't exists." );

            return;
        }

        List schemas = FileUtils.getFiles( schemaDirectory, "**/*.schema", "" );

        for ( Iterator it = schemas.iterator(); it.hasNext(); )
        {
            File file = (File) it.next();

            String name = file.getName().split( "\\." )[0];

            EveSchemaToolTask schemaTool = new EveSchemaToolTask();

            Properties props = getSchemaProperties( file.getParentFile(), name );

            schemaTool.setName( name );

            if ( props == null )
            {
                schemaTool.execute();
                continue;
            }

            System.err.println( name + " deps = " + props.get( DEPS_KEY ) );

            // ----------------------------------------------------------------------
            // Generate the beans
            // ----------------------------------------------------------------------
            //
            // Note that every property except the manditory schema name defaults
            // so it need not be set.  If a properties file is not found then all
            // the defaults are used.
            //
            // ----------------------------------------------------------------------

            if ( props.containsKey( OWNER_KEY ) )
            {
                schemaTool.setOwner( props.getProperty( OWNER_KEY ) );
            }

            if ( props.containsKey( DEPS_KEY ) )
            {
                schemaTool.setDependencies( props.getProperty( DEPS_KEY ) );
            }

            if ( props.containsKey( PKGNAME_KEY ) )
            {
                schemaTool.setDependencies( props.getProperty( DEPS_KEY ) );
            }

            schemaTool.execute();
        }
    }



    private Properties getSchemaProperties( File parentDir, String schema )
    {
        File propFile = new File( parentDir, schema + ".properties" );

        System.err.println( schema + " properties file = " + propFile.getAbsolutePath() );

        return PropertyUtils.loadProperties( propFile );
    }
}
