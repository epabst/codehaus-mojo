package org.codehaus.mojo.repositorytools;

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.mojo.repositorytools.components.LocalRepositoryBuilder;

/**
 * An abstract mojo for adding artifacts to the local repository
 * 
 * @requiresProject false
 * @author tom
 */

public abstract class AbstractAddMojo extends AbstractMojo
{

	/**
	 * The remote repositories to resolve against. Defaults to the central
	 * repository. Format: id::layout::url,id::layout::url,...
	 * 
	 * @parameter expression="${remote}"
	 *            default-value="central::default::http://repo1.maven.org/maven2"
	 * 
	 */
	protected String remote;

	/**
	 * The local repository to create or add to. Defaults to a 'local'
	 * subdirectory of the working directory.
	 * 
	 * @parameter expression="${local}" default-value="local"
	 * 
	 */
	protected String local;

	/**
	 * Add the transitive dependencies of this artifact
	 * 
	 * @parameter expression="${transitive}" default-value="false"
	 */
	protected boolean transitive;
	
	/**
	 * @component
	 */
	protected LocalRepositoryBuilder builder;

}
