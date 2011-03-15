package org.codehaus.mojo.pomtools.console.toolkit.widgets;

import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;

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
public class LabeledList
{
    private final TableLayout tab;

    public LabeledList( Terminal term )
    {
        this( term, false );
    }

    public LabeledList( Terminal term, boolean rightAlignLabel )
    {
        this( term, rightAlignLabel, false );
    }
    
    public LabeledList( Terminal term, boolean rightAlignLabel, boolean boldLabel )
    {
        TableColumn firstColumn = new TableColumn( rightAlignLabel ? TableColumn.ALIGN_RIGHT : TableColumn.ALIGN_LEFT );
        if ( boldLabel )
        {
            firstColumn.setTextStyle( TableColumn.BOLD );
        }
        
        tab = new TableLayout( term, new TableColumn[] { firstColumn, TableColumn.ALIGN_LEFT_COLUMN } );
    }

    public LabeledList add( String label, String content )
    {
        tab.add( new String[] { label, content } );
        return this;
    }
    
    public LabeledList addEmpty()
    {
        tab.addEmptyRow();
        return this;
    }

    public String getOutput()
    {
        return tab.getOutput();
    }

}
