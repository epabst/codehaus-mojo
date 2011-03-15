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


/**
 * Goal that injects Google analytics script into the generated website.
 * Ought to be executed after site:site and before site:deploy, or added to the site phase.
 *
 * @author mkleint@codehaus.org
 * @goal inject
 * @requiresProject false
 * @phase site
 */
public class InjectAnalyticsMojo
    extends AbstractPostProcessMojo
{

    /**
     * Google analytics token to inject into site.
     *
     * @parameter expression="${analyticsId}"
     * @required
     */
    protected String id;

    public void execute()
        throws MojoExecutionException
    {
        getLog().info("Injecting Google Analytics ID: " + id);
        processChildrenHtml(outputDirectory, "</head>",
                            "<script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\">" +
                            "</script>" + 
                            "<script type=\"text/javascript\">" + 
                            "_uacct = \"" + id + "\";" + 
                            "urchinTracker();" + 
                            "</script></head>");
        
    }

}
