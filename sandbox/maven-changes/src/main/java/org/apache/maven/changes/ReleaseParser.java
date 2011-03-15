package org.apache.maven.changes;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

/**
 * Parses a <tt>changes.xml</tt> file and returns the list of {@link Release} elements.
 *
 * @author <a href="snicoll@apache.org">Stephane Nicoll</a>
 * @version $Id$
 */
public final class ReleaseParser
{

    public static Release[] parse( final File changesFile )
        throws IOException, InvalidChangesException
    {
        final Reader r = new FileReader( changesFile );
        return parse( r );
    }

    public static Release[] parse( final Reader changesReader )
        throws InvalidChangesException, IOException
    {
        try
        {
            final Xpp3Dom doc = parseDocument( changesReader );
            final Xpp3Dom releasesRoot = doc.getChild( "body" );
            final List releases = new ArrayList();

            final Xpp3Dom[] releaseElements = releasesRoot.getChildren( "release" );
            for ( int i = 0; i < releaseElements.length; i++ )
            {
                Xpp3Dom releaseElement = releaseElements[i];
                releases.add( parseRelease( releaseElement ) );

            }
            return (Release[]) releases.toArray( new Release[releases.size()] );
        }
        catch ( XmlPullParserException e )
        {
            throw new InvalidChangesException( "Invalid changes.xml file", e );
        }
    }


    public static Release parse( final File changesFile, String releaseVersion )
        throws ReleaseNotFoundException, InvalidChangesException, IOException
    {
        final Reader reader = new FileReader( changesFile );
        return parse( reader, releaseVersion );
    }

    public static Release parse( final Reader changesReader, String releaseVersion )
        throws ReleaseNotFoundException, InvalidChangesException, IOException
    {
        try
        {
            final Xpp3Dom doc = parseDocument( changesReader );
            final Xpp3Dom releasesRoot = doc.getChild( "body" );
            // TODO Baaah easier to use xpath here "/document/body/release[@version='"+releaseVersion+"']"
            final Xpp3Dom[] releaseElements = releasesRoot.getChildren( "release" );
            for ( int i = 0; i < releaseElements.length; i++ )
            {
                Xpp3Dom releaseElement = releaseElements[i];
                if ( releaseVersion.equals( releaseElement.getAttribute( "version" ) ) )
                {
                    return parseRelease( releaseElement );
                }
            }
            throw new ReleaseNotFoundException( "Release version[" + releaseVersion + "] does not exist." );
        }
        catch ( XmlPullParserException e )
        {
            throw new InvalidChangesException( "Invalid changes.xml file", e );
        }
    }

    private static Xpp3Dom parseDocument( final Reader reader )
        throws IOException, XmlPullParserException
    {
        try
        {
            return Xpp3DomBuilder.build( reader );
        }
        finally
        {
            if ( reader != null )
            {
                reader.close();
            }
        }

    }

    private static Release parseRelease( final Xpp3Dom releaseRoot )
        throws InvalidChangesException
    {
        final String version = releaseRoot.getAttribute( "version" );
        if ( version == null || version.equals( "" ) )
        {
            throw new InvalidChangesException( "Invalid release, version attribute should be set." );
        }

        final String releaseDate = releaseRoot.getAttribute( "date" );
        final String description = releaseRoot.getAttribute( "description" );

        // Actions
        final List actions = new ArrayList();
        final Xpp3Dom actionElements[] = releaseRoot.getChildren( "action" );
        for ( int i = 0; i < actionElements.length; i++ )
        {
            Xpp3Dom actionElement = actionElements[i];
            actions.add( parseIssueAction( actionElement ) );

        }
        Action[] parsedActions = (Action[]) actions.toArray( new Action[actions.size()] );

        return new Release( version, releaseDate, description, parsedActions );
    }

    private static Action parseIssueAction( final Xpp3Dom actionRoot )
        throws InvalidChangesException
    {
        final String author = actionRoot.getAttribute( "dev" );
        if ( author == null || author.equals( "" ) )
        {
            throw new InvalidChangesException( "Invalid action, dev attribute should be set." );
        }
        final String issueId = actionRoot.getAttribute( "issue" );
        final String typeString = actionRoot.getAttribute( "type" );
        final ActionType type = ActionType.getActionType( typeString );
        if ( type == null )
        {
            throw new InvalidChangesException( "Invalid action, type[" + typeString + "] is not supported, " +
                "valid values are[" + ActionType.getActionTypes() + "]" );
        }
        final String dueTo = actionRoot.getAttribute( "due-to" );
        final String dueToEmail = actionRoot.getAttribute( "due-to-email" );

        final String description = actionRoot.getValue();

        return new Action( author, issueId, type, description, dueTo, dueToEmail );
    }
}
