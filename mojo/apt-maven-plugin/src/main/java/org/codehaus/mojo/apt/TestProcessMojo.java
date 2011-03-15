package org.codehaus.mojo.apt;

/*
 * The MIT License
 *
 * Copyright 2006-2008 The Codehaus.
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

import org.apache.maven.model.Resource;

/**
 * Executes apt on project test sources.
 * 
 * @author <a href="mailto:jubu@codehaus.org">Juraj Burian</a>
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @goal test-process
 * @phase generate-test-resources
 * @requiresDependencyResolution test
 */
public class TestProcessMojo extends AbstractAptMojo
{
    // read-only parameters ---------------------------------------------------

    /**
     * The source directories containing the test sources to be processed.
     * 
     * @parameter default-value="${project.testCompileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> testCompileSourceRoots;

    /**
     * The project's test resources.
     * 
     * @parameter default-value="${project.testResources}"
     * @required
     * @readonly
     */
    private List<Resource> testResources;
    
    /**
     * The project's test classpath.
     * 
     * @parameter default-value="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> testClasspathElements;

    // configurable parameters ------------------------------------------------

    /**
     * The directory to place processor and generated class files. This is equivalent to the <code>-d</code> argument
     * for apt.
     * 
     * @parameter default-value="${project.build.directory}/generated-test-resources/apt"
     */
    private File testOutputDirectory;

    /**
     * The directory root under which processor-generated test source files will be placed; files are placed in
     * subdirectories based on package namespace. This is equivalent to the <code>-s</code> argument for apt.
     * 
     * @parameter default-value="${project.build.directory}/generated-test-sources/apt"
     */
    private File testSourceOutputDirectory;
    
    // AbstractAptMojo methods ------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getCompileSourceRoots()
    {
        return testCompileSourceRoots;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Resource> getResources()
    {
        return testResources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getClasspathElements()
    {
        return testClasspathElements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected File getOutputDirectory()
    {
        return testOutputDirectory;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected File getSourceOutputDirectory()
    {
        return testSourceOutputDirectory;
    }
}
