package org.codehaus.mojo.pomtools.console.toolkit.widgets;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.mojo.pomtools.console.toolkit.ConsoleUtils;
import org.codehaus.mojo.pomtools.console.toolkit.terminal.Terminal;
import org.codehaus.mojo.pomtools.helpers.LocalStringUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class TableLayout
{
    private static final String NEWLINE = "\n";

    private static final String RPAD = " ";
    
    private TableColumn[] columns;

    private int[] maxLengths;
    
    private final Terminal term;
    
    private int tableWidth = -1;

    private List rowList = new ArrayList();
    
    private static final String ELLIPSES = "...";

    public TableLayout( Terminal term )
    {
       this.term = term;
    }

    public TableLayout( Terminal term, int width )
    {
        this.term = term;
        
        this.tableWidth = width;
    }
    
    public TableLayout( Terminal term, TableColumn[] columns )
    {
        this.term = term;
        this.columns = columns;
        this.maxLengths = new int[columns.length];
    }
    
    public TableLayout( Terminal term, TableColumn[] columns, int width )
    {
        this.term = term;
        this.columns = columns;
        this.tableWidth = width;
        this.maxLengths = new int[columns.length];
        
        int sumColumnWidths = 0;
        
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( columns[i].getFixedWidth() >= 0 )
            {
                sumColumnWidths += columns[i].getFixedWidth();
            }
            else
            {
                sumColumnWidths++; // minimum of 1 char for the column
            }
        }
        
        if ( width > 0 && sumColumnWidths > width )
        {
            throw new IllegalArgumentException( "You may not specify a width that is less than the "
                                                + "sum of the column widths" );
        }
        else if ( width > 0 && columns.length > width )
        {
            throw new IllegalArgumentException( "You may not specify a width that is less than the "
                    + "number of columns" );
        }
    }

    public void addEmptyRow()
    {
        if ( columns == null )
        {
            throw new IllegalArgumentException( "Columns must be initialized before adding an empty row" );
        }
        
        this.add( new String[columns.length] );
    }
    
    public void add( String[] row )
    {
        if ( columns == null )
        { 
            createDefaultColumns( row.length );
        }
        else if ( row.length != columns.length )
        {
            throw new IllegalArgumentException( "Row data must have same number of columns as construtor" );
        }

        for ( int nColumn = 0; nColumn < row.length; nColumn++ )
        {
            if ( row[nColumn] != null )
            {
                String[] cellLines = StringUtils.split( row[nColumn], NEWLINE );
                
                for ( int nLine = 0; nLine < cellLines.length; nLine++ )
                {
                    maxLengths[nColumn] = Math.max( maxLengths[nColumn], term.length( cellLines[nLine] ) );
                }
            }
        }
        
        rowList.add( row );
    }
    
    /** Simple helper function that adds n columns of data.
     * Easier than passing new String[] { cell1 .. celln }  
     */
    public void add( String cell1 )
    {
        add( new String[] { cell1 } );
    }

    /** Simple helper function that adds n columns of data.
     * Easier than passing new String[] { cell1 .. celln }  
     */
    public void add( String cell1, String cell2 )
    {
        add( new String[] { cell1, cell2 } );
    }
    
    /** Simple helper function that adds n columns of data.
     * Easier than passing new String[] { cell1 .. celln }  
     */
    public void add( String cell1, String cell2, String cell3 )
    {
        add( new String[] { cell1, cell2, cell3 } );
    }
    
    private void createDefaultColumns( int nCols )
    {
        columns = new TableColumn[nCols];
        maxLengths = new int[nCols];
        for ( int i = 0; i < columns.length; i++ )
        {
            columns[i] = new TableColumn();
        }
    }
    
    private int sum( int[] iarr )
    {
        int result = 0;
        for ( int i = 0; i < iarr.length; i++ )
        {
            result += iarr[i];
        }
        
        return result;
    }

    private int[] calculateColumnWidths() 
    {
        int ncols = columns.length;
        
        int[] widths = new int[ncols];
        int curWidth = 0;
        
        for ( int i = 0; i < ncols; i++ )
        {
            if ( columns[i].getFixedWidth() > 0 )
            {
                widths[i] = columns[i].getFixedWidth();
            }
            else
            {
                widths[i] = maxLengths[i];
                
                if ( columns[i].getMaxWidth() > 0 )
                {
                    widths[i] = Math.min( widths[i], columns[i].getMaxWidth() );
                }
            }
            
            curWidth += widths[i];
        }
        
        if ( this.tableWidth > 0 && curWidth > this.tableWidth )
        {
            int[] fixedColumns = new int[ncols];
            int[] varColumns = new int[ncols];
                                       
            // we need to reduce the length of columns
            for ( int i = 0; i < ncols; i++ )
            {
                if ( columns[i].getFixedWidth() < 0 )
                {
                    varColumns[i] = widths[i]; // variable sized column                    
                }
                else
                {
                    fixedColumns[i] = widths[i]; // fixed size column
                }
            }
            
            int diff = curWidth - sum( fixedColumns ) - this.tableWidth;
            double sumVarCols = sum( varColumns );
            
            for ( int i = 0; i < ncols; i++ )
            {
                if ( varColumns[i] > 0 )
                {
                    widths[i] = (int) Math.round( (double) varColumns[i] / sumVarCols * diff );
                }
            }
        }
        
        return widths;
    }
    
    protected TableColumn getColumn( int index )
    {
        return columns[index];
    }
    
    public String getOutput()
    {
        StringBuffer sb = new StringBuffer();

        int[] columnWidths = calculateColumnWidths();
        
        for ( Iterator iter = rowList.iterator(); iter.hasNext(); )
        {
            String[] row = (String[]) iter.next();
            
            RowOutput output = new RowOutput( columnWidths );

            for ( int columnIndex = 0; columnIndex < row.length; columnIndex++ )
            {
                TableColumn col = columns[columnIndex];
                
                int columnWidth = columnWidths[columnIndex];
                
                String cellData = StringUtils.defaultString( row[columnIndex] );
                
                if ( !col.isWrap() && term.length( cellData ) > columnWidth )
                {
                    int encodingLen = term.encodingLength( cellData );
                    
                    int lastPos = columnWidth - ELLIPSES.length();
                    if ( lastPos - encodingLen <= 0 )
                    {
                        throw new IllegalArgumentException( "Can't truncate string in a column that is too " 
                                + "short for ellipses" );
                    }
                    
                    // TODO: this could chop in the middle of an escape
                    cellData = cellData.substring( 0, lastPos + encodingLen  ) + ELLIPSES;
                }
                
                if ( term.length( cellData ) > columnWidth )
                {
                    // If any single line of the data is longer than the column width, just wordwrap the whole value
                    String[] cellLines = LocalStringUtils.splitPreserveAllTokens( cellData, NEWLINE );
                    
                    for ( int i = 0; i < cellLines.length; i++ )
                    {
                        if ( term.length( cellLines[i] ) > columnWidth )
                        {
                            // TODO: Make wordWrap understand escape sequences
                            cellData = ConsoleUtils.wordWrap( cellData, columnWidth );
                            break;
                        }
                    }
                                        
                } 
                  
                output.setColumnData( columnIndex, cellData );
            }
            
            sb.append( output.getOutput() );
        }

        return sb.toString();
    }
    

    protected int getWidthBeforeColumn( int colIndex ) 
    {
        int width = 0;
        for ( int i = 0; i < colIndex; i++ )
        {
            width += maxLengths[i];            
        }
        
        return width;
    }
    
    private class RowOutput
    {
        private final int[] columnWidths;
        
        private final List[] columnLists;
        
        public RowOutput( int[] columnWidths )
        {
            this.columnWidths = columnWidths;
            
            columnLists = new List[columnWidths.length];
            
            for ( int i = 0; i < columnLists.length; i++ )
            {
                columnLists[i] = new ArrayList();
            }
        }
        
        public RowOutput setColumnData( int colIndex, String s )
        {
            TableColumn column = getColumn( colIndex );
            
            String[] strLines = LocalStringUtils.splitPreserveAllTokens( s, NEWLINE );
            if ( strLines.length == 0 )
            {
                columnLists[colIndex].add( s );
            }
            else
            {
                for ( int i = 0; i < strLines.length; i++ )
                {
                    String line = strLines[i];
                    
                    if ( term.length( line ) > columnWidths[colIndex] )
                    {
                        // This shouldn't happen
                        throw new IllegalArgumentException( "Passed column data that was longer than the max width" );
                    }
                    
                    if ( column.getAlignment() == TableColumn.ALIGN_LEFT )
                    {
                        line = StringUtils.rightPad( line, columnWidths[colIndex] + term.encodingLength( line ) );
                    }
                    else
                    {
                        line = StringUtils.leftPad( line, columnWidths[colIndex] + term.encodingLength( line ) );
                    }
                    
                    columnLists[colIndex].add( line );                
                }
            }
            
            return this;
        }
    
        private String getFormattedData( int columnIndex, String data )
        {
            TableColumn col = getColumn( columnIndex );
            
            if ( col.getTextStyle() != null )
            {
                if ( col.getTextStyle() == TableColumn.BOLD )
                {
                    return term.bold( data );                
                }
                else if ( col.getTextStyle() == TableColumn.UNDERLINE )
                {
                    return term.underline( data );
                }
            }
            
            return data;
        }
        
        public String getOutput() 
        {
            StringBuffer sb = new StringBuffer();
            
            int maxLinesInRow = 0;
            
            for ( int i = 0; i < columnLists.length; i++ )
            {
                maxLinesInRow = Math.max( maxLinesInRow, columnLists[i].size() );
            }
            
            for ( int nLine = 0; nLine < maxLinesInRow; nLine++ )
            {
                for ( int nColumn = 0; nColumn < columnLists.length; nColumn++ )
                {
                    // Does this column have data for this line?
                    if ( columnLists[nColumn].size() > nLine ) 
                    {
                        sb.append( getFormattedData( nColumn, (String) columnLists[nColumn].get( nLine ) ) );
                    } 
                    else
                    {
                        // no data for line so just pad it out so the next column prints in the right place
                        sb.append( StringUtils.repeat( " ", columnWidths[nColumn] ) );
                    }
                    
                    if ( nColumn < columnLists.length - 1 )
                    {
                        sb.append( RPAD );
                    }
                }
                
                sb.append( NEWLINE );
            }
            
            return sb.toString();
        }
    }
}
