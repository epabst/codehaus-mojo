package org.codehaus.mojo.rmic;

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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.StringUtils;

/**
 * Generic super class of rmi compiler mojos.
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractRmiMojo
    extends AbstractMojo
    implements RmicConfig
{
    // ----------------------------------------------------------------------
    // Configurable parameters
    // ----------------------------------------------------------------------

    /**
     * A list of inclusions when searching for classes to compile.
     * 
     * @parameter
     */
    protected Set includes;

    /**
     * A list of exclusions when searching for classes to compile.
     * 
     * @parameter
     */
    protected Set excludes;

    /**
     * The id of the rmi compiler to use.
     * 
     * @parameter default-value="sun"
     */
    protected String compilerId;

    /**
     * The version of the rmi protocol to which the stubs should be compiled. Valid values include 1.1, 1.2, compat. See
     * the rmic documentation for more information. If nothing is specified the underlying rmi compiler will
     * choose the default value.  For example, in sun jdk 1.5 the default is 1.2.
     * 
     * @parameter
     */
    private String version;

    /**
     * Create stubs for IIOP.
     * 
     * @parameter default-value="false"
     */
    private boolean iiop;

    /**
     * Do not create stubs optimized for same process.
     * 
     * @parameter
     */
    private boolean noLocalStubs;

    /**
     * Create IDL.
     * 
     * @parameter default-value="false"
     */
    private boolean idl;

    /**
     * Do not generate methods for valuetypes.
     * 
     * @parameter
     */
    private boolean noValueMethods;

    /**
     * Do not delete intermediate generated source files.
     * 
     * @parameter default-value="false"
     */
    private boolean keep;

    /**
     * Turn off rmic warnings.
     * 
     * @parameter
     */
    private boolean nowarn;

    /**
     * Enable verbose rmic output.
     * 
     * @parameter
     */
    private boolean verbose;

    /**
     * Time in milliseconds between automatic recompilations. A value of 0 means that up to date rmic output classes
     * will not be recompiled until the source classes change.
     * 
     * @parameter default-value=0
     */
    private int staleMillis;

    // ----------------------------------------------------------------------
    // Constant parameters
    // ----------------------------------------------------------------------

    /**
     * @component org.apache.maven.plugin.rmic.RmiCompilerManager
     */
    private RmiCompilerManager rmiCompilerManager;

    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * Get the list of elements to add to the classpath of rmic
     * 
     * @return list of classpath elements
     */
    public abstract List getProjectClasspathElements();

    /**
     * Get the directory where rmic generated class files are written.
     * 
     * @return the directory
     */
    public abstract File getOutputDirectory();

    /**
     * Get the directory where Remote impl classes are located.
     * 
     * @return path to compiled classes
     */
    public abstract File getClassesDirectory();

    /**
     * Main mojo execution.
     * 
     * @throws MojoExecutionException if there is a problem executing the mojo.
     */
    public void execute()
        throws MojoExecutionException
    {
        if ( this.includes == null )
        {
            this.includes = Collections.singleton( "**/*" );
        }

        if ( this.excludes == null )
        {
            this.excludes = new HashSet();
        }
        // Exclude _Stub files from being recompiled by rmic.
        excludes.add( "**/*_Stub.class" );

        RmiCompiler rmiCompiler;

        try
        {
            rmiCompiler = rmiCompilerManager.getRmiCompiler( compilerId );
        }
        catch ( NoSuchRmiCompilerException e )
        {
            throw new MojoExecutionException( "No such RMI compiler installed '" + compilerId + "'." );
        }

        if ( !getOutputDirectory().isDirectory() )
        {
            if ( !getOutputDirectory().mkdirs() )
            {
                throw new MojoExecutionException( "Could not make output directory: " + "'" 
                                                  + getOutputDirectory().getAbsolutePath() + "'." );
            }
        }

        try
        {
            // Get the list of classes to compile
            List remoteClassesToCompile = scanForRemoteClasses();

            if ( remoteClassesToCompile.size() == 0 )
            {
                getLog().info( "No out of date rmi classes to process." );
                return;
            }

            getLog().info( "Compiling " + remoteClassesToCompile.size() + " remote classes" );
            rmiCompiler.execute( this, remoteClassesToCompile );
        }
        catch ( RmiCompilerException e )
        {
            throw new MojoExecutionException( "Error while executing the RMI compiler.", e );
        }
    }

    /**
     * Get the list of elements to add to the classpath of rmic
     * 
     * @return list of classpath elements
     */
    public List getRmicClasspathElements()
    {
        List classpathElements = getProjectClasspathElements();
        
        if ( !classpathElements.contains( getClassesDirectory().getAbsolutePath() ) )
        {
            classpathElements.add( getClassesDirectory().getAbsolutePath() );
        }

        return classpathElements;
    }

    /**
     * Search the input directory for classes to compile.
     * 
     * @return a list of class names to rmic
     */
    public List scanForRemoteClasses()
    {
        List remoteClasses = new ArrayList();

        try
        {
            // Set up the classloader
            List classpathList = generateUrlCompileClasspath();
            URL[] classpathUrls = new URL[classpathList.size()];
            classpathUrls[0] = getClassesDirectory().toURL();
            classpathUrls = (URL[]) classpathList.toArray( classpathUrls );
            URLClassLoader loader = new URLClassLoader( classpathUrls );

            // Scan for remote classes
            SourceInclusionScanner scanner = new StaleSourceScanner( staleMillis, this.includes, this.excludes );
            scanner.addSourceMapping( new SuffixMapping( ".class", "_Stub.class" ) );
            Collection staleRemoteClasses = scanner.getIncludedSources( getClassesDirectory(), getOutputDirectory() );

            for ( Iterator iter = staleRemoteClasses.iterator(); iter.hasNext(); )
            {
                // Get the classname and load the class
                File remoteClassFile = (File) iter.next();
                URI relativeURI = getClassesDirectory().toURI().relativize( remoteClassFile.toURI() );
                String className =
                    StringUtils.replace( StringUtils.replace( relativeURI.toString(), ".class", "" ), "/", "." );
                Class remoteClass = loader.loadClass( className );

                // Check that each class implements java.rmi.Remote, ignore interfaces unless in IIOP mode
                if ( java.rmi.Remote.class.isAssignableFrom( remoteClass ) 
                                && ( !remoteClass.isInterface() || isIiop() ) )
                {
                    remoteClasses.add( className );
                }
            }

            // Check for classes in a classpath jar
            for ( Iterator iter = includes.iterator(); iter.hasNext(); )
            {
                String include = (String) iter.next();
                File includeFile = new File( getClassesDirectory(), include );
                if ( ( include.indexOf( "*" ) != -1 ) || includeFile.exists() )
                {
                    continue;
                }
                // We have found a class that is not in the classes dir.
                String fqClassName = StringUtils.replace( StringUtils.replace( include, ".class", "" ), "/", "." );
                remoteClasses.add( fqClassName );
            }
        }
        catch ( Exception e )
        {
            getLog().warn( "Problem while scanning for classes: " + e );
        }
        return remoteClasses;
    }

    /**
     * Returns a list of URL objects that represent the classpath elements. This is useful for using a URLClassLoader
     * 
     * @return list of url classpath elements
     */
    protected List generateUrlCompileClasspath()
        throws MojoExecutionException
    {
        List rmiCompileClasspath = new ArrayList();
        try
        {
            rmiCompileClasspath.add( getClassesDirectory().toURL() );
            Iterator iter = getRmicClasspathElements().iterator();
            while ( iter.hasNext() )
            {
                URL pathUrl = new File( (String) iter.next() ).toURL();
                rmiCompileClasspath.add( pathUrl );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new MojoExecutionException( "Problem while generating classpath: " + e.getMessage() );
        }
        return rmiCompileClasspath;
    }

    public String getCompilerId()
    {
        return compilerId;
    }

    public boolean isIiop()
    {
        return iiop;
    }

    public boolean isIdl()
    {
        return idl;
    }

    public boolean isKeep()
    {
        return keep;
    }

    public String getVersion()
    {
        return version;
    }

    public boolean isNowarn()
    {
        return nowarn;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public boolean isNoLocalStubs()
    {
        return noLocalStubs;
    }

    public boolean isNoValueMethods()
    {
        return noValueMethods;
    }

}
