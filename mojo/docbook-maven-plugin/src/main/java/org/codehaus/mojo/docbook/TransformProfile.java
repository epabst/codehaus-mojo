/*
 * maven-docbook-plugin - Copyright (C) 2006 Mindquarry GmbH - http://www.mindquarry.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *   
 * $Id$
 */
package org.codehaus.mojo.docbook;

import javax.xml.transform.Transformer;

/**
 * A holder of profiling information, like operating system, architecture or vendor.
 * 
 * @author <a href="mailto:lars.trieloff@mindquarry.com">Lars Trieloff</a>
 */
public class TransformProfile
{
    /**
     * A unique id for the profile
     */
    private String id;

    /**
     * The attribute to profile for (attribute-based profiling)
     */
    private String attribute;

    /**
     * The attribute's value to profile for (for attribute-based profiling)
     */
    private String value;

    /**
     * For profiling for a single hardware architecture
     */
    private String arch;

    /**
     * For profiling for multiple hardware architectures
     */
    private String[] archictectures;

    /**
     * For profiling for a single condition
     */
    private String condition;

    /**
     * For profiling for multiple conditions
     */
    private String[] conditions;

    /**
     * For profiling for a single conformance
     */
    private String conformance;

    /**
     * For profiling for multiple conformances
     */
    private String[] conformances;

    /**
     * For profiling for a single language
     */
    private String lang;

    /**
     * For profiling for multiple languages
     */
    private String[] languages;

    /**
     * For profiling for a single operating system
     */
    private String os;

    /**
     * For profiling for multiple operating systems
     */
    private String[] operatingsystems;

    /**
     * For profiling for a single revision
     */
    private String revision;

    /**
     * For profiling for multiple revisions
     */
    private String[] revisions;

    /**
     * For profiling for a single role
     */
    private String role;

    /**
     * For profiling for multiple roles
     */
    private String[] roles;

    /**
     * For profiling for a single security level
     */
    private String security;

    /**
     * For profiling for multiple profiling levels
     */
    private String[] securities;

    /**
     * For profiling for a single user level
     */
    private String userlevel;

    /**
     * For profiling for multiple user levels
     */
    private String[] userlevels;

    /**
     * For profiling for a single vendor
     */
    private String vendor;

    /**
     * For profiling for multiple vendors
     */
    private String vendors[];

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getAttribute()
    {
        return attribute;
    }

