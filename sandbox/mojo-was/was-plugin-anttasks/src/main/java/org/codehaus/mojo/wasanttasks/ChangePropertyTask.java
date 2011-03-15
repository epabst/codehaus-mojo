package org.codehaus.mojo.wasanttasks;

/*
 * The MIT License
 *
 * Copyright (c) 2006, DNB Nor.
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
 */

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.lang.reflect.Field;
import java.util.Hashtable;

/*
 * This class mutates an otherwise immutable ant property
 * This class is based on the Ant contrib Variable task.
 * Author : Hermod Opstvedt Version : 1.0 Date : 03 april 2006
 */

public class ChangePropertyTask
    extends Task
{
    private String name = null;

    private String value = null;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public void execute()
        throws BuildException
    {
        try
        {
            if ( name == null || name.equals( "" ) )
            {
                throw new BuildException( "The 'name' attribute is required." );
            }
            if ( value == null )
            {
                value = "";
            }
            mutateProperty( name, getProject().replaceProperties( value ) );
        }
        catch ( Exception e )
        {
            log( "Failure: " + e.getMessage() );
            if ( e.getCause() != null )
            {
                log( "Caused by: " + e.getCause().getMessage() );
            }
            throw new BuildException( e );
        }
    }

    private void mutateProperty( String name, String value )
    {
        try
        {
            Hashtable properties = (Hashtable) getValue( getProject(), "properties" );
            if ( properties == null )
            {
                getProject().setUserProperty( name, value );
            }
            else
            {
                properties.put( name, value );
            }
        }
        catch ( Exception e )
        {
            getProject().setUserProperty( name, value );
        }
    }

    private Field getField( Class thisClass, String fieldName )
        throws NoSuchFieldException
    {
        if ( thisClass == null )
        {
            throw new NoSuchFieldException( "Invalid field : " + fieldName );
        }
        if ( thisClass.getDeclaredField( fieldName ) != null )
        {
            return thisClass.getDeclaredField( fieldName );
        }
        return getField( thisClass.getSuperclass(), fieldName );
    }

    private Object getValue( Object instance, String fieldName )
        throws IllegalAccessException, NoSuchFieldException
    {
        Field field = getField( instance.getClass(), fieldName );
        field.setAccessible( true );
        return field.get( instance );
    }
}
