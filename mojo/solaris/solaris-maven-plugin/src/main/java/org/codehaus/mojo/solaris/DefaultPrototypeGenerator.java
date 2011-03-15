package org.codehaus.mojo.solaris;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.LinkedList;

/**
 * @author <a href="mailto:trygvis@codehaus.org">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultPrototypeGenerator
    extends AbstractLogEnabled
    implements PrototypeGenerator
{
    // -----------------------------------------------------------------------
    // PrototypeGenerator Implementation
    // -----------------------------------------------------------------------

    public Iterator generatePrototype( File packageRoot,
                                       List prototype,
                                       Defaults directoryDefaults,
                                       Defaults fileDefaults )
        throws MojoFailureException, MojoExecutionException
    {
        prototype = getEntries( prototype );

        getLogger().debug( "Generating prototype...");

        // -----------------------------------------------------------------------
        // Validate
        // -----------------------------------------------------------------------

        for ( Iterator it = prototype.iterator(); it.hasNext(); )
        {
            Object entry = it.next();

            Defaults defaults;

            if ( entry instanceof DirectoryEntry || entry instanceof DirectoryCollection )
            {
                defaults = directoryDefaults;
            }
            else
            {
                defaults = fileDefaults;
            }

            ((AbstractPrototypeEntry) entry).validate( defaults );
        }

        AbstractEntryCollection defaultDirectoryCollection = new DirectoryCollection(
            directoryDefaults.getPkgClass(),
            directoryDefaults.getMode(),
            directoryDefaults.getUser(),
            directoryDefaults.getGroup(),
            directoryDefaults.isRelative(),
            directoryDefaults.getIncludes(),
            directoryDefaults.getExcludes() );

        AbstractEntryCollection defaultFileCollection = new FileCollection(
            fileDefaults.getPkgClass(),
            fileDefaults.getMode(),
            fileDefaults.getUser(),
            fileDefaults.getGroup(),
            fileDefaults.isRelative(),
            fileDefaults.getIncludes(),
            fileDefaults.getExcludes() );

        prototype.add( 0, defaultDirectoryCollection );
        prototype.add( 1, defaultFileCollection );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        PrototypeEntryList collectedPrototypeEntries = new PrototypeEntryList();

        // -----------------------------------------------------------------------
        // Iterate through the prototype
        // -----------------------------------------------------------------------

        for ( Iterator it = prototype.iterator(); it.hasNext(); )
        {
            Object o = it.next();

            if ( o instanceof SinglePrototypeEntry )
            {
                SinglePrototypeEntry entry = (SinglePrototypeEntry) o;

                getLogger().debug( "Adding entry: " );
                getLogger().debug( " " + entry );

                if ( entry instanceof DirectoryEntry )
                {
                    entry.validate( directoryDefaults );
                }
                else
                {
                    entry.validate( fileDefaults );
                }

//                entry.adjustPath( null );

                collectedPrototypeEntries.add( entry );

                continue;
            }

            AbstractEntryCollection collection = (AbstractEntryCollection) o;
            getLogger().debug( "Expanding collection: " + collection );
            getLogger().debug( " Basedir: " + packageRoot );

            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir( packageRoot );
            scanner.setIncludes( collection.getIncludes() );
            scanner.setExcludes( collection.getExcludes() );
            scanner.scan();

            PrototypeEntryList entries = expandCollection(collection, scanner);

            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( " Found " + entries.size() + " entries: " );
                for (Iterator it2 = entries.iterator(); it2.hasNext();)
                {
                    SinglePrototypeEntry entry = (SinglePrototypeEntry) it2.next();

                    getLogger().debug( "  " + ToStringBuilder.reflectionToString( entry, ToStringStyle.SHORT_PREFIX_STYLE ) );
                }
            }

            collectedPrototypeEntries.addAll( entries );
        }

        return collectedPrototypeEntries.iterator();
    }

    private PrototypeEntryList expandCollection( AbstractEntryCollection collection, DirectoryScanner scanner )
        throws MojoExecutionException
    {
        String[] paths;

        if ( collection instanceof DirectoryCollection )
        {
            paths = scanner.getIncludedDirectories();
        }
        else
        {
            paths = scanner.getIncludedFiles();
        }

        PrototypeEntryList entries = new PrototypeEntryList();

        for ( int i = 0; i < paths.length; i++ )
        {
            String path = paths[i];

            if ( path.equals("") || path.equals( "/" ) )
            {
                continue;
            }

            entries.add( collection.getEntryForPath(path) );
        }

        return entries;
    }

    private List getEntries( List original )
    {
        return original == null ? new LinkedList() : original;
    }

    private class PrototypeEntryList
    {
        private TreeSet collectedPrototypeEntries;

        private PrototypeEntryList()
        {
            collectedPrototypeEntries = new TreeSet( new Comparator()
            {
                public int compare( Object o, Object o1 )
                {
                    SinglePrototypeEntry a = (SinglePrototypeEntry) o;
                    SinglePrototypeEntry b = (SinglePrototypeEntry) o1;
                    return a.getPath().compareTo( b.getPath() );
                }
            } );
        }

        public void add( SinglePrototypeEntry entry )
        {
            collectedPrototypeEntries.remove( entry );
            collectedPrototypeEntries.add( entry );
        }

        public void addAll( PrototypeEntryList list )
        {
            for (Iterator it = list.iterator(); it.hasNext(); ) {
                SinglePrototypeEntry entry = (SinglePrototypeEntry) it.next();

                collectedPrototypeEntries.remove( entry );
                collectedPrototypeEntries.add( entry );
            }
        }

        public int size()
        {
            return collectedPrototypeEntries.size();
        }

        public Iterator iterator()
        {
            return collectedPrototypeEntries.iterator();
        }
    }
}
