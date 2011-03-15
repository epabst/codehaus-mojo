package org.apache.maven.diagrams.connectors.classes.filter;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.util.HashMap;
import java.util.Map;

/**
 * Cache of filtered classNames. (after filtering className it is added to the cache and the next answer for the same
 * class is faster)
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class FilterRepository
{
    private ClassNamesFilter filter;

    private Map<String, ClassFilterStatus> filtered;

    public FilterRepository( ClassNamesFilter a_filter )
    {
        filter = a_filter;
        filtered = new HashMap<String, ClassFilterStatus>();
    }

    public ClassFilterStatus getStatus( String className )
    {
        ClassFilterStatus result = filtered.get( className );
        if ( result == null )
        {
            result = filter.filter( className );
            filtered.put( className, result );
        }
        return result;
    }
}
