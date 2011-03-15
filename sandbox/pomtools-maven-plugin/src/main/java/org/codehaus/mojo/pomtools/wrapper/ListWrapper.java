package org.codehaus.mojo.pomtools.wrapper;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.codehaus.mojo.pomtools.helpers.LocalStringUtils;
import org.codehaus.mojo.pomtools.wrapper.modify.ModifiableList;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ListWrapper
    extends ObjectWrapper
    implements List
{
    private final Class itemClass;
    
    private final Class itemWrapperClass;
    
    private final String name;
    
    private final String itemTypeLabel;
    
    private final ModifiableList items;

    public ListWrapper( ObjectWrapper parent, List items, Class itemClass,
                        String name, String itemTypeLabel )
    {
        this( parent, items, itemClass, ObjectWrapper.class, name, itemTypeLabel );
    }
    
    public ListWrapper( ObjectWrapper parent, List items, Class itemClass, Class itemWrapperClass, 
                            String name, String itemTypeLabel )
        {
        super( parent, items, name );
    
        this.itemClass = itemClass;
        
        this.itemWrapperClass = itemWrapperClass;
        
        this.name = name;
        
        this.itemTypeLabel = itemTypeLabel;
        
        if ( items == null )
        {
            this.items = new ModifiableList( this, new ArrayList() );
        }
        else
        {
            List tmpList = new ArrayList( items.size() );
            
            for ( Iterator i = items.iterator(); i.hasNext(); )
            {
                tmpList.add( internalCreateItem( i.next() ) );
            }       
            
            this.items = new ModifiableList( this, tmpList );
        }
    }
    
    public String toString()
    {
        if ( items.isEmpty() )
        {
            return null;
        }
        
        return items.size() + " " + LocalStringUtils.ifTrue( ( items.size() == 1 ), 
                                                             itemTypeLabel, name );  
    }
    
    public String getItemTypeLabel()
    {
        return itemTypeLabel;
    }
    
    private ObjectWrapper internalCreateItem( Object objectToWrap )
    {
        ObjectWrapper item = null;
        
        if ( ObjectWrapper.class.equals( itemWrapperClass ) )
        {
            item = new ObjectWrapper( this, objectToWrap, itemTypeLabel, itemClass );
        } 
        else
        {
            try
            {
                Constructor con = itemWrapperClass.getConstructor( new Class[] { 
                    ObjectWrapper.class, Object.class, String.class, Class.class } );
                
                item = (ObjectWrapper) con.newInstance( new Object[] { this, objectToWrap, itemTypeLabel, itemClass } );
            }
            catch ( NoSuchMethodException e )
            {
                throw new ModelReflectionException( e );
            }
            catch ( IllegalAccessException e )
            {
                throw new ModelReflectionException( e );
            }
            catch ( InvocationTargetException e )
            {
                throw new ModelReflectionException( e );
            }
            catch ( InstantiationException e )
            {
                throw new ModelReflectionException( e );
            }
        }
        
        return item;   
    }
    
    public ObjectWrapper createItem( Object objectToWrap )
    {
        setModified();
        ObjectWrapper obj = internalCreateItem( objectToWrap );
        
        items.add( obj );
        
        return obj;
    }
    
    protected void add( ObjectWrapper o )
    {
        items.add( o );        
    }
    
    public Object getWrappedObject() 
    {
        if ( isEmpty() )
        {
            return null;
        }
        
        List result = new ArrayList();
        
        for ( Iterator i = items.iterator(); i.hasNext(); )
        {
            result.add( ( (ObjectWrapper) i.next() ).getWrappedObject() );
        }
        
        return result;
    }
    
    public boolean isEmpty()
    {
        if ( !items.isEmpty() )
        {
            for ( Iterator i = items.iterator(); i.hasNext(); )
            {
                ObjectWrapper obj = (ObjectWrapper) i.next();
                if ( !obj.isEmpty() )
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public int size()
    {
        return items.size();
    }

    public boolean remove( Object obj )
    {
        setModified();
        
        return items.remove( obj );
    }
    
    public List getItems()
    {
        return new ModifiableList( this, items );
    }

    public Class getItemClass()
    {
        return itemClass;
    }

    public ObjectWrapper getObject( int index )
    {
        return (ObjectWrapper) items.get( index );
    }
    
    public Object get( int index )
    {
        return (ObjectWrapper) items.get( index );
    }

    public void add( int arg0, Object arg1 )
    {
        items.add( arg0, arg1 );
    }

    public boolean add( Object arg0 )
    {
        return items.add( arg0 );
    }

    public boolean addAll( Collection arg0 )
    {
        return items.addAll( arg0 );
    }

    public boolean addAll( int arg0, Collection arg1 )
    {
        return items.addAll( arg0, arg1 );
    }

    public void clear()
    {
        items.clear();
    }

    public boolean contains( Object o )
    {
        return items.contains( o );
    }

    public boolean containsAll( Collection arg0 )
    {
        return items.containsAll( arg0 );
    }

    public boolean equals( Object o )
    {
        return items.equals( o );
    }

    public int hashCode()
    {
        return items.hashCode();
    }

    public int indexOf( Object o )
    {
        return items.indexOf( o );
    }

    public Iterator iterator()
    {
        return items.iterator();
    }

    public int lastIndexOf( Object o )
    {
        return items.lastIndexOf( o );
    }

    public ListIterator listIterator()
    {
        return items.listIterator();
    }

    public ListIterator listIterator( int index )
    {
        return items.listIterator( index );
    }

    public Object remove( int index )
    {
        return items.remove( index );
    }

    public boolean removeAll( Collection arg0 )
    {
        return items.removeAll( arg0 );
    }

    public boolean retainAll( Collection arg0 )
    {
        return items.retainAll( arg0 );
    }

    public Object set( int arg0, Object arg1 )
    {
        return items.set( arg0, arg1 );
    }

    public List subList( int fromIndex, int toIndex )
    {
        return items.subList( fromIndex, toIndex );
    }

    public Object[] toArray()
    {
        return items.toArray();
    }

    public Object[] toArray( Object[] arg0 )
    {
        return items.toArray( arg0 );
    }
}
