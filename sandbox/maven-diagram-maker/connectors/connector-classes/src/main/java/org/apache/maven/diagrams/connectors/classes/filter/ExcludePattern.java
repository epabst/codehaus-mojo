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
 * Compiled pattern for className exclussion
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 * 
 */
public class ExcludePattern extends FilterPattern
{
    private Boolean withKeepEdges;

    public ExcludePattern( String a_pattern )
    {
        super( a_pattern );
        withKeepEdges = false;
    }

    public ExcludePattern( String a_pattern, boolean a_withKeepEdges )
    {
        super( a_pattern );
        withKeepEdges = a_withKeepEdges;
    }

    public Boolean getWithKeepEdges()
    {
        return withKeepEdges;
    }

    public void setWithEdges( Boolean withKeepEdges )
    {
        this.withKeepEdges = withKeepEdges;
    }

}
