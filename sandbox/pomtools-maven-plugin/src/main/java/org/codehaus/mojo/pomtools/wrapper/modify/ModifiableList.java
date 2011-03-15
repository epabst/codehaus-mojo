package org.codehaus.mojo.pomtools.wrapper.modify;

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** Simple wrapper for {@link java.util.List} which keeps track of the modified status
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ModifiableList extends AbstractModifiableObject
    implements List
{
    private final List list;
    
    public ModifiableList( Modifiable parent, List l )
    {
        super( parent );
        
        this.list = l;
    }

    public void add( int arg0, Object arg1 )
    {
        setModified();
        list.add( arg0, arg1 );
    }

    public boolean add( Object arg0 )
    {
        setModified();
        return list.add( arg0 );
    }

    public boolean addAll( Collection arg0 )
    {
        setModified();
        return list.addAll( arg0 );
    }

    public boolean addAll( int arg0, Collection arg1 )
    {
        setModified();
        return list.addAll( arg0, arg1 );
    }

    public void clear()
    {
        setModified();
        list.clear();
    }

    public boolean contains( Object o )
    {
        return list.contains( o );
    }

    public boolean containsAll( Collection arg0 )
    {
        return list.containsAll( arg0 );
    }

    public boolean equals( Object o )
    {
        return list.equals( o );
    }

    public Object get( int index )
    {
        return list.get( index );
    }

    public int hashCode()
    {
        return list.hashCode();
    }

    public int indexOf( Object o )
    {
        return list.indexOf( o );
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    public Iterator iterator()
    {
        return list.iterator();
    }

    public int lastIndexOf( Object o )
    {
        return list.lastIndexOf( o );
    }

    public ListIterator listIterator()
    {
        return new ModifiableListIterator( this, list.listIterator() );
    }

    public ListIterator listIterator( int index )
    {
        return new ModifiableListIterator( this, list.listIterator( index ) );
    }

    public Object remove( int index )
    {
        setModified();
        return list.remove( index );
    }

    public boolean remove( Object o )
    {
        setModified();
        return list.remove( o );
    }

    public boolean removeAll( Collection arg0 )
    {
        setModified();
        return list.removeAll( arg0 );
    }

    public boolean retainAll( Collection arg0 )
    {
        setModified();
        return list.retainAll( arg0 );
    }

    public Object set( int arg0, Object arg1 )
    {
        setModified();
        return list.set( arg0, arg1 );
    }

    public int size()
    {
        return list.size();
    }

    public List subList( int fromIndex, int toIndex )
    {
        return new ModifiableList( this, list.subList( fromIndex, toIndex ) );
    }

    public Object[] toArray()
    {
        return list.toArray();
    }

    public Object[] toArray( Object[] arg0 )
    {
        return list.toArray( arg0 );
    }
}
