package org.codehaus.mojo.apt.it;

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

/**
 * Runs the integration tests against the apt:test-process goal.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class TestProcessMojoTest extends AbstractAptMojoTest
{
    // AbstractAptMojoTest methods --------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getGoal()
    {
        return "apt:test-process";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSourcePath()
    {
        return "src/test/java/";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTargetPath()
    {
        return "target/generated-test-resources/apt/";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTargetSourcePath()
    {
        return "target/generated-test-sources/apt/";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getClassPrefix()
    {
        return "Test";
    }
}
