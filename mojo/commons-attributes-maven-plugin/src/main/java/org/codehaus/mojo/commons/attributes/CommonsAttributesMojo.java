package org.codehaus.mojo.commons.attributes;

/*
 * Copyright (c) 2004-2006, Codehaus.org
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
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Commons-attributes compiler.
 * 
 * @version $Id$
 * @goal compile
 * @phase generate-sources
 */
public class CommonsAttributesMojo
    extends AbstractCommonsAttributeMojo
{

    /**
     * The directory for generated sources.
     *
     * @parameter default-value="${project.build.directory}/generated-sources"
     */
    private File outputDirectory = new File( projectBuildDirectory, "generated-sources" );

    /**
     * A list of inclusion filters for the compiler.
     *
     * @parameter
     */
    private Set includes = new HashSet();

    /**
     * A list of exclusion filters for the compiler.
     *
     * @parameter
     */
    private Set excludes = new HashSet();

    public CommonsAttributesMojo()
    {
    }

    public void execute() throws MojoExecutionException
    {
        String sourcePath = (String) project.getCompileSourceRoots().get( 0 );
        execute( sourcePath, outputDirectory, includes, excludes );
        project.addCompileSourceRoot( outputDirectory.getAbsolutePath() );
    }

}