    public void setAttribute( String attribute )
    {
        this.attribute = attribute;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getArch()
    {
        return arch;
    }

    public void setArch( String arch )
    {
        this.arch = arch;
    }

    public String[] getArchictectures()
    {
        return archictectures;
    }

    public void setArchictectures( String[] archictectures )
    {
        this.archictectures = archictectures;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition( String condition )
    {
        this.condition = condition;
    }

    public String[] getConditions()
    {
        return conditions;
    }

    public void setConditions( String[] conditions )
    {
        this.conditions = conditions;
    }

    public String getConformance()
    {
        return conformance;
    }

    public void setConformance( String conformance )
    {
        this.conformance = conformance;
    }

    public String[] getConformances()
    {
        return conformances;
    }

    public void setConformances( String[] conformances )
    {
        this.conformances = conformances;
    }

    public String getLang()
    {
        return lang;
    }

    public void setLang( String lang )
    {
        this.lang = lang;
    }

    public String[] getLanguages()
    {
        return languages;
    }

    public void setLanguages( String[] languages )
    {
        this.languages = languages;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs( String os )
    {
        this.os = os;
    }

    public String[] getOperatingsystems()
    {
        return operatingsystems;
    }

    public void setOperatingsystems( String[] operatingsystems )
    {
        this.operatingsystems = operatingsystems;
    }

    public String getRevision()
    {
        return revision;
    }

    public void setRevision( String revision )
    {
        this.revision = revision;
    }

    public String[] getRevisions()
    {
        return revisions;
    }

    public void setRevisions( String[] revisions )
    {
        this.revisions = revisions;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    public String[] getRoles()
    {
        return roles;
    }

    public void setRoles( String[] roles )
    {
        this.roles = roles;
    }

    public String getSecurity()
    {
        return security;
    }

    public void setSecurity( String security )
    {
        this.security = security;
    }

    public String[] getSecurities()
    {
        return securities;
    }

    public void setSecurities( String[] securities )
    {
        this.securities = securities;
    }

    public String getUserlevel()
    {
        return userlevel;
    }

    public void setUserlevel( String userlevel )
    {
        this.userlevel = userlevel;
    }

    public String[] getUserlevels()
    {
        return userlevels;
    }

    public void setUserlevels( String[] userlevels )
    {
        this.userlevels = userlevels;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }

    public String[] getVendors()
    {
        return vendors;
    }

    public void setVendors( String[] vendors )
    {
        this.vendors = vendors;
    }

    /**
     * Concatenates all profile values.
     * 
     * @return concatenated string conatining all profile values;
     */
    private String concatenateValues()
    {
        StringBuffer buf = new StringBuffer();
        append( buf, this.arch );
        append( buf, this.archictectures );
        append( buf, this.attribute );
        append( buf, this.condition );
        append( buf, this.conditions );
        append( buf, this.conformance );
        append( buf, this.conformances );
        append( buf, this.lang );
        append( buf, this.languages );
        append( buf, this.operatingsystems );
        append( buf, this.os );
        append( buf, this.revision );
        append( buf, this.revisions );
        append( buf, this.role );
        append( buf, this.roles );
        append( buf, this.securities );
        append( buf, this.security );
        append( buf, this.userlevel );
        append( buf, this.userlevels );
        append( buf, this.vendor );
        append( buf, this.vendors );
        return buf.toString();
    }

    private void append( StringBuffer buf, String string )
    {
        if ( string == null )
        {
            return;
        }
        buf.append( string );
    }

    private void append( StringBuffer buf, String[] strings )
    {
        if ( strings == null )
        {
            return;
        }
        for ( int i = 0; i < strings.length; i++ )
        {
            buf.append( strings[i] );
        }
    }

    public String getSeparator()
    {
        int startchar = 33;
        String values = this.concatenateValues();
        while ( values.indexOf( startchar ) != -1 )
        {
            startchar++;
        }
        return new String( new byte[] { (byte) startchar } );
    }

    public void setParameters( Transformer t )
    {
        String s = this.getSeparator();
        t.setParameter( "profile.separator", s );
        setParameter( t, "profile.arch", arch, archictectures, s );
        setParameter( t, "profile.condition", condition, conditions, s );
        setParameter( t, "profile.conformance", conformance, conformances, s );
        setParameter( t, "profile.lang", lang, languages, s );
        setParameter( t, "profile.os", os, operatingsystems, s );
        setParameter( t, "profile.revision", revision, revisions, s );
        setParameter( t, "profile.role", role, roles, s );
        setParameter( t, "profile.security", security, securities, s );
        setParameter( t, "profile.userlevel", userlevel, userlevels, s );
        setParameter( t, "profile.vendor", vendor, vendors, s );

        if ( attribute != null && attribute.length() != 0 && value != null && value.length() != 0 )
        {
            t.setParameter( "profile.attribute", attribute );
            t.setParameter( "profile.value", value );
        }
    }

    public static void setParameter( Transformer transformer, String parameter, String singlevalue,
                                     String[] multivalue, String separator )
    {
        if ( ( multivalue == null || multivalue.length == 0 ) && ( singlevalue != null )
            && ( !"".equals( singlevalue ) ) )
        {
            transformer.setParameter( parameter, singlevalue );
        }
        else if ( multivalue != null && multivalue.length != 0 )
        {
            transformer.setParameter( parameter, join( multivalue, separator ) );
        }
    }

    private static String join( String[] strings, String separator )
    {
        StringBuffer joined = new StringBuffer();
        for ( int i = 0; i < strings.length; i++ )
        {
            if ( i != 0 )
            {
                joined.append( separator );
            }
            joined.append( strings[i] );
        }
        return joined.toString();
    }
}
