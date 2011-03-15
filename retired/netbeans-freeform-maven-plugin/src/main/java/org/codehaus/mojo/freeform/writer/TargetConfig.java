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

/**
 * The configuration for a custom Ant target.
 * @author <a href="mailto:gergely.dombi.sp@lhsystems.com">Gergely Dombi</a>
 */
public class TargetConfig {

	/**
	 * The name of the Ant target.
	 */
	protected String targetName;
	/**
	 * The property that has to be set by the IDE at runtime (to the selected files in the
	 * tree view).
	 */
	protected String failUnlessSet;
	/**
	 * The java source directory.
	 */
	protected String sourceDir;
	/**
	 * The build output directory.
	 */
	protected String outputDir;
	/**
	 * The java source version. Defaults to 1.5.
	 */
	protected String sourceVersion;
	
	public TargetConfig() {
		sourceVersion = "1.5";
	}
	
	
	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}	
	
	public String getFailUnlessSet() {
		return failUnlessSet;
	}

	public void setFailUnlessSet(String failUnlessSet) {
		this.failUnlessSet = failUnlessSet;
	}
	
	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String getSourceVersion() {
		return sourceVersion;
	}

	public void setSourceVersion(String sourceVersion) {
		this.sourceVersion = sourceVersion;
	}	
	
}
