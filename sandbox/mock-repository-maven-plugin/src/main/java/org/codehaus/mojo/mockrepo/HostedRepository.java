package org.codehaus.mojo.mockrepo;

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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.mojo.mockrepo.utils.MockRepoUtils;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Locally hosted repository.
 * @since 1.0-alpha-1
 * @author Stephen Connolly
 */
public class HostedRepository
    implements Repository
{
    private Map/*<String,Metadata>*/ metadata = new HashMap();

    private SortedMap/*<String,Content>*/ content = new TreeMap();

    private final Object lock = new Object();

    public Metadata getMetadata( String path )
    {
        synchronized ( lock )
        {
            return (Metadata) metadata.get( path );
        }
    }

    public Content getContent( String path )
    {
        synchronized ( lock )
        {
            return (Content) content.get( path );
        }
    }

    public List getChildPaths( String path )
    {
        System.out.println("ls " + path);
        if ( path.length() > 0 && !path.endsWith( "/" ) )
        {
            System.out.println("a file");
            return Collections.EMPTY_LIST;
        }
        Set results = new TreeSet();
        synchronized ( lock )
        {
            if ( content.containsKey( path ) )
            {
                System.out.println("really a file");
                return Collections.EMPTY_LIST;
            }
            final Iterator i = content.tailMap( path ).keySet().iterator();
            while ( i.hasNext() )
            {
                String s = (String) i.next();
                if ( !s.startsWith( path ) )
                {
                    break;
                }
                s = s.substring( path.length() );
                int index = s.indexOf( '/' );
                if ( index == -1 )
                {
                    results.add( s );
                }
                else
                {
                    results.add( s.substring( 0, index+1 ) );
                }
            }
        }
        return Collections.unmodifiableList( new ArrayList( results ) );
    }

    public Model deployPom( Content content )
        throws IOException
    {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        XmlStreamReader fileReader = null;
        try
        {
            fileReader = ReaderFactory.newXmlReader( content.getInputStream() );
            final Model model = reader.read( fileReader );
            String groupId = MockRepoUtils.getGroupId( model );
            String artifactId = MockRepoUtils.getArtifactId( model );
            String version = MockRepoUtils.getVersion( model );
            String packaging = model.getPackaging() == null ? "jar" : model.getPackaging();
            final String basePath = MockRepoUtils.getGAVPathName( groupId, artifactId, version );

            deploy( basePath + ".pom", content );

            updateVersionMetadata( groupId, artifactId, version );

            if ( "maven-plugin".equals( packaging ) )
            {
                updatePluginMetadata( model, groupId, artifactId );
            }

            return model;

        }
        catch ( XmlPullParserException e )
        {
            IOException ioe = new IOException( "Unable to parse pom" );
            ioe.initCause( e );
            throw ioe;
        }
        finally
        {
            if ( fileReader != null )
            {
                try
                {
                    fileReader.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }

    }

    public void deploy( String path, Content content )
        throws IOException
    {
        InputStream is = content.getInputStream();
        try
        {
            MessageDigest md5Digest = MessageDigest.getInstance( "MD5" );
            MessageDigest sha1Digest = MessageDigest.getInstance( "SHA1" );
            md5Digest.reset();
            sha1Digest.reset();
            byte[] buffer = new byte[8192];
            int read;
            while ( ( read = is.read( buffer ) ) > 0 )
            {
                md5Digest.update( buffer, 0, read );
                sha1Digest.update( buffer, 0, read );
            }
            final String md5 = StringUtils.leftPad( new BigInteger( 1, md5Digest.digest() ).toString( 16 ), 32, "0" );
            final String sha1 = StringUtils.leftPad( new BigInteger( 1, sha1Digest.digest() ).toString( 16 ), 40, "0" );
            synchronized ( lock )
            {
                this.content.put( path, content );
                this.content.put( path + ".md5", new ByteArrayContent( md5.getBytes(), content.getLastModified() ) );
                this.content.put( path + ".sha1", new ByteArrayContent( sha1.getBytes(), content.getLastModified() ) );
            }
        }
        catch ( NoSuchAlgorithmException e )
        {
            IOException ioe = new IOException( "Unable to calculate hashes" );
            ioe.initCause( e );
            throw ioe;
        }
        finally
        {
            try
            {
                is.close();
            }
            catch ( IOException e )
            {
                // ignore
            }
        }

    }

    private void updatePluginMetadata( Model model, String groupId, String artifactId )
    {
        synchronized ( lock )
        {
            Metadata metadata = (Metadata) this.metadata.get( MockRepoUtils.getGAVPath( groupId, null, null ) );
            if ( metadata == null )
            {
                metadata = new Metadata();
                this.metadata.put( MockRepoUtils.getGAVPath( groupId, null, null ), metadata );
            }
            boolean found = false;
            Iterator k = metadata.getPlugins().iterator();
            while ( !found && k.hasNext() )
            {
                found = StringUtils.equals( artifactId, ( (Plugin) k.next() ).getArtifactId() );
            }
            if ( !found )
            {
                Plugin plugin = new Plugin();
                plugin.setArtifactId( artifactId );
                plugin.setName( model.getName() );
                // TODO proper goal-prefix determination
                // ugh! this is incredibly hacky and does not handle some fool that sets the goal prefix in
                // a parent pom... ok unlikely, but stupid is as stupid does
                boolean havePrefix = false;
                final Build build = model.getBuild();
                if ( build != null && build.getPlugins() != null )
                {
                    havePrefix = setPluginGoalPrefixFromConfiguration( plugin, build.getPlugins() );
                }
                if ( !havePrefix && build != null && build.getPluginManagement() != null
                    && build.getPluginManagement().getPlugins() != null )
                {
                    havePrefix =
                        setPluginGoalPrefixFromConfiguration( plugin, build.getPluginManagement().getPlugins() );
                }
                if ( !havePrefix && artifactId.startsWith( "maven-" ) && artifactId.endsWith( "-plugin" ) )
                {
                    plugin.setPrefix( StringUtils.chompLast( artifactId, "-plugin" ).substring( "maven-".length() ) );
                    havePrefix = true;
                }
                if ( !havePrefix && artifactId.endsWith( "-maven-plugin" ) )
                {
                    plugin.setPrefix( StringUtils.chompLast( artifactId, "-maven-plugin" ) );
                    havePrefix = true;
                }
                if ( !havePrefix )
                {
                    plugin.setPrefix( artifactId );
                }
                metadata.addPlugin( plugin );
            }
        }
    }

    private boolean setPluginGoalPrefixFromConfiguration( Plugin plugin, List plugins )
    {
        Iterator iterator = plugins.iterator();
        while ( iterator.hasNext() )
        {
            org.apache.maven.model.Plugin def = (org.apache.maven.model.Plugin) iterator.next();
            if ( ( def.getGroupId() == null || StringUtils.equals( "org.apache.maven.plugins", def.getGroupId() ) )
                && StringUtils.equals( "maven-plugin-plugin", def.getArtifactId() ) )
            {
                Xpp3Dom configuration = (Xpp3Dom) def.getConfiguration();
                if ( configuration != null )
                {
                    final Xpp3Dom goalPrefix = configuration.getChild( "goalPrefix" );
                    if ( goalPrefix != null )
                    {
                        plugin.setPrefix( goalPrefix.getValue() );
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    private void updateVersionMetadata( String groupId, String artifactId, String version )
    {
        synchronized ( lock )
        {
            Metadata metadata = (Metadata) this.metadata.get( MockRepoUtils.getGAVPath( groupId, artifactId, null ) );
            if ( metadata == null )
            {
                metadata = new Metadata();
                metadata.setGroupId( groupId );
                metadata.setArtifactId( artifactId );
                this.metadata.put( MockRepoUtils.getGAVPath( groupId, artifactId, null ), metadata );
            }
            if ( metadata.getArtifactId() == null )
            {
                // might be a plugin at localhost.foo and an artifact foo at localhost
                metadata.setArtifactId( artifactId );
            }
            if ( metadata.getVersioning() == null )
            {
                metadata.setVersioning( new Versioning() );
                metadata.getVersioning().setLastUpdatedTimestamp( new Date() );
            }
            metadata.getVersioning().addVersion( version );
            if ( metadata.getVersion() == null ||
                new DefaultArtifactVersion( metadata.getVersion() ).compareTo( new DefaultArtifactVersion( version ) )
                    < 0 )
            {
                metadata.setVersion( version );
            }
        }
    }


}
