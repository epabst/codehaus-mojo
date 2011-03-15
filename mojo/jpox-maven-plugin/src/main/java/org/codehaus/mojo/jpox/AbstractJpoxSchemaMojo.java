package org.codehaus.mojo.jpox;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.jpox.SchemaTool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Extensions of this class implement the
 * {@link #prepareModeSpecificCommandLineArguments(Commandline)} method and
 * provide <b>mode</b>-specific arguments to the {@link SchemaTool} invocation.
 * <p>
 * Following properties are at least required for the SchemaTool to execute:
 * <ul>
 * <li><code>javax.jdo.option.ConnectionDriverName</code></li>
 * <li><code>javax.jdo.option.ConnectionURL</code></li>
 * <li><code>javax.jdo.option.ConnectionUserName</code></li>
 * <li><code>javax.jdo.option.ConnectionPassword</code></li>
 * </ul>
 * <p>
 * SchemaTool properties can be specified in the POM configuration, or from
 * command line. In case of conflicts, property values specified from command
 * line take precedence.
 * <p>
 * An example JPOX-maven-plugin configuration can look like below:
 * <p>
 * <code>
 * <pre>
 *    &lt;plugin&gt;
 *      &lt;groupId&gt;org.codehaus.mojo&lt;/groupId&gt;
 *      &lt;artifactId&gt;jpox-maven-plugin&lt;/artifactId&gt;
 *      &lt;version&gt;${jpox.plugin.version}&lt;/version&gt;            
 *      &lt;configuration&gt;         
 *        &lt;outputFile&gt;${project.build.directory}/schema.sql&lt;/outputFile&gt;
 *        &lt;toolProperties&gt;
 *          &lt;property&gt;
 *            &lt;name&gt;javax.jdo.option.ConnectionDriverName&lt;/name&gt;
 *            &lt;value&gt;org.hsqldb.jdbcDriver&lt;/value&gt;
 *          &lt;/property&gt;
 *          &lt;property&gt;
 *            &lt;name&gt;javax.jdo.option.ConnectionURL&lt;/name&gt;
 *            &lt;value&gt;jdbc:hsqldb:mem:continuum&lt;/value&gt;
 *          &lt;/property&gt;
 *          &lt;property&gt;
 *            &lt;name&gt;javax.jdo.option.ConnectionUserName&lt;/name&gt;
 *            &lt;value&gt;sa&lt;/value&gt;
 *          &lt;/property&gt;                
 *          &lt;property&gt;
 *            &lt;name&gt;javax.jdo.option.ConnectionPassword&lt;/name&gt;
 *            &lt;value&gt;&lt;/value&gt;
 *          &lt;/property&gt;                
 *          &lt;property&gt;
 *            &lt;name&gt;log4j.configuration&lt;/name&gt;
 *            &lt;value&gt;file:${basedir}/src/main/resources/log4j.properties&lt;/value&gt;
 *          &lt;/property&gt;
 *          &lt;property&gt;
 *            &lt;name&gt;org.jpox.autoCreateTables&lt;/name&gt;
 *            &lt;value&gt;true&lt;/value&gt;
 *          &lt;/property&gt;
 *        &lt;/toolProperties&gt;              
 *      &lt;/configuration&gt;            
 *    &lt;/plugin&gt;
 * </pre>
 * </code>
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 * @see <a href="http://www.jpox.org/docs/1_1/schematool.html">JPOX SchemaTool</a>
 *      for other optional properties that can be specified.
 */
public abstract class AbstractJpoxSchemaMojo extends AbstractJpoxMojo
{

    /**
     * Qualified name for JPOX's {@link SchemaTool} main class.
     */
    private static final String TOOL_NAME_JPOX_SCHEMA_TOOL = "org.jpox.SchemaTool";

    /**
     * For validating if required properties were passed in.
     */
    private static final String[] requiredProperties =
        new String[] { "javax.jdo.option.ConnectionDriverName", "javax.jdo.option.ConnectionURL",
            "javax.jdo.option.ConnectionUserName", "javax.jdo.option.ConnectionPassword" };

    /**
     * Properties that will be passed to the SchemaTool's execution.
     * 
     * @parameter
     * @required
     */
    private Properties toolProperties;

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.mojo.jpox.AbstractJpoxMojo#executeJpoxTool(java.util.List,
     *      java.net.URL, java.util.List)
     */
    protected void executeJpoxTool( List pluginArtifacts, URL log4jProperties, List files )
        throws CommandLineException, MojoExecutionException
    {
        executeSchemaTool( pluginArtifacts, log4jProperties, files );
    }

    /**
     * Generates Database schema using the list of JDO mappings and enhanced
     * class files.
     * <p>
     * The list of class files is provided on the classpath by
     * {@link #getUniqueClasspathElements()}.
     * 
     * @param pluginArtifacts for creating classpath for Jpox tool execution.
     * @param log4jProperties log4j configuration.
     * @param files jdo mapping file list
     * @throws CommandLineException if there was an error invoking Jpox schema
     *             tool.
     * @throws MojoExecutionException
     */
    private void executeSchemaTool( List pluginArtifacts, URL log4jProperties, List files )
        throws CommandLineException, MojoExecutionException
    {
        // system properties override the ones specified in pom config
        Properties systemProperties = System.getProperties();

        // add conflicting properties from system properties to toolProperties
        Set toolPropertyKeys = toolProperties.keySet();

        for ( Iterator it = toolPropertyKeys.iterator(); it.hasNext(); )
        {
            String key = (String) it.next();

            if ( systemProperties.containsKey( key ) )
            {
                toolProperties.put( key, systemProperties.getProperty( key ) );
                getLog().warn( "Property '" + key + "' value specified in pom configuration will be overridden." );
            }
        }

        // validate required properties were specified
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < requiredProperties.length; i++ )
        {
            if ( !toolProperties.containsKey( requiredProperties[i] ) )
            {
                if ( sb.length() == 0 )
                    sb.append( "Missing required properties:\n" );
                sb.append( requiredProperties[i] + "\n" );
            }

        }

        if ( sb.length() > 0 )
            throw new MojoExecutionException( sb.toString() );

        // all good - start preparing the command.
        Commandline cl = new Commandline();

        cl.setExecutable( "java" );

        StringBuffer cpBuffer = new StringBuffer();

        for ( Iterator it = getUniqueClasspathElements().iterator(); it.hasNext(); )
        {
            cpBuffer.append( (String) it.next() );

            if ( it.hasNext() )
            {
                cpBuffer.append( File.pathSeparator );
            }
        }

        for ( Iterator it = pluginArtifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            try
            {
                cpBuffer.append( File.pathSeparator ).append( artifact.getFile().getCanonicalPath() );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error while creating the canonical path for '" + artifact.getFile()
                                + "'.", e );
            }
        }

        cl.createArgument().setValue( "-cp" );

        cl.createArgument().setValue( cpBuffer.toString() );

        // setup properties from the mojo configuration
        for ( Iterator it = toolPropertyKeys.iterator(); it.hasNext(); )
        {
            String key = (String) it.next();
            String val = ( null != toolProperties.getProperty( key ) ? toolProperties.getProperty( key ) : "" );
            cl.createArgument().setValue( "-D" + key + "=" + val );
        }

        cl.createArgument().setValue( TOOL_NAME_JPOX_SCHEMA_TOOL );

        // allow extensions to prepare Mode specific arguments
        prepareModeSpecificCommandLineArguments( cl );

        cl.createArgument().setValue( "-v" );

        for ( Iterator it = files.iterator(); it.hasNext(); )
        {
            File file = (File) it.next();

            cl.createArgument().setValue( file.getAbsolutePath() );
        }

        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        getLog().debug( "Executing command line:" );

        getLog().debug( cl.toString() );

        int exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );

        getLog().debug( "Exit code: " + exitCode );

        getLog().debug( "--------------------" );
        getLog().debug( " Standard output from the JPox schema tool:" );
        getLog().debug( "--------------------" );
        getLog().info( stdout.getOutput() );
        getLog().debug( "--------------------" );

        String stream = stderr.getOutput();

        if ( stream.trim().length() > 0 )
        {
            getLog().error( "--------------------" );
            getLog().error( " Standard error from the JPox schema tool:" );
            getLog().error( "--------------------" );
            getLog().error( stderr.getOutput() );
            getLog().error( "--------------------" );
        }

        if ( exitCode != 0 )
        {
            throw new MojoExecutionException( "The JPox schema tool exited with a non-null exit code." );
        }
    }

    /**
     * Template method that sets up arguments for the {@link SchemaTool}
     * depending upon the <b>mode</b> invoked.
     * <p>
     * This is expected to be implemented by extensions.
     * 
     * @param cl {@link Commandline} instance to set up arguments for.
     */
    protected abstract void prepareModeSpecificCommandLineArguments( Commandline cl );

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.mojo.jpox.AbstractJpoxMojo#getToolName()
     */
    protected String getToolName()
    {
        return TOOL_NAME_JPOX_SCHEMA_TOOL;
    }

}
