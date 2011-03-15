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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public abstract class AbstractModifiableObject
    implements Modifiable
{
    private final Modifiable modifiableParent;
    
    private boolean modified = false;
    
    private final List children;

    public AbstractModifiableObject( Modifiable parent )
    {
        this.modifiableParent = parent;
        this.children = new ArrayList();
        
        if ( this.modifiableParent != null )
        {
            parent.addChild( this );
        }
    }
    
    public boolean isModified()
    {
        return modified;
    }

    public void setModified()
    {
        setModified( true );
    }
    
    /** Setting modified to true propagates the value up to our parent.
     * Setting it to false will propagate down to our children.
     * <p>
     * If I am modified, that makes my parent modified.  However, if I am
     * setting my modified to false, that does not mean my parent is no longer 
     * modified.
     * 
     */
    public void setModified( boolean modified )
    {
        this.modified = modified;
        
        if ( modified )
        {
            if ( modifiableParent != null )
            {
                modifiableParent.setModified( this.modified );
            }
        }
        else
        {
            // We are setting modified to false, so propagate that to all of our children
            for ( Iterator i = children.iterator(); i.hasNext(); )
            {
                Modifiable child = (Modifiable) i.next();
                if ( child.isModified() )
                {
                    child.setModified( this.modified );
                }
            }
        }
    }
    
    public Modifiable addChild( Modifiable child )
    {
        children.add( child );
        
        return this;
    }

}
