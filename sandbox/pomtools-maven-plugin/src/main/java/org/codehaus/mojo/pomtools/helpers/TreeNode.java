package org.codehaus.mojo.pomtools.helpers;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/** Used for representing and printing a hierarchy of objects.
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class TreeNode
{
    protected static final String NEWLINE = "\n";
    
    protected static final int MAX_DEPTH = 1024;
    
    public static final Stringifier DEFAULT_STRINGIFIER = new Stringifier() {
        public String getNodeLabel( TreeNode tree )
        {
            return String.valueOf( tree.getId() );
        }
    };
    
    private final Comparator comparator;
    
    private final Object oid;
    
    private final TreeNode parent;
    
    private final TreeMap children;

    public TreeNode( Object id )
    {
        this( id, null, null );
    }
    
    public TreeNode( Object id, Comparator comparator )
    {
        this( id, comparator, null );
    }
    
    public TreeNode( Object id, Comparator comparator, TreeNode parent )
    {
        this.oid = id;
        this.comparator = comparator;
        this.parent = parent;
        
        this.children = new TreeMap( comparator );
    }
    
    protected Comparator getComparator()
    {
        return comparator;
    }

    public boolean containsChild( Object id )
    {
        return children.containsKey( id );
    }
    
    public TreeNode addChild( Object id )
    {
        if ( !children.containsKey( id ) )
        {
            children.put( id, new TreeNode( id, comparator, this ) );
        }
        
        return (TreeNode) children.get( id );
    }
    
    public TreeNode getParent() 
    {
        return this.parent;
    }
    
    public TreeNode getSingleChild()
    {
        if ( this.children.isEmpty() || this.children.size() > 1 )
        {
            throw new IllegalArgumentException( "This node does not contain a single child" );
        }
        
        return (TreeNode) children.values().toArray()[0];
    }
    
    public List getChildren() 
    {
        return Collections.unmodifiableList( new ArrayList( children.values() ) );
    }
    
    public Object getId()
    {
        return this.oid;
    }

    public boolean hasChildren()
    {
        return !children.isEmpty();
    }
    
    public String toString()
    {
        return toString( DEFAULT_STRINGIFIER );
    }
    
    public String toString( Stringifier stringifier )
    {
        StringBuffer sb = new StringBuffer();
        
        boolean[] hasMore = new boolean[MAX_DEPTH];
        
        printNode( stringifier, this, sb, 0, hasMore );
        
        return sb.toString();
    }
    
    protected void printNode( Stringifier stringifier, TreeNode tree, StringBuffer sb, int depth, boolean[] hasMore )
    {
        for ( int i = 1; i < depth; i++ )
        {
            sb.append( hasMore[i] ? "|   " : "    " );
        }
        
        if ( depth > 0 )
        {
            sb.append( hasMore[depth] ? "|-- " : "`-- " );
        }
        
        int childDepth = depth + 1; 
        if ( this.oid == null && this.parent == null )
        {
            // don't print anything for a null parent and null label
            childDepth = depth;
        }
        else
        {
            sb.append( stringifier.getNodeLabel( tree ) );
        
            sb.append( NEWLINE );
        }
        
        if ( tree.hasChildren() )
        {
            for ( Iterator iter = tree.getChildren().iterator(); iter.hasNext(); )
            {
                TreeNode childTree = (TreeNode) iter.next();
                
                hasMore[depth + 1] = iter.hasNext();
            
                printNode( stringifier, childTree, sb, childDepth, hasMore );
            }
        }
    }
    
    
    public interface Stringifier 
    {
        String getNodeLabel( TreeNode treeNode );
    }
}
