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

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.resolver.ResolutionNode;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;
import org.apache.maven.project.MavenProjectBuilder;

/** This class is a hack to temporarily access private members of
 * maven classes. I plan to submit patches to maven for exposing the
 * items that this class is accessing.  Do not use these methods!
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public final class APIWorkaroundHelper
{
    private APIWorkaroundHelper()
    {
    }
    
    /** This is a temporary hack to get model plugin working without depending on 
     * changes to other components.  MavenProjectBuilder needs a clear function
     * to clear its internal cache.
     */
    public static void clearBuilderCache( MavenProjectBuilder builder )
    {
        Map modelCache = (Map) getPrivateFieldValue( builder, "modelCache" );
        
        modelCache.clear();        
    }
    
    /** This is a temporary hack to get model plugin working without depending on 
     * changes to other components.  ResolutionNode needs a function that returns its
     * parent.  ResolutionNode.getParent()
     */
    public static ResolutionNode getParent( ResolutionNode node )
    {
        return (ResolutionNode) getPrivateFieldValue( node, "parent" );        
    }

    /** Similar to {@link ResolutionNode#getDependencyTrail()} except 
     * it returns a list of ResolutionNodes rathen than their keys.
     * 
     * @param node
     * @return
     */
    public static List getNodeLineage( ResolutionNode node )
    {
        List path = new LinkedList();
        ResolutionNode currentNode = node;
        while ( currentNode != null )
        {
            path.add( 0, currentNode );
            currentNode = APIWorkaroundHelper.getParent( currentNode );
        }
        return path;
    }
    
    private static Object getPrivateFieldValue( Object o, String fieldName )
    {
        try
        {
            Field field = o.getClass().getDeclaredField( fieldName );
            
            field.setAccessible( true );
            
            return field.get( o );
        }
        catch ( SecurityException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( NoSuchFieldException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ModelReflectionException( e );
        }
    }
}
