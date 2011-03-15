package org.codehaus.mojo.hibernate3.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class PlexusConfigurationUtils
{
// -------------------------- STATIC METHODS --------------------------

    public static PlexusConfiguration parseHibernateTool( PlexusConfiguration hibernatetool, String goalName,
                                                          ClassLoader classLoader, MavenSession session )
        throws PlexusConfigurationException
    {
        // let's create the expression evaluator first
        HibernateExpressionEvaluator evaluator = new HibernateExpressionEvaluator( session );

        // now let's extract some basic information
        PlexusConfiguration defaultConfiguration = findConfiguration( hibernatetool, evaluator );
        List<PlexusConfiguration> goals = new Vector<PlexusConfiguration>();
        List<PlexusConfiguration> properties = new Vector<PlexusConfiguration>();

        for ( PlexusConfiguration child : hibernatetool.getChildren() )
        {
            if ( "goal".equals( PropertyUtils.getProperty( child.getName() ) ) )
            {
                goals.add( child );
            }
            else if ( !isConfiguration( child ) )
            {
                properties.add( child );
            }
        }

        PlexusConfiguration target = getTarget( "hibernatetool", classLoader );
        for ( PlexusConfiguration originalGoal : goals )
        {
            if ( "run".equals( goalName ) || originalGoal.getName().equals( goalName ) )
            {
                PlexusConfiguration task = getHibernateTask( hibernatetool, evaluator );
                setDestinationDirectory( task, originalGoal, target, session, evaluator );
                setHibernateConfiguration( task, originalGoal, defaultConfiguration, evaluator );
                setHibernateGoal( task, originalGoal, evaluator );
                setHibernateProperties( task, properties, evaluator );
                target.addChild( task );
            }
        }
        return target;
    }

    public static PlexusConfiguration parseInstrument( PlexusConfiguration instrument, ClassLoader classLoader,
                                                       MavenSession session )
        throws PlexusConfigurationException
    {
        // let's create the expression evaluator first
        HibernateExpressionEvaluator evaluator = new HibernateExpressionEvaluator( session );

        // let's create the task now
        PlexusConfiguration target = getTarget( "instrument", classLoader );
        PlexusConfiguration task = getHibernateTask( instrument, evaluator );
        target.addChild( copyChild( task.getName(), task, evaluator ) );
        return target;
    }

    private static PlexusConfiguration copyChild( String key, PlexusConfiguration from,
                                                  HibernateExpressionEvaluator evaluator )
        throws PlexusConfigurationException
    {
        XmlPlexusConfiguration to = new XmlPlexusConfiguration( from.getName() );

        // let's copy required attributes
        String[] requiredAttributeNames = PropertyUtils.getPropertyArray( key + ".attributes" );
        for ( String attributeName : requiredAttributeNames )
        {
            String attributeValueKey = PropertyUtils.getProperty( key + "." + attributeName + ".default" );
            String attributeValue = evaluator.evaluateString( attributeValueKey );
            to.setAttribute( attributeName, attributeValue );
        }

        // let's copy the attributes
        for ( String attributeName : from.getAttributeNames() )
        {
            if ( !"implementation".equals( attributeName ) )
            {
                String attributeValueKey = from.getAttribute( attributeName );
                String attributeValue = evaluator.evaluateString( attributeValueKey );
                to.setAttribute( attributeName, attributeValue );
            }
        }

        // copy value
        to.setValue( from.getValue() );

        // set up the required children
        String[] requiredChildren = PropertyUtils.getPropertyArray( key + ".children" );
        for ( String requiredChild : requiredChildren )
        {
            from.getChild( requiredChild );
        }

        // copy the children
        for ( PlexusConfiguration configuration : from.getChildren() )
        {
            to.addChild( copyChild( key + "." + configuration.getName(), configuration, evaluator ) );
        }

        // return configured child
        return to;
    }

    private static PlexusConfiguration findConfiguration( PlexusConfiguration configuration,
                                                          HibernateExpressionEvaluator evaluator )
        throws PlexusConfigurationException
    {
        for ( PlexusConfiguration child : configuration.getChildren() )
        {
            if ( isConfiguration( child ) )
            {
                String name = child.getName();

                // let's create the new goal PlexusConfiguration
                XmlPlexusConfiguration newConfiguration = new XmlPlexusConfiguration( name );

                // let's copy required attributes
                String[] requiredAttributeNames = PropertyUtils.getPropertyArray( name + ".attributes" );
                for ( String attributeName : requiredAttributeNames )
                {
                    String attributeValueKey = PropertyUtils.getProperty( name + "." + attributeName + ".default" );
                    String attributeValue = evaluator.evaluateString( attributeValueKey );
                    if ( FileUtils.fileExists( attributeValue ) )
                    {
                        newConfiguration.setAttribute( attributeName, attributeValue );
                    }
                }

                // let's copy the attributes
                for ( String attributeName : child.getAttributeNames() )
                {
                    String attributeValue = evaluator.evaluateString( child.getAttribute( attributeName ) );
                    newConfiguration.setAttribute( attributeName, attributeValue );
                }

                // let's copy the children
                for ( PlexusConfiguration c : child.getChildren() )
                {
                    newConfiguration.addChild( c );
                }

                // return the configuration now
                return newConfiguration;
            }
        }
        return null;
    }

    private static PlexusConfiguration getHibernateTask( PlexusConfiguration configuration,
                                                         HibernateExpressionEvaluator evaluator )
        throws PlexusConfigurationException
    {
        String name = configuration.getName();

        // now create the task container
        XmlPlexusConfiguration task = new XmlPlexusConfiguration( name );

        // let's copy required attributes
        String[] requiredAttributeNames = PropertyUtils.getPropertyArray( name + ".attributes" );
        for ( String attributeName : requiredAttributeNames )
        {
            String attributeValue = PropertyUtils.getProperty( name + "." + attributeName + ".default" );
            task.setAttribute( attributeName, evaluator.evaluateString( attributeValue ) );
        }

        // let's copy the attributes
        for ( String attributeName : configuration.getAttributeNames() )
        {
            if ( !"implementation".equals( attributeName ) )
            {
                String attributeValue = evaluator.evaluateString( configuration.getAttribute( attributeName ) );
                task.setAttribute( attributeName, attributeValue );
            }
        }

        // return the configured task now
        return task;
    }

    private static PlexusConfiguration getTarget( String taskName, ClassLoader classLoader )
    {
        XmlPlexusConfiguration target = new XmlPlexusConfiguration( "target" );

        // let's find the implementation
        String[] implementations = PropertyUtils.getPropertyArray( taskName + ".implementations" );
        for ( String implementation : implementations )
        {
            try
            {
                String className = PropertyUtils.getProperty( taskName + "." + implementation + ".implementation" );
                classLoader.loadClass( className );

                XmlPlexusConfiguration taskdef = new XmlPlexusConfiguration( "taskdef" );
                taskdef.setAttribute( "name", taskName );
                taskdef.setAttribute( "classname", className );
                target.addChild( taskdef );

                break;
            }
            catch ( ClassNotFoundException e )
            {
                // NOOP
            }
        }

        // now return the target
        return target;
    }

    private static boolean isConfiguration( PlexusConfiguration configuration )
    {
        return "configuration".equals( PropertyUtils.getProperty( configuration.getName() ) );
    }

    private static void setDestinationDirectory( PlexusConfiguration task, PlexusConfiguration goal,
                                                 PlexusConfiguration target, MavenSession session,
                                                 HibernateExpressionEvaluator evaluator )
        throws PlexusConfigurationException
    {
        // first let's find out where is the destination directory
        String destdir = goal.getAttribute( "destdir" );
        if ( destdir == null )
        {
            destdir = task.getAttribute( "destdir" );
        }
        if ( destdir == null )
        {
            destdir = PropertyUtils.getProperty( goal.getName() + ".destdir" );
        }
        destdir = evaluator.evaluateString( destdir );

        // let's see it the destination directory needs to be added to sources
        if ( "true".equals( PropertyUtils.getProperty( goal.getName() + ".addtosource" ) ) )
        {
            session.getCurrentProject().addCompileSourceRoot( destdir );
        }

        // create the directory
        XmlPlexusConfiguration mkdir = new XmlPlexusConfiguration( "mkdir" );
        mkdir.setAttribute( "dir", destdir );
        target.addChild( mkdir );

        // and add it to the task
        ( (XmlPlexusConfiguration) task ).setAttribute( "destdir", destdir );
    }

    private static void setHibernateConfiguration( PlexusConfiguration task, PlexusConfiguration originalGoal,
                                                   PlexusConfiguration defaultConfiguration,
                                                   HibernateExpressionEvaluator evaluator )
        throws PlexusConfigurationException
    {
        PlexusConfiguration configuration = findConfiguration( originalGoal, evaluator );
        if ( configuration == null )
        {
            configuration = defaultConfiguration;
        }
        task.addChild( configuration );
    }

    private static void setHibernateGoal( PlexusConfiguration task, PlexusConfiguration originalGoal,
                                          HibernateExpressionEvaluator evaluator )
        throws PlexusConfigurationException
    {
        String goalName = originalGoal.getName();

        // let's create the new goal PlexusConfiguration
        PlexusConfiguration goal = task.getChild( goalName );

        // let's copy required attributes
        String[] requiredAttributeNames = PropertyUtils.getPropertyArray( goalName + ".attributes" );
        for ( String attributeName : requiredAttributeNames )
        {
            String attributeValue =
                evaluator.evaluateString( PropertyUtils.getProperty( goalName + "." + attributeName + ".default" ) );
            ( (XmlPlexusConfiguration) goal ).setAttribute( attributeName, attributeValue );
        }

        // let's copy the attributes
        for ( String attributeName : originalGoal.getAttributeNames() )
        {
            if ( !"destdir".equals( attributeName ) )
            {
                String attributeValue = evaluator.evaluateString( originalGoal.getAttribute( attributeName ) );
                ( (XmlPlexusConfiguration) goal ).setAttribute( attributeName, attributeValue );
            }
        }

        // let's copy the children
        for ( PlexusConfiguration child : originalGoal.getChildren() )
        {
            if ( !isConfiguration( child ) )
            {
                goal.addChild( child );
            }
        }

        // let's copy the value
        ( (XmlPlexusConfiguration) goal ).setValue( originalGoal.getValue() );
    }

    private static void setHibernateProperties( PlexusConfiguration task, List<PlexusConfiguration> properties,
                                                HibernateExpressionEvaluator evaluator )
        throws PlexusConfigurationException
    {
        for ( PlexusConfiguration property : properties )
        {
            task.addChild( copyChild( property.getName(), property, evaluator ) );
        }
    }

// --------------------------- CONSTRUCTORS ---------------------------

    private PlexusConfigurationUtils()
    {
        // NOOP
    }
}
