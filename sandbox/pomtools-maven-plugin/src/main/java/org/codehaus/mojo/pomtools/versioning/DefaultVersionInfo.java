package org.codehaus.mojo.pomtools.versioning;

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

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.codehaus.plexus.util.StringUtils;

/** This compares and increments versions for a common java versioning scheme.
 * <p>
 * The supported version scheme has the following parts.<br>
 * <code><i>component-digits-annotation-annotationRevision-buildSpecifier</i></code><br>
 * Example:<br>
 * <code>my-component-1.0.1-alpha-2-SNAPSHOT</code>
 * 
 * <ul>Terms:
 *  <li><i>component</i> - name of the versioned component (log4j, commons-lang, etc)
 *  <li><i>digits</i> - Numeric digits with at least one "." period. (1.0, 1.1, 1.01, 1.2.3, etc)
 *  <li><i>annotation</i> - Version annotation - Valid Values are (alpha, beta, RC).
 *   Use {@link DefaultVersionInfo#setAnnotationOrder(List)} to change the valid values.
 *  <li><i>annotationRevision</i> - Integer qualifier for the annotation. (4 as in RC-4)
 *  <li><i>buildSpecifier</i> - Additional specifier for build. (SNAPSHOT, or build number like "20041114.081234-2")
 * </ul>
 * <b>Digits is the only required piece of the version string, and must contain at lease one "." period.</b>
 * <p>
 * Implementation details:<br>
 * The separators "_" and "-" between components are also optional (though they are usually reccommended).<br>
 * Example:<br>
 * <code>log4j-1.2.9-beta-9-SNAPSHOT == log4j1.2.9beta9SNAPSHOT == log4j_1.2.9_beta_9_SNAPSHOT</code>
 * <p>
 * All numbers in the "digits" part of the version are considered Integers. Therefore 1.01.01 is the same as 1.1.1
 * Leading zeros are ignored when performing comparisons.
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class DefaultVersionInfo
    implements VersionInfo, Cloneable
{
    private String strVersion;

    private String component;

    private List digits;

    private String annotation;

    private String annotationRevision;

    private String buildSpecifier;

    private String digitSeparator;

    private String annotationSeparator;

    private String annotationRevSeparator;

    private String buildSeparator;
    
    private List annotationOrder;
    
    private boolean parsed = false;
    
    private static final int COMPONENT_INDEX = 1;

    private static final int DIGIT_SEPARATOR_INDEX = 2;
    
    private static final int DIGITS_INDEX = 3;

    private static final int ANNOTATION_SEPARATOR_INDEX = 4;

    private static final int ANNOTATION_INDEX = 5;

    private static final int ANNOTATION_REV_SEPARATOR_INDEX = 6;

    private static final int ANNOTATION_REVISION_INDEX = 7;

    private static final int BUILD_SEPARATOR_INDEX = 8;

    private static final int BUILD_SPECIFIER_INDEX = 9;

    public static final String SNAPSHOT_IDENTIFIER = "SNAPSHOT";
    
    protected static final String DIGIT_SEPARATOR_STRING = ".";
    
    /** Default order of annotations to consider in {@link #compareTo(Object)}.
     * <code>null</code> denotes a version without an annotation. Therefore, a "SP"
     * or Service Pack build is considered to be greater than a version without 
     * an annotation.
     */
    public static final String[] DEFAULT_ANNOTATION_ORDER = new String[] 
        { "DEV", "ALPHA", "B", "BETA", "RC", null, "SP" };

    protected static final Pattern DATESTAMP_PATTERN = Pattern.compile( "^(SNAPSHOT.)?((\\d{8})(?:\\.(\\d+))?)$" );
        
    protected static final Pattern STANDARD_PATTERN = Pattern.compile( 
        "^(?:([a-zA-Z].*?)([-_])(?=\\d))?" +  // non greedy .* to grab the component. dash must precede a number 
        "((?:\\d+[.])*\\d+)" +                // digit(s) and '.' repeated - followed by digits
        "([-_])?" +                 // optional - or _  (annotation separator)
        "([a-zA-Z]*)" +             // alpha characters (looking for annotation - alpha, beta, RC, etc.)
        "([-_])?" +                 // optional - or _  (annotation revision separator)
        "(\\d*)" +                  // digits  (any digits after rc or beta is an annotation revision)
        "(?:([-_])?(.*?))?$" );     // - or _ followed everything else (build specifier)
    
    protected static final Pattern OPTIONAL_DIGIT_SEPARATOR_PATTERN = Pattern.compile( 
        "^(.*?)" +                  // non greedy .* to grab the component. 
        "([-_])?" +                 // optional - or _  (digits separator)
        "((?:\\d+[.])+\\d+)" +      // digit(s) and '.' repeated - followed by digit (version digits 1.22.0, etc)
        "([-_])?" +                 // optional - or _  (annotation separator)
        "([a-zA-Z]*)" +             // alpha characters (looking for annotation - alpha, beta, RC, etc.)
        "([-_])?" +                 // optional - or _  (annotation revision separator)
        "(\\d*)" +                  // digits  (any digits after rc or beta is an annotation revision)
        "(?:([-_])?(.*?))?$" );     // - or _ followed everything else (build specifier)

    protected static final Pattern DIGIT_SEPARATOR_PATTERN = Pattern.compile( "(\\d+)\\.?" );
    
    /** Constructs this object and parses the supplied version string.
     *  
     * @param version
     */
    public DefaultVersionInfo( String version )        
    {
        annotationOrder = Arrays.asList( DEFAULT_ANNOTATION_ORDER );
        
        if ( version == null )
        {
            throw new VersionParseRTException( "Version cannot be null" );            
        }
        
        parseVersion( version );
    }
    
    /** Internal routine for parsing the supplied version string into its parts. 
     * 
     * @param version
     */
    protected void parseVersion( String version )        
    {
        this.strVersion = version;

        if ( StringUtils.isEmpty( strVersion ) )
        {
            // Don't try to parse null strings
            return;
        }
        
        Matcher m = DATESTAMP_PATTERN.matcher( strVersion );
        
        if ( m.matches() )
        {
            // Just grab the digits part of the string.
            this.buildSpecifier = m.group( 2 );
            this.parsed = true;
        }
        else
        {
            m = OPTIONAL_DIGIT_SEPARATOR_PATTERN.matcher( strVersion );
            if ( !m.matches() )
            {
                m = STANDARD_PATTERN.matcher( strVersion );
            }
            
            if ( m.matches() )
            {
                setComponent( m.group( COMPONENT_INDEX ) );
                this.digitSeparator = m.group( DIGIT_SEPARATOR_INDEX );
                setDigits( parseDigits( m.group( DIGITS_INDEX ) ) );
                if ( !SNAPSHOT_IDENTIFIER.equals( m.group( ANNOTATION_INDEX ) ) )
                {
                    this.annotationSeparator = m.group( ANNOTATION_SEPARATOR_INDEX );
                    setAnnotation( m.group( ANNOTATION_INDEX ) );
    
                    if ( StringUtils.isNotEmpty( m.group( ANNOTATION_REV_SEPARATOR_INDEX ) )
                        && StringUtils.isEmpty( m.group( ANNOTATION_REVISION_INDEX ) ) )
                    {
                        // The build separator was picked up as the annotation revision separator
                        this.buildSeparator = m.group( ANNOTATION_REV_SEPARATOR_INDEX );
                        setBuildSpecifier( m.group( BUILD_SPECIFIER_INDEX ) );
                    }
                    else
                    {
                        this.annotationRevSeparator = m.group( ANNOTATION_REV_SEPARATOR_INDEX );
                        setAnnotationRevision( m.group( ANNOTATION_REVISION_INDEX ) );
    
                        this.buildSeparator = m.group( BUILD_SEPARATOR_INDEX );
                        setBuildSpecifier( m.group( BUILD_SPECIFIER_INDEX ) );
                    }
                }
                else
                {
                    // Annotation was "SNAPSHOT" so populate the build specifier with that data
                    this.buildSeparator = m.group( ANNOTATION_SEPARATOR_INDEX );
                    setBuildSpecifier( m.group( ANNOTATION_INDEX ) );
                }
                
                this.parsed = true;
            }            
        }        
    }
    
    public boolean equals( Object obj ) 
    {
        if ( !( obj instanceof DefaultVersionInfo ) )
        {
            throw new ClassCastException( "DefaultVersionInfo object expected" );
        }

        DefaultVersionInfo that = (DefaultVersionInfo) obj;

        return StringUtils.equals( this.strVersion, that.strVersion );
    }
    
    public int hashCode()
    {
        return strVersion.hashCode();
    }

    public boolean isParsed() 
    {
        return this.parsed;
    }
    
    public boolean isSnapshot()
    {
        return SNAPSHOT_IDENTIFIER.equalsIgnoreCase( this.buildSpecifier );
    }

    public VersionInfo getNextVersion()
    {
        if ( !isParsed() ) 
        {
            return null;
        }
        
        DefaultVersionInfo result;

        try
        {
            result = (DefaultVersionInfo) this.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            return null;
        }

        if ( StringUtils.isNumeric( result.annotationRevision ) )
        {
            result.annotationRevision = incrementVersionString( result.annotationRevision );
        }
        else if ( result.digits != null && !result.digits.isEmpty() )
        {
            try
            {
                List tmpDigits = result.digits;
                tmpDigits.set( tmpDigits.size() - 1, 
                               incrementVersionString( (String) tmpDigits.get( tmpDigits.size() - 1 ) ) );
            }
            catch ( NumberFormatException e )
            {
                return null;
            }
        }
        else
        {
            return null;
        }

        return result;
    }
    
    /** Compares this {@link DefaultVersionInfo} to the supplied {@link DefaultVersionInfo}
     * to determine which version is greater.
     * <p>
     * Decision order is: digits, annotation, annotationRev, buildSpecifier.
     * <p>
     * Presence of an annotation is considered to be less than an equivalent version without an annotation.<br>
     * Example: 1.0 is greater than 1.0-alpha.<br> 
     * <p> 
     * The {@link DefaultVersionInfo#getAnnotationOrder()} is used in determining the rank order of annotations.<br>
     * For example: alpha &lt; beta &lt; RC &lt release 
     * 
     * @param that
     * @return
     * @throws IllegalArgumentException if the components differ between the objects or if 
     *         either of the annotations can not be determined.
     */
    public int compareTo( Object obj )
    {
        if ( !( obj instanceof DefaultVersionInfo ) )
        {
            throw new ClassCastException( "DefaultVersionInfo object expected" );
        }

        DefaultVersionInfo that = (DefaultVersionInfo) obj;

        if ( !isParsed() ) 
        {
            throw new IllegalArgumentException( "Cannot perform comparison on a component that wasn't "
                    + "able to be parsed" );
        }
        else if ( !StringUtils.equals( this.component, that.component ) )
        {
            throw new IllegalArgumentException( "Cannot perform comparison on different components: \""
                + this.component + "\" compared to \"" + that.component + "\"" );
        }

        if ( this.digits == null && that.digits != null ) 
        {
            return -1;
        } 
        else if ( this.digits != null && that.digits == null )
        {
            return 1;
        }
        else if ( this.digits == null && that.digits == null )
        {
            // nothing, keep looking at rest of verison
            this.digits = null; //dummy statment to silence checkstyle
        }
        else if ( !this.digits.equals( that.digits ) )
        {
            for ( int i = 0; i < this.digits.size(); i++ )
            {
                if ( i >= that.digits.size() )
                {
                    // We've gone past the end of the digit list of that. We are greater
                    return 1;
                }

                if ( !StringUtils.equals( (String) this.digits.get( i ), (String) that.digits.get( i ) ) )
                {
                    return compareToAsIntegers( (String) this.digits.get( i ), (String) that.digits.get( i ) );
                }
            }

            if ( this.digits.size() < that.digits.size() )
            {
                // The lists were equal up to the end of this list. The other has more digits so it is greater.
                return -1;
            }
        }

        if ( !StringUtils.equalsIgnoreCase( this.annotation, that.annotation ) )
        {
            int nThis = annotationOrder.indexOf( StringUtils.lowerCase( this.annotation ) );
            int nThat = annotationOrder.indexOf( StringUtils.upperCase( that.annotation ) );

            if ( nThis == -1 || nThat == -1 )
            {
                // Here we have a situation where one of the annotations is unknown
                // If both are non-null, just compare them lexically.
                // else consider the version with the null annotation as being greater
                // a 1.0-unknown is less than 1.0
                if ( this.annotation != null && that.annotation == null )
                {
                    return -1;
                }
                else if ( this.annotation == null && that.annotation != null )
                {
                    return 1;
                }
                else
                {
                    return this.annotation.toUpperCase().compareTo( that.annotation.toUpperCase() );
                }
            }

            return nThis - nThat;
        }

        if ( !StringUtils.equals( this.annotationRevision, that.annotationRevision ) )
        {
            return compareToAsIntegers( this.annotationRevision, that.annotationRevision );
        }

        if ( !StringUtils.equals( this.buildSpecifier, that.buildSpecifier ) )
        {
            if ( this.buildSpecifier == null && that.buildSpecifier != null )
            {
                return 1;
            }
            else if ( this.buildSpecifier != null && that.buildSpecifier == null )
            {
                return -1;
            }
            else
            {
                // Just do a simple string comparison?
                return this.buildSpecifier.compareTo( that.buildSpecifier );
            }
        }

        return 0;
    }

    private int compareToAsIntegers( String s1, String s2 )
    {
        int n1 = StringUtils.isEmpty( s1 ) ? -1 : Integer.parseInt( s1 );
        int n2 = StringUtils.isEmpty( s2 ) ? -1 : Integer.parseInt( s2 );

        return n1 - n2;
    }
    
    /** Takes a string and increments it as an integer.  
     * Preserves any lpad of "0" zeros.
     * 
     * @param s
     * @return
     */
    protected String incrementVersionString( String s )
    {
        if ( StringUtils.isEmpty( s ) )
        {
            return null;
        }

        try
        {
            Integer n = new Integer( Integer.parseInt( s ) + 1 );
            if ( n.toString().length() < s.length() )
            {
                // String was left-padded with zeros
                return StringUtils.leftPad( n.toString(), s.length(), "0" );
            }
            return n.toString();
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }
    
    public String getSnapshotVersionString() 
    {
        return getVersionString( this, SNAPSHOT_IDENTIFIER, StringUtils.defaultString( this.buildSeparator, "-" ) );
    }
    
    public String getReleaseVersionString()
    {
        return getVersionString( this, null, null );
    }
    
    public ArtifactVersion getArtifactVersion() 
    {
        return new DefaultArtifactVersion( getVersionString() );
    }

    public String toString() 
    {
        return getVersionString();
    }
    
    public String getVersionString()
    {
        return getVersionString( this, this.buildSpecifier, this.buildSeparator );
    }

    protected static String getVersionString( DefaultVersionInfo info, String buildSpecifier, String buildSeparator )
    {
        if ( !info.isParsed() ) 
        {
            return info.strVersion;
        }
        
        StringBuffer sb = new StringBuffer();

        if ( StringUtils.isNotEmpty( info.component ) )
        {
            sb.append( info.component );
        }

        if ( info.digits != null )
        {
            sb.append( StringUtils.defaultString( info.digitSeparator ) );
            sb.append( joinDigitString( info.digits ) );
        }

        if ( StringUtils.isNotEmpty( info.annotation ) )
        {
            sb.append( StringUtils.defaultString( info.annotationSeparator ) );
            sb.append( info.annotation );
        }

        if ( StringUtils.isNotEmpty( info.annotationRevision ) )
        {
            if ( StringUtils.isEmpty( info.annotation ) )
            {
                sb.append( StringUtils.defaultString( info.annotationSeparator ) );
            }
            else
            {
                sb.append( StringUtils.defaultString( info.annotationRevSeparator ) );
            }
            sb.append( info.annotationRevision );
        }

        if ( StringUtils.isNotEmpty( buildSpecifier ) )
        {
            sb.append( StringUtils.defaultString( buildSeparator ) );
            sb.append( buildSpecifier );
        }

        return sb.toString();
    }
    
    /** Simply joins the items in the list with "." period
     * 
     * @param digits
     * @return
     */
    protected static String joinDigitString( List digits )
    {
        if ( digits == null )
        {
            return null;
        }

        return StringUtils.join( digits.iterator(), DIGIT_SEPARATOR_STRING );
    }

    /** Splits the string on "." and returns a list 
     * containing each digit.
     * 
     * @param strDigits
     * @return
     */
    protected List parseDigits( String strDigits )
    {
        if ( StringUtils.isEmpty( strDigits ) )
        {
            return null;
        }

        String[] strings = StringUtils.split( strDigits, DIGIT_SEPARATOR_STRING );
        return Arrays.asList( strings );
    }

    //--------------------------------------------------
    // Getters & Setters
    //--------------------------------------------------

    private String nullIfEmpty( String s )
    {
        return ( StringUtils.isEmpty( s ) ) ? null : s;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    protected void setAnnotation( String annotation )
    {
        this.annotation = nullIfEmpty( annotation );
    }

    public String getAnnotationRevision()
    {
        return annotationRevision;
    }

    protected void setAnnotationRevision( String annotationRevision )
    {
        this.annotationRevision = nullIfEmpty( annotationRevision );
    }

    public String getComponent()
    {
        return component;
    }

    protected void setComponent( String component )
    {
        this.component = nullIfEmpty( component );
    }

    public List getDigits()
    {
        return digits;
    }

    protected void setDigits( List digits )
    {
        this.digits = digits;
    }

    public String getBuildSpecifier()
    {
        return buildSpecifier;
    }

    protected void setBuildSpecifier( String buildSpecifier )
    {
        this.buildSpecifier = nullIfEmpty( buildSpecifier );
    }

    public List getAnnotationOrder()
    {
        return annotationOrder;
    }

    protected void setAnnotationOrder( List annotationOrder )
    {
        this.annotationOrder = annotationOrder;
    }

}
