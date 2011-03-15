package org.codehaus.mojo.pomtools.console.toolkit;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ConsoleScreenDisplay
{
    private String contents;
    private String prompt;
    
    private boolean clearScreen;
    
    private boolean redrawPreviousContents;

    public ConsoleScreenDisplay( String contents, String prompt, boolean clearScreen )
    {   
        this.contents = contents;
        this.prompt = prompt;
        this.clearScreen = clearScreen;
    }

    public boolean isClearScreen()
    {
        return clearScreen;
    }

    public String getContents()
    {
        return contents;
    }

    public String getPrompt()
    {
        return prompt;
    }

    public boolean isRedrawPreviousContents()
    {
        return redrawPreviousContents;
    }

    public void setRedrawPreviousContents( boolean redrawPreviousScreen )
    {
        this.redrawPreviousContents = redrawPreviousScreen;
    }

    public void setClearScreen( boolean clearScreen )
    {
        this.clearScreen = clearScreen;
    }

    public void setContents( String contents )
    {
        this.contents = contents;
    }

    public void setPrompt( String prompt )
    {
        this.prompt = prompt;
    }

}
