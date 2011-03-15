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
/**
 * Every class (interface) can be in one of such a states (in terms of filtering classes)
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */

public enum ClassFilterStatus
{
    /**
     * Does not match any includePattern
     */
    NOT_INCLUDED,

    /**
     * Matches one of includePattern and doesn't match any excludePattern
     */
    INCLUDED,

    /**
     * Matches one of includePattern and matched excludePattern with keep edges first
     */
    EXCLUDED_WITH_KEEP_EDGES,

    /**
     * Matches one of includePattern and matched excludePattern without keep edges first
     */
    EXCLUDED_WITHOUT_KEEP_EDGES;

    /**
     * NOT_INCLUDED and EXCLUDED_WITHOUT_KEEP_EDGES classes are skipped
     */
    public boolean toSkip()
    {
        return this.equals( NOT_INCLUDED ) || this.equals( EXCLUDED_WITHOUT_KEEP_EDGES );
    }

}
