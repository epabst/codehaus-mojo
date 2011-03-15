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
import java.util.List;

/**
 * Compiles rmi stubs and skeleton classes from a remote implementation class.
 * By default runs against files in the test-classes directory.
 * 
 * @goal test-rmic
 * @phase process-test-classes
 * @requiresDependencyResolution test
 * @author pgier
 * @version $Id$
 */
public class TestRmicMojo
    extends AbstractRmiMojo
{
    /**
     * Specifies where to place rmic generated class files. If the generated files 
     * need to be included in the project test artifact, this parameter can be set 
     * to ${project.build.testOutputDirectory}.
     * 
     * @parameter default-value="${project.build.directory}/rmi-test-classes"
     * @since 1.0
     */
    private File testOutputDirectory;

    /**
     * Directory tree where the compiled Remote classes are located.
     * 
     * @parameter default-value="${project.build.testOutputDirectory}"
     * @since 1.0
     */
    private File testClassesDirectory;

    /**
     * Compile classpath of the maven project.
     * 
     * @parameter expression="${project.testClasspathElements}"
     * @readonly
     */
    protected List projectTestClasspathElements;

    /**
     * Get the directory where rmic generated class files are written.
     * 
     * @return the directory
     */
    public File getOutputDirectory()
    {
        return testOutputDirectory;
    }

    /**
     * Get the directory where the project classes are located.
     * 
     * @return The project classes directory.
     */
    public File getClassesDirectory()
    {
        return testClassesDirectory;
    }

    /**
     * Get the list of classpath elements for the project.
     * 
     * @return A list containing the project classpath elements.
     */
    public List getProjectClasspathElements()
    {
        return projectTestClasspathElements;
    }

}
