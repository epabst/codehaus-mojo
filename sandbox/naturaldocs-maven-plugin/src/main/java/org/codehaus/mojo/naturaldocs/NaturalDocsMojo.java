package org.codehaus.mojo.naturaldocs;

/*
 * The MIT License
 * 
 * Copyright (c) 2008, The Codehaus
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/*
 * Debug syntax
 * mvn org.codehaus.mojo.naturaldocs:naturaldocs-maven-plugin:1.0-SNAPSHOT:generate --debug
 */

/**
 * Generate the Natural Docs report. This will invoke Natural Docs already installed on your system.
 * <p>
 * TODO: Could this be bound to the site phase by default and included in the site documentation (perhaps in place of
 * JavaDoc)?
 * <p>
 * TODO: Do you want to consider a main and test invocation of the plugin?
 * 
 * @author <a href="mailto:timothy.astle@caris.com">Tim Astle</a>
 * @goal generate
 */
public class NaturalDocsMojo
    extends AbstractMojo
{
    /**
     * The Natural Docs home directory. TODO: These should be private and have getters/setters.
     * 
     * @parameter
     * @required
     */
    protected File naturalDocsHome;

    /**
     * The input directory to run Natural Docs on.
     * <p>
     * TODO: I would think having some defaults here might be useful e.g. src/main/java
     * 
     * @parameter
     * @required
     */
    protected File input;

    /**
     * The output format of the generated documenation. The supported formats are HTML and FramedHTML.
     * 
     * @parameter default-value="HTML"
     * @required
     */
    protected String outputFormat;

    /**
     * The output directory where the generated documenation will be placed.
     * 
     * @parameter expression="${project.naturaldocs.build.directory}"
     *            default-value="${project.build.directory}/naturaldocs"
     * @required
     */
    protected File output;

    /**
     * The project directory where the configuration for the project documentation is located.
     * 
     * @parameter
     * @required
     */
    protected File project;

    /**
     * Excludes a subdirectory from being scanned. The output and project directories are automatically excluded.
     * 
     * @parameter
     */
    protected File excludeImport;

    /**
     * Adds a directory to search for image files when using (see [file]).
     * 
     * @parameter
     */
    protected File images;

    /**
     * Selects the CSS style for HTML output. The default styles are Default, Small, and Roman. You can use any CSS file
     * in your project directory or Natural Docs' Styles directory just by using its name without the .css extension. If
     * you include more than one, they will all be included in the HTML that order.
     * 
     * @parameter
     */
    protected List<String> style;

    /**
     * Rebuilds everything from scratch. All source files will be rescanned and all output files will be rebuilt
     * 
     * @parameter default-value="false"
     */
    protected boolean rebuild;

    /**
     * Rebuilds all output files from scratch.
     * 
     * @parameter default-value="false"
     */
    protected boolean rebuildOutput;

    /**
     * Sets the number of spaces tabs should be expanded to. This only needs to be set if you use tabs in example code
     * or text diagrams. The default is 4.
     * 
     * @parameter
     */
    protected Integer tabLength;

    /**
     * Sets the syntax highlighting option used in the output. Off turns off all syntax highlighting. Code applies it to
     * prototypes and (start code) segments. All applies it to prototypes, (start code) segments, and lines prefixed
     * with >, |, or :. The default is Code.
     * 
     * @parameter
     */
    protected String highlight;

    /**
     * Tells Natural Docs to only include what you explicitly document in the output, and not to find undocumented
     * classes, functions, and variables. This option is only relevant if you have full language support.
     * 
     * @parameter default-value="false"
     */
    protected boolean documentedOnly;

    /**
     * Tells Natural Docs to only use the file name for its menu and page titles. It won't try to determine one from the
     * contents of the file.
     * 
     * @parameter default-value="false"
     */
    protected boolean onlyFileTitles;

    /**
     * Tells Natural Docs to not automatically create group topics if you don't add them yourself.
     * 
     * @parameter default-value="false"
     */
    protected boolean noAutoGroup;

    /**
     * Sets the character set property of the generated HTML, such as UTF-8 or Shift_JIS. The default leaves it
     * unspecified.
     * 
     * @parameter
     */
    protected String characterSet;

    /**
     * Suppresses all non-error output.
     * 
     * @parameter default-value="false"
     */
    protected boolean quiet;

    /*
     * (non-Javadoc)
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    public void execute()
        throws MojoExecutionException
    {
        this.checkConfig();

        // Get the Natual Docs script path
        String naturalDocsScriptPath = this.getNaturalDocsScriptPath();

        // We copy the project directory to avoid file modification if project is in a RCS.
        this.copyProjectDirectory();

        // Run Natural Docs
        this.generateNaturalDocs( this.createNaturalDocsCommand( naturalDocsScriptPath ) );
    }

    /**
     * Verify the configuration passed given to the mojo.
     * 
     * @throws MojoExecutionException The configuration provided was not correct.
     */
    protected void checkConfig()
        throws MojoExecutionException
    {
        if ( !this.naturalDocsHome.isDirectory() )
        {
            throw new MojoExecutionException(
                                              "The <naturalDocsHome/> configuration must be set to the directory where Natural Docs is located." );
        }
        if ( !this.input.isDirectory() )
        {
            throw new MojoExecutionException( "The <input/> configuration must be set to a valid directory." );
        }
        if ( !this.project.isDirectory() )
        {
            throw new MojoExecutionException( "The <project/> configuration must be set to a valid directory." );
        }
        if ( !( this.outputFormat.equals( "HTML" ) || this.outputFormat.equals( "FramedHTML" ) ) )
        {
            throw new MojoExecutionException( "The <outputFormat/> configuration must be set to HTML or FramedHTML." );
        }

        // Optional check
        if ( this.highlight != null
            && ( !this.highlight.equals( "Off" ) && !this.highlight.equals( "Code" ) && !this.highlight.equalsIgnoreCase( "All" ) ) )
        {
            throw new MojoExecutionException( "The <highlight/> configuration must be set to Off or Code or All." );
        }
    }

    /**
     * Construct a fully qualified path to where the Natural Docs perl script should be located based on the given
     * Natural Docs home directory.
     * 
     * @return The path to the natural docs perl script.
     * @throws MojoExecutionException Could not find the NaturalDocs perl script.
     */
    protected String getNaturalDocsScriptPath()
        throws MojoExecutionException
    {
        String naturalDocsScriptPath = naturalDocsHome.getPath() + File.separator + "NaturalDocs";
		// TODO: Surround with debug check.
        this.getLog().debug( "Natural Docs script path: " + naturalDocsScriptPath );
        File f = new File( naturalDocsScriptPath );
        if ( !f.isFile() )
        {
            throw new MojoExecutionException(
                                              "Cannot find the NaturalDocs perl script in the given <naturalDocsHome/> location." );
        }
        return naturalDocsScriptPath;
    }

    /**
     * Create the command that will be used to execute Natural Docs.
     * 
     * @param naturalDocsScriptPath The path to the Natural Docs script file.
     * @return The command that will run Natural Docs.
     * @see <a href="http://www.naturaldocs.org/running.html">Running Natural Docs</a>
     */
    protected String createNaturalDocsCommand( String naturalDocsScriptPath )
    {
        // Mandatory parameters
        // TODO: You probably want a StringBuilder instead:
        // http://download.oracle.com/javase/1.5.0/docs/api/java/lang/StringBuilder.html
        StringBuffer naturalDocsCommand = new StringBuffer( "perl " );
        naturalDocsCommand.append( "\"" + naturalDocsScriptPath + "\"" );
        naturalDocsCommand.append( " -i " );
        naturalDocsCommand.append( "\"" + this.input + "\"" );
        naturalDocsCommand.append( " -o " );
        naturalDocsCommand.append( this.outputFormat );
        naturalDocsCommand.append( " " );
        naturalDocsCommand.append( "\"" + this.output + "\"" );
        naturalDocsCommand.append( " -p " );
        naturalDocsCommand.append( "\"" + this.project + "\"" );

        // Optional parameters
        if ( this.excludeImport != null && excludeImport.isDirectory() )
        {
            naturalDocsCommand.append( " -xi " );
            naturalDocsCommand.append( "\"" + excludeImport.getPath() + "\"" );
            this.getLog().debug( "Exclude Import: " + excludeImport.getPath() );
        }
        if ( this.images != null && images.isDirectory() )
        {
            naturalDocsCommand.append( " -img " );
            naturalDocsCommand.append( "\"" + images.getPath() + "\"" );
            this.getLog().debug( "Images: " + images.getPath() );
        }
        if ( this.style != null && this.style.size() > 0 )
        {
            naturalDocsCommand.append( " -s" );
            for ( String s : this.style )
            {
                naturalDocsCommand.append( " " + s );
                this.getLog().debug( "Style: " + s );
            }
        }
        if ( this.rebuild )
        {
            naturalDocsCommand.append( " -r" );
            this.getLog().debug( "Rebuild" );
        }
        if ( this.rebuildOutput )
        {
            naturalDocsCommand.append( " -ro" );
            this.getLog().debug( "Rebuild Output" );
        }
        if ( this.tabLength != null )
        {
            naturalDocsCommand.append( " -t " + this.tabLength );
            this.getLog().debug( "Tab Length: " + this.tabLength );
        }
        if ( this.highlight != null )
        {
            naturalDocsCommand.append( " -hl " + this.highlight );
            this.getLog().debug( "Highlight: " + this.highlight );
        }
        if ( this.documentedOnly )
        {
            naturalDocsCommand.append( " -do" );
            this.getLog().debug( "Documented Only" );
        }
        if ( this.onlyFileTitles )
        {
            naturalDocsCommand.append( " -oft" );
            this.getLog().debug( "Only File Titles" );
        }
        if ( this.noAutoGroup )
        {
            naturalDocsCommand.append( " -nag" );
            this.getLog().debug( "No Auto Group" );
        }
        if ( this.characterSet != null )
        {
            naturalDocsCommand.append( " -cs " + this.characterSet );
            this.getLog().debug( "Character Set: " + this.characterSet );
        }
        if ( this.quiet )
        {
            naturalDocsCommand.append( " -q" );
            this.getLog().debug( "Quiet" );
        }

        // This is what we'll run.
        // TODO: Surround is a getLog().isDebugEnabled() check.
        this.getLog().debug( "Natural Docs command: " + naturalDocsCommand );

        return naturalDocsCommand.toString();
    }

    /**
     * Copy the project directory before we run natural docs. Often a user will have the project configuration committed
     * in a revision control system and Natual Docs modifies these files. So we'll copy the project directory elsewhere
     * before Natural Docs uses it.
     * 
     * @throws MojoExecutionException There was a problem copying the project directory.
     */
    private void copyProjectDirectory()
        throws MojoExecutionException
    {
        String tmpProjectPath = System.getProperty( "java.io.tmpdir" ) + "NaturalDocsProject";
        this.getLog().debug( "Temporary Project Path: " + tmpProjectPath );

        // Prepare the directory.
        File tmpProjectDirectory = new File( tmpProjectPath );
        if ( tmpProjectDirectory.exists() )
        {
            tmpProjectDirectory.delete();
        }
        tmpProjectDirectory.mkdir();

        try
        {
            FileUtils.copyDirectory( this.project, tmpProjectDirectory );
        }
        catch ( IOException e )
        {
            this.getLog().error( e );
            throw new MojoExecutionException( "There was a problem copying the project directory to java.io.tmpdir.", e );
        }

        // Make the project directory the newly copied one.
        this.project = tmpProjectDirectory;
    }

    /**
     * Generate documentation by running Natural Docs.
     * 
     * @param naturalDocsCommand The command to run natural docs.
     * @throws MojoExecutionException There was a problem running the Natural Docs process.
     */
    private void generateNaturalDocs( String naturalDocsCommand )
        throws MojoExecutionException
    {
        // If the output directory doesn't exist, we need to make it.
        if ( !this.output.exists() )
        {
            this.output.mkdir();
        }

        try
        {
            // We need to run Natural Docs from the Natural Docs home directory to avoid 'Can't open perl script
            // "NaturalDocs": No such file or directory' type messages.
            Process p = Runtime.getRuntime().exec( naturalDocsCommand.toString(), null, naturalDocsHome );

            // Let's give a little extra info when --debug is provided.
            if ( this.getLog().isDebugEnabled() )
            {
                String s = null;

                this.getLog().debug( "Standard Input:" );
                BufferedReader stdInput = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                while ( ( s = stdInput.readLine() ) != null )
                {
                    this.getLog().debug( s );
                }

                this.getLog().debug( "Standard Output:" );
                BufferedReader stdError = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
                while ( ( s = stdError.readLine() ) != null )
                {
                    this.getLog().debug( s );
                }
            }
        }
        catch ( IOException e )
        {
            this.getLog().error( e );
            throw new MojoExecutionException( "An IO Exception occurred while executing Natural Docs.", e );
        }
    }
}
