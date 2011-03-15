package $package;

/*
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

/**
 * A plugin that execute a jelly script when requested
 * to achieve the 'test' goal.
 *
 * @goal test
 * @description Test Jelly Mojo
 * @author <a href="mailto:eburghard@free.fr">Éric BURGHARD</a>
 * @version $Id$
 */
public class MyMojo
    extends AbstractMojo
{
    /**
     * A test value given to the script at runtime.
     *
     * @parameter expression="${test}" default-value="test is successfull !"
     * @optional
     */
    private String test;
    
    /**
     * Don't forget to put all your plugin's parameters in params
     */
    public void setParams() {
    	super.setParams();
    	params.put("test", test);
    }
}