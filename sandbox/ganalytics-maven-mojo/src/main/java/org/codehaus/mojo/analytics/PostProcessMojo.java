package org.codehaus.mojo.analytics;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;

/**
 * Goal that injects any html snippet into the generated website. Requires a certain token 
 * to be present in the generated website with gets replaced by the html snippet.
 * Ought to be executed after site:site and before site:deploy, or added to the site phase.
 *
 * @author mkleint@codehaus.org
 * @goal postprocess
 * @requiresProject false
 * @phase site
 */
public class PostProcessMojo
    extends AbstractPostProcessMojo
{

    /**
     * Teh token present in generated website to be replaced by the new content.
     *
     * @parameter
     * @required
     */
    protected String token;
    
    /**
     * The content of the file will replace the token in the generated web site files.
     * @parameter
     * @required
     */ 
    protected File replaceContent;
    

    public void execute()
        throws MojoExecutionException
    {
        if (! replaceContent.exists()) {
            throw new MojoExecutionException("Please make sure the replaceContent parameter point to an existing file.");
        }
        try {
            String replace = fileRead(replaceContent);
            processChildrenHtml(outputDirectory, token, replace);
        } catch (IOException ex) {
            throw new MojoExecutionException("Cannot read the content of replaceContent parameter", ex);
        }
    }

}
