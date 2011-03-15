package org.codehaus.mojo.freeform;

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

/**
 * This abstract class contains the all the constants for the classes
 * defined in the Netbeans Freeform plugin.
 *
 * @author <a href="mailto:raphaelpieroni@gmail.com">Raphaël Piéroni</a>
 */
public abstract class FreeformConstants
{
	//	Patch by Gergely Dombi 2006.04.10 - Single file IDE tasks
	/**
	 * The Ant scripting variable name of the build file that contains the custom
	 * IDE actions.  
	 */
	public static final String CUSTOM_SCRIPT = "custom.script";
	
	//~ custom Ant targets to simulate the default IDE actions
	
	public static final String DEBUG_SELECTED_FILES_IN_MAIN = "debug-selected-files-in-main";
	public static final String DEBUG_SELECTED_FILES_IN_TEST = "debug-selected-files-in-test";
	public static final String RUN_SELECTED_FILES_IN_MAIN = "run-selected-files-in-main";
	public static final String RUN_SELECTED_FILES_IN_TEST = "run-selected-files-in-test";
	public static final String COMPILE_SELECTED_FILES_IN_MAIN = "compile-selected-files-in-main";
	public static final String COMPILE_SELECTED_FILES_IN_TEST = "compile-selected-files-in-test";

	/**
	 * The file name that stores the custom IDE actions' ant targets.
	 */
	public static final String CUSTOM_BUILD_SCRIPT = "ide-file-targets.xml";	
}
