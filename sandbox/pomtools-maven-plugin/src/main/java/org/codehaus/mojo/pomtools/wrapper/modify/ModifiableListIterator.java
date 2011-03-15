package org.codehaus.mojo.pomtools.wrapper.modify;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import java.util.ListIterator;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ModifiableListIterator
    extends AbstractModifiableObject
    implements ListIterator
{
    private final ListIterator li;

    public ModifiableListIterator( Modifiable parent, ListIterator li )
    {
        super( parent );
        
        this.li = li;
    }

    public boolean hasNext()
    {
        return li.hasNext();
    }

    public Object next()
    {
        return li.next();
    }

    public boolean hasPrevious()
    {
        return li.hasPrevious();
    }

    public Object previous()
    {
        return li.previous();
    }

    public int nextIndex()
    {
        return li.nextIndex();
    }

    public int previousIndex()
    {
        return li.previousIndex();
    }

    public void remove()
    {
        setModified();
        li.remove();
    }

    public void set( Object arg0 )
    {
        setModified();
        set( arg0 );
    }

    public void add( Object arg0 )
    {
        setModified();
        li.add( arg0 );
    }
}
