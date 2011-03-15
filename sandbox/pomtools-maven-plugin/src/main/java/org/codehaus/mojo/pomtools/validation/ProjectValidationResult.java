package org.codehaus.mojo.pomtools.validation;

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

import org.codehaus.mojo.pomtools.wrapper.custom.ProjectWrapper;
import org.apache.maven.project.validation.ModelValidationResult;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ProjectValidationResult
{

    private final ProjectWrapper project;
    
    private final ModelValidationResult validationResult;
    
    public ProjectValidationResult( ProjectWrapper project, ModelValidationResult validationResult )
    {
        this.project = project;
        this.validationResult = validationResult;
    }
    
    public boolean isValid()
    {
        return validationResult.getMessageCount() == 0;
    }

    public ProjectWrapper getProject()
    {
        return project;
    }

    public ModelValidationResult getValidationResult()
    {
        return validationResult;
    }


}
