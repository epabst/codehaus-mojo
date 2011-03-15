package org.codehaus.mojo.pomtools.wrapper.custom;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public final class ModelVersionRange
{
    private final VersionRange range;

    public ModelVersionRange( VersionRange range )
    {
        this.range = range;
    }
    
    public static ModelVersionRange createFromVersionSpec( String spec )
        throws InvalidVersionSpecificationException
    {
        return new ModelVersionRange( VersionRange.createFromVersionSpec( spec ) );
    }

    public boolean containsVersion( String version )
    {
        return containsVersion( new DefaultArtifactVersion( version ) );
    }
    
    /** {@link VersionRange#hasRestrictions()} doesn't verify that it has restrictions when
     * it checks containsVersion and throws an NPE if the range was created on a single version.
     * 
     * @param version
     * @return
     */
    public boolean containsVersion( ArtifactVersion version )
    {
        if ( hasRestrictions() )
        {
            return range.containsVersion( version );
        }
        else
        {
            return StringUtils.equals( range.toString(), version.toString() );
        }
    }

    /** {@link VersionRange#hasRestrictions()} returns false if it has a recommendedVersion regarless
     * of whether it has restrictions or not.  We actually need to know if it has restrictions,
     * so this function re-implements the hasRestrictions to actually determine if the range has
     * restrictions.
     */
    public boolean hasRestrictions()
    {
        return hasRestrictions( this.range );
    }
    
    /** {@link VersionRange#hasRestrictions()} returns false if it has a recommendedVersion regarless
     * of whether it has restrictions or not.  We actually need to know if it has restrictions,
     * so this function re-implements the hasRestrictions to actually determine if the range has
     * restrictions.
     */
    public static boolean hasRestrictions( VersionRange range )
    {
        if ( range == null )
        {
            return false;            
        }
        
        List restrictions = range.getRestrictions();
        if ( restrictions != null && !restrictions.isEmpty() )
        {
            if ( restrictions.size() > 1 )
            {
                return true;
            }
            
            Restriction restriction = (Restriction) restrictions.get( 0 );
            return restriction.getLowerBound() != null || restriction.getUpperBound() != null;
        }
        else
        {
            return false;
        }
    }

    /** {@link VersionRange#toString()} simply returns the recommended version if it has
     * a recommended version; otherwise it builds a proper string based on the restrictions.
     * We need the string build with restrictions if there are any; so this is a copy of the guts
     * of {@link VersionRange#toString()} except we build the string with restrictions regardless 
     * of having a recommendedVersion.
     */
    public String toString()
    {
        return toString( range );
    }
    
    /** {@link VersionRange#toString()} simply returns the recommended version if it has
     * a recommended version; otherwise it builds a proper string based on the restrictions.
     * We need the string build with restrictions if there are any; so this is a copy of the guts
     * of {@link VersionRange#toString()} except we build the string with restrictions regardless 
     * of having a recommendedVersion.
     */
    public static String toString( VersionRange range )
    {
        if ( range == null )
        {
            return null;
        }
        
        if ( !hasRestrictions( range ) )
        {
            return range.toString();
        }
        else
        {
            StringBuffer buf = new StringBuffer();
            for ( Iterator i = range.getRestrictions().iterator(); i.hasNext(); )
            {
                Restriction r = (Restriction) i.next();

                buf.append( r.isLowerBoundInclusive() ? "[" : "(" );
                if ( r.getLowerBound() != null )
                {
                    buf.append( r.getLowerBound().toString() );
                }
                buf.append( "," );
                if ( r.getUpperBound() != null )
                {
                    buf.append( r.getUpperBound().toString() );
                }
                buf.append( r.isUpperBoundInclusive() ? "]" : ")" );

                if ( i.hasNext() )
                {
                    buf.append( "," );
                }
            }
            return buf.toString();
        }
    }

    public List getRestrictions()
    {
        return range.getRestrictions();
    }
}
