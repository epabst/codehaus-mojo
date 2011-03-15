package org.apache.maven.plugin.simple;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal call
 * @author Jason van Zyl
 * @version $Revision:$
 *
 */
public class SimpleMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${keyOne}"
     */
    private String keyOne;

    /**
     * @parameter expression="${keyTwo}"
     */
    private String keyTwo;

    public String getKeyOne()
    {
        return keyOne;
    }

    public String getKeyTwo()
    {
        return keyTwo;
    }

    public void execute()
        throws MojoExecutionException
    {
        getLog().info( "i am the happy logger!" );
    }
}
