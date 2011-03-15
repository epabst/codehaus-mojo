package org.apache.maven.plugin.deb;

import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @description A Maven 2 mojo which creates a Debian package from a Maven2 project.
 * @goal attach-deb
 * @phase package
 * @requiresProject
 */
public class AttachDeb
    extends AbstractDebMojo
{
    /**
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File outputDirectory;

    public void execute()
        throws MojoFailureException
    {
        getArtifact().setFile( new File( outputDirectory, getDebTool().getDebFileName() ) );
    }
}
