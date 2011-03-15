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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ClassNamesFilter
{
    private List<IncludePattern> includes;

    private List<ExcludePattern> excludes;

    /* ------------------- Getters and setters ---------------------------- */
    public List<IncludePattern> getIncludes()
    {
        return includes;
    }

    public void setIncludes( List<IncludePattern> includes )
    {
        this.includes = includes;
    }

    public List<ExcludePattern> getExcludes()
    {
        return excludes;
    }

    public void setExcludes( List<ExcludePattern> excludes )
    {
        this.excludes = excludes;
    }

    /* ------------------- Logic ---------------------------- */

    /**
     * Filters given collection of classNames (full-dot-qualified) and produces map from ClassName to
     * {@link ClassFilterStatus}
     */
    public Map<String, ClassFilterStatus> scan( Collection<String> col )
    {
        Map<String, ClassFilterStatus> result = new LinkedHashMap<String, ClassFilterStatus>( col.size() );
        for ( String className : col )
        {
            result.put( className, filter( className ) );
        }
        return result;
    }

    /**
     * Filters single full-dot-qualified class name in context of includes and excludes and returns
     * {@link ClassFilterStatus}
     * 
     * @param className
     * @return
     */
    public ClassFilterStatus filter( String className )
    {
        if ( isIncluded( className ) )
        {
            for ( ExcludePattern exclude : excludes )
            {
                if ( exclude.match( className ) )
                    return ( exclude.getWithKeepEdges() ) ? ClassFilterStatus.EXCLUDED_WITH_KEEP_EDGES
                                    : ClassFilterStatus.EXCLUDED_WITHOUT_KEEP_EDGES;
            }
            return ClassFilterStatus.INCLUDED;
        }
        else
            return ClassFilterStatus.NOT_INCLUDED;

    }

    /**
     * Returns true if given full-dot-qualified className is included by any includePattern
     * 
     * @param className
     * @return
     */
    private boolean isIncluded( String className )
    {
        for ( IncludePattern includePattern : includes )
        {
            if ( includePattern.match( className ) )
                return true;
        }
        return false;
    }
}
