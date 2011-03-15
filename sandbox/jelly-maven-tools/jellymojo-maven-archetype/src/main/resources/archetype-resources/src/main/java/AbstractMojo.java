package $package;

/*
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

import org.codehaus.mojo.AbstractJellyMojo;
import org.apache.maven.project.MavenProject;

import java.util.HashMap;

/**
 * Intermediate Abstract class just to hide the adding of the references to
 * the MavenProject and Mojo objects. This stuff cannot be deported inside
 * AbstractJellyMojo class since the source must be parsed for constructing
 * plugin.xml correctly (reference to project).
 *
 * @description Abstract Jelly Mojo
 * @author <a href="mailto:eburghard@free.fr">Éric BURGHARD</a>
 * @version $Id$
 */
public abstract class AbstractMojo
    extends AbstractJellyMojo
{
	/**
     * Reference of the maven project
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;
    
    /**
     * Add references to MavenProject and Mojo objects. concrete
     * classes which override setParams must call super.setSetParams()
     */
    public void setParams() {
    	params = new HashMap();
    	params.put("project", project);
    	params.put("mojo", this);
    }
}