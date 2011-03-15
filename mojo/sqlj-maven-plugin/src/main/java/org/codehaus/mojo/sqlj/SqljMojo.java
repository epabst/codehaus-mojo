package org.codehaus.mojo.sqlj;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Generates SQLJ javacode.
 * 
 * @author <a href="mailto:david@codehaus.org">David J. M. Karlsen</a>
 * @goal sqlj
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class SqljMojo
    extends AbstractSqljMojo
{
    
    /**
     * Codepage for generated sources.
     * 
     * @parameter expression="${sqlj.encoding}" default-value="${project.build.sourceEncoding}"
     */
    private String encoding;

    /**
     * Show status while executing.
     * 
     * @parameter expression="${sqlj.status}" default-value="true"
     */
    private boolean status;

    /**
     * Explicit list of sqlj files to process.
     * 
     * @parameter expression="${sqlj.sqljFiles}"
     */
    private File[] sqljFiles;

    /**
     * Directories to recursively scan for .sqlj files.
     * 
     * @parameter expression="${sqlj.sqljDirectories}"
     */
    private File[] sqljDirs;

    /**
     * The enclosing project.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( StringUtils.isEmpty( encoding ) )
        {
            encoding = SystemUtils.FILE_ENCODING;
            getLog().warn( "No encoding given, falling back to system default value: " + encoding );
        }
        
        try {
            FileUtils.forceMkdir( getGeneratedResourcesDirectory().getAbsoluteFile() );
            FileUtils.forceMkdir( getGeneratedSourcesDirectory().getAbsoluteFile() );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }

        Set sqljFiles = getSqljFiles();
        for ( Iterator i=sqljFiles.iterator(); i.hasNext(); )
        {
            File file = (File) i.next();
            generate( file );
        }
        
        Resource resource = new Resource();
        resource.setDirectory( getGeneratedResourcesDirectory().getAbsolutePath() );
        mavenProject.addResource( resource );
        mavenProject.addCompileSourceRoot( getGeneratedSourcesDirectory().getAbsolutePath() );
    }
    
    /**
     * Generate resources for a given file.
     * @param file to generate from.
     * @throws MojoFailureException in case of failure.
     * @throws MojoExecutionException in case of execution failure.
     */
    private void generate( File file )
        throws MojoFailureException, MojoExecutionException
    {
        Class sqljClass;
        try
        {
            sqljClass = Class.forName( "sqlj.tools.Sqlj" );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoFailureException( "Please add sqlj to the plugins classpath " + e.getMessage() );
        }
        catch ( Exception e )
        {
            throw new MojoFailureException( e.getMessage() );
        }
        
        String[] arguments = { 
            "-dir=" + getGeneratedSourcesDirectory().getAbsolutePath(),
            "-d=" + getGeneratedResourcesDirectory().getAbsolutePath(),
            "-encoding=" + encoding, 
            status ? "-status" : "",
            "-compile=false", file.getAbsolutePath() };
        
        Integer returnCode = null;
        try
        {
            returnCode =
                (Integer) MethodUtils.invokeExactStaticMethod( sqljClass, "statusMain", new Object[] { arguments } );
        }
        catch ( Exception e )
        {
            throw new MojoFailureException( e.getMessage() );
        }

        if ( returnCode.intValue() != 0 )
        {
            throw new MojoExecutionException( "Bad returncode: " + returnCode );
        }
    }

    /**
     * Finds the union of files to generate for.
     * 
     * @return a Set of unique files.
     */
    private Set getSqljFiles()
    {
        Set files = new HashSet();

        final String[] extensions = new String[] { "sqlj" };
        for ( int i = 0; i < sqljDirs.length; i++ )
        {
            files.addAll( FileUtils.listFiles( sqljDirs[i], extensions, true ) );
        }

        files.addAll( Arrays.asList( sqljFiles ) );

        return files;
    }

}
