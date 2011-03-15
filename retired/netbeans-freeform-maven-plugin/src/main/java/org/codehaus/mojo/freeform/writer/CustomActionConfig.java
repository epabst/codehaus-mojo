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
package org.codehaus.mojo.freeform.writer;

import org.codehaus.mojo.freeform.FreeformConstants;

/**
 * This class encapsulates the configuration of a custom IDE action.
 * @see http://www.netbeans.org/kb/articles/freeform-config-40.html
 * for more details
 * @author <a href="mailto:gergely.dombi.sp@lhsystems.com">Gergely Dombi</a>
 *
 */
public class CustomActionConfig {
	
	/**
	 * The name of the IDE action. Possible values are:
	 * <ul>
	 *   <li>compile.single</li>
	 *   <li>run.single</li>
	 *   <li>debug.single</li>
	 * </ul>
	 * test.single is supported by the IDE but unimplemented by this module at this moment. 
	 */
	private String actionName;
	
	/**
	 * The path to the custom ant script.
	 */
	private String script;
	
	/**
	 * The ant target's name in the custom script..
	 */
	private String target;
	
	/**
	 * This property should be set by the IDE at runtime. Generally the ant target fails
	 * if it's not set.
	 */
	private String contextProperty;
	
	/**
	 * The folder to search for the subject of the build.
	 */
	private String contextFolder;
	
	/**
	 * A regular expression defining the files to be selected by the search.
	 */	
	private String contextPattern;
	
	/**
	 * The format to be used with the selected file(s). Possible values are:
	 * <ul>
	 *   <li>relative-path</li>
	 *   <li>relative-path-noext</li>
	 *   <li>absolute-path</li>
	 *   <li>absolute-path-noext</li>
	 *   <li>java-name</li>
	 * </ul>
	 */
	private String contextFormat;
	
	/**
	 * Flag to indicate whether multiple files are allowed by this action or not.
	 */
	private boolean singleOnly;
	
	/**
	 * If multiple files are allowed this member field contains the separator.
	 */
	private String separatedFiles;
	
	
	public CustomActionConfig() {
		script = "${" + FreeformConstants.CUSTOM_SCRIPT  + "}";
		separatedFiles = ",";		
	}
	
	
	
	
	public String getContextFolder() {
		return contextFolder;
	}


	public void setContextFolder(String contextFolder) {
		this.contextFolder = contextFolder;
	}


	public String getContextFormat() {
		return contextFormat;
	}


	public void setContextFormat(String contextFormat) {
		this.contextFormat = contextFormat;
	}


	public String getContextPattern() {
		return contextPattern;
	}


	public void setContextPattern(String contextPattern) {
		this.contextPattern = contextPattern;
	}


	public String getContextProperty() {
		return contextProperty;
	}


	public void setContextProperty(String contextProperty) {
		this.contextProperty = contextProperty;
	}


	public String getScript() {
		return script;
	}


	public void setScript(String script) {
		this.script = script;
	}


	public String getTarget() {
		return target;
	}


	public void setTarget(String target) {
		this.target = target;
	}




	public boolean isSingleOnly() {
		return singleOnly;
	}


	public void setSingleOnly(boolean isSingleOnly) {
		this.singleOnly = isSingleOnly;
	}


	public String getSeparatedFiles() {
		return separatedFiles;
	}


	public void setSeparatedFiles(String separatedFiles) {
		this.separatedFiles = separatedFiles;
	}




	public String getActionName() {
		return actionName;
	}




	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
}
