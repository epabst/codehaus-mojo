/*
The MIT License

Copyright (c) 2006, The Codehaus http://www.codehaus.org/

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package org.codehaus.mojo.freemarker.configurations;

import java.io.File;

/**
 * 
 * @author jimisola <public@jimisola.com>
 *
 */
public class InputConfigurationEntry
{
    /**
     * @parameter
     * @required
     */
    private File inputFile;
    
    /**
     * @parameter
     * @required
     */
    private String loaderType;

    /**
     * @parameter
     * @required
     */
    private String contextVariable;
    
    public String getContextVariable()
    {
        return this.contextVariable;
    }

    public File getInputFile()
    {
        return this.inputFile;
    }

    public String getLoader()
    {
        return this.loaderType;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(super.toString());
        sb.append(" [");
        sb.append("inputFile =" + this.inputFile.getAbsolutePath());
        sb.append("loader =" + this.loaderType);
        sb.append("contextVariable =" + this.contextVariable);        
        sb.append("] ");
        
        return sb.toString(); 
    }

}