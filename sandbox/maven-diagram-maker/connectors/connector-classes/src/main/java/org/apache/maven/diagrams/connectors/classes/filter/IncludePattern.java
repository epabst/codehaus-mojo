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
import java.util.LinkedList;
import java.util.List;

/**
 * Compiled pattern for className inclussion
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class IncludePattern extends FilterPattern
{

    public IncludePattern( String a_pattern )
    {
        super( a_pattern );
    }

    static List<IncludePattern> createList( String[] inputs )
    {
        List<IncludePattern> result = new LinkedList<IncludePattern>();
        for ( String pattern : inputs )
        {
            result.add( new IncludePattern( pattern ) );
        }
        return result;
    }

}
