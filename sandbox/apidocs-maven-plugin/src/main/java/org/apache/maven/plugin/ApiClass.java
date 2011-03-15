package org.apache.maven.plugin;

/*
 * LICENSE
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * A wrapper class around a @see com.thoughtworks.qdox.model.JavaClass to make it
 * easier to use from velcity.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ApiClass
{
    /**
     * The wrapped <code>JavaClass</code>.
     */
    private JavaClass clazz;

    public ApiClass( JavaClass clazz )
    {
        this.clazz = clazz;
    }

    /**
     * Returns only the class name of this class.
     *
     * @return Returns only the class name of this class.
     */
    public String getName()
    {
        return clazz.getName();
    }

    /**
     * Returns the full name of this class including the package.
     *
     * @return Returns the full name of this class including the package.
     */
    public String getFullName()
    {
        return clazz.getFullyQualifiedName();
    }

    public String getFileName()
    {
        return clazz.getFullyQualifiedName().replace( '.', File.separatorChar );
    }

    /**
     * Returns the package of this class.
     *
     * @return Returns the package of this class.
     */
    public String getPackage()
    {
        return emptyIfNull( clazz.getPackage() );
    }

    /**
     * Returns a list of all superclasses starting with <code>java.lang.Object</code>.
     * <p/>
     * The list starts with <code>java.lang.Object</code>.
     *
     * @return A list of all parents.
     */
    public List getParents()
    {
        List parents = new ArrayList();

        if ( getFullName().equals( "java.lang.Object" ) )
        {
            return new ArrayList( 0 );
        }

        JavaClass parent = clazz.getSuperJavaClass();

        while ( parent != null )
        {
            parents.add( new ApiClass( parent ) );

            parent = parent.getSuperJavaClass();
        }

        return parents;
    }

    /**
     * Returns the reversed list from @see #getParentsReversed().
     *
     * @return Returns the reversed list from @see #getParentsReversed().
     */
    public List getParentsReversed()
    {
        List parents = getParents();

        Collections.reverse( parents );

        return parents;
    }

    /**
     * Returns the class name of the super class.
     *
     * @return Returns the class name of the super class.
     */
    public String getSuperClassName()
    {
        if ( clazz.getSuperJavaClass() != null )
        {
            return clazz.getSuperJavaClass().getName();
        }

        return "";
    }

    /**
     * Returns a <code>List</code> of all implemented interfaces.
     *
     * @return Returns a <code>List</code> of all implemented interfaces.
     */
    public List getInterfaces()
    {
        JavaClass[] implemented = clazz.getImplementedInterfaces();

        ApiClass[] interfaces = new ApiClass[ implemented.length ];

        for ( int i = 0; i < interfaces.length; i++ )
        {
            interfaces[ i ] = new ApiClass( implemented[ i ] );
        }

        return Arrays.asList( interfaces );
    }

    /**
     * Returns a formatted string containing all the interfaces.
     * <p/>
     * The string will be on the form:
     * <pre>
     *  Interface1, Interface2
     * </pre>
     *
     * @return Returns a formatted string containing all the interfaces.
     */
    public String getInterfacesAsString()
    {
        JavaClass[] interfaces = clazz.getImplementedInterfaces();

        String[] list = new String[ interfaces.length ];

        for ( int i = 0; i < list.length; i++ )
        {
            list[ i ] = interfaces[ i ].getName();
        }

        return formatList( list );
    }

    public boolean isPublic()
    {
        return clazz.isPublic();
    }

    public boolean isInterface()
    {
        return clazz.isInterface();
    }

    /**
     * Returns a formatted string containing all the authors.
     * <p/>
     * The string will be on the form:
     * <pre>
     *  Author 1, Author 2
     * </pre>
     *
     * @return Returns a formatted string containing all the authors.
     */
    public String getAuthorsAsString()
    {
        DocletTag[] authors = clazz.getTagsByName( "author" );

        String[] list = new String[ authors.length ];

        for ( int i = 0; i < list.length; i++ )
        {
            list[ i ] = authors[ i ].getValue();
        }

        return formatList( list );
    }

    /**
     * Returns a formatted string containing all the "see also" elements.
     * <p/>
     * The string will be on the form:
     * <pre>
     *  Interface1, Interface2
     * </pre>
     *
     * @return Returns a formatted string containing all the "see also" elements.
     */
    public String getSeeAlsoAsString()
    {
        DocletTag[] elements = clazz.getTagsByName( "see" );

        String[] list = new String[ elements.length ];

        for ( int i = 0; i < list.length; i++ )
        {
            list[ i ] = elements[ i ].getValue();
        }

        return formatList( list );
    }

    public String getAccessLevel()
    {
        if ( clazz.isPublic() )
        {
            return "public";
        }
        else if ( clazz.isProtected() )
        {
            return "protected";
        }
        else
        {
            return "private";
        }
    }

    public List getFields()
    {
        return Arrays.asList( clazz.getFields() );
    }

    public List getConstructors()
    {
        JavaMethod[] methods = clazz.getMethods();

        List constructors = new ArrayList();

        for ( int i = 0; i < methods.length; i++ )
        {
            if ( methods[ i ].isConstructor() )
            {
                constructors.add( new ApiMethod( methods[ i ] ) );
            }
        }

        // if the source file doesn't have a constructor add the default constructor
        if ( constructors.size() == 0 )
        {
            JavaMethod constructor = new JavaMethod( clazz );

            constructor.setName( clazz.getName() );

            constructor.setConstructor( true );

            constructors.add( new ApiMethod( constructor ) );
        }

        return constructors;
    }

    public List getMethods()
    {
        JavaMethod[] methods = clazz.getMethods( false );

        List list = new ArrayList();

        for ( int i = 0; i < methods.length; i++ )
        {
            if ( !methods[ i ].isConstructor() )
            {
                list.add( new ApiMethod( methods[ i ] ) );
            }
        }

        return list;
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * Returns <code>""</code> is <code>string</code> is null,
     * unless <code>string is returned</code>.
     *
     * @param string The string to check.
     * @return Returns <code>""</code> is <code>string</code> is null,
     *         unless <code>string is returned</code>.
     */
    private String emptyIfNull( String string )
    {
        if ( string == null )
        {
            return "";
        }

        return string;
    }

    private String formatList( Object[] list )
    {
        if ( list.length == 0 )
        {
            return "";
        }

        StringBuffer string = new StringBuffer( list[ 0 ].toString() );

        for ( int i = 1; i < list.length; i++ )
        {
            string.append( ", " ).append( list[ i ].toString() );
        }

        return string.toString();
    }
}
