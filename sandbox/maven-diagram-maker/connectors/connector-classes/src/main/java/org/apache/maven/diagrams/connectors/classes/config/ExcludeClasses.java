package org.apache.maven.diagrams.connectors.classes.config;

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
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 * @version $Id$
 */
public class ExcludeClasses
{
    /**
     * Regular expression pattern for the class name
     */
    private String pattern;

    /**
     * If the keepEdges is true, and there is such a situation: A--B--C and B is excluded the result is A--C, if
     * keepEdges ware false, the result would be A and C separetly.
     */
    private Boolean keepEdges;

    public ExcludeClasses( String pattern, Boolean keepEdges )
    {
        super();
        this.pattern = pattern;
        this.keepEdges = keepEdges;
    }

    /**
     * Gets the regexp pattern for classes to be excluded
     * 
     * @return
     */
    public String getPattern()
    {
        return pattern;
    }

    /**
     * Sets the regexp pattern for classes to be excluded
     * 
     * @return
     */
    public void setPattern( String pattern )
    {
        this.pattern = pattern;
    }

    /**
     * Gets keepEdges parameter.
     * 
     * If the keepEdges is true, and there is such a situation: A--B--C and B is excluded the result is A--C, if
     * keepEdges ware false, the result would be A and C separetly.
     */
    public Boolean getKeepEdges()
    {
        return keepEdges;
    }

    /**
     * Sets keepEdges parameter.
     * 
     * If the keepEdges is true, and there is such a situation: A--B--C and B is excluded the result is A--C, if
     * keepEdges ware false, the result would be A and C separetly.
     */
    public void setKeepEdges( Boolean keepEdges )
    {
        this.keepEdges = keepEdges;
    }
}
