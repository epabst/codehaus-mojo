/*
 * Created on Jan 10, 2006
 *
 */
package org.codehaus.mojo.simian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import au.com.redhillconsulting.simian.AuditListener;
import au.com.redhillconsulting.simian.Options;
import au.com.redhillconsulting.simian.SourceFile;

/**
 * 
 * @author mgriffa
 * @version $Id$
 */
public class SimianAuditListener
    implements AuditListener
{
    private Log log;

    private List records = new ArrayList();

    private List processed = new ArrayList();

    private Map totalsByFilename = new HashMap();

    private long end;

    private long start;

    private int currentBlockSize;

    private int currentBlockId = 1;

    private int totalLines = 0;

    private int blockCount = 0;

    private int totalSouceLines;

    public void startCheck( final Options arg0 )
    {
        log.debug( "startCheck" );
        start = System.currentTimeMillis();
    }

    public void fileProcessed( final SourceFile arg0 )
    {
        log.debug( "file processed " + arg0.getFilename() );
        processed.add( arg0 );
        this.totalSouceLines += arg0.getSignificantLineCount();
    }

    public void startSet( final int arg0 )
    {
        this.currentBlockSize = arg0;
    }

    public void block( final SourceFile sourcefile, final int startLine, final int endLine, final boolean subsumed )
    {
        final Record r = new Record();
        r.setSourcefile( sourcefile );
        r.setStartLine( startLine );
        r.setEndLine( endLine );
        r.setSubsumed( subsumed );
        r.setBlockSize( this.currentBlockSize );
        r.setBlockId( this.currentBlockId );
        records.add( r );
        addBlock( sourcefile.getFilename(), endLine - startLine );
        this.totalLines += ( endLine - startLine );
        this.blockCount++;
    }

    private void addBlock( String filename, int i )
    {
        if ( !totalsByFilename.containsKey( filename ) )
        {
            Integer val = new Integer( i );
            totalsByFilename.put( filename, val );
        }
        else
        {
            Integer val = (Integer) totalsByFilename.get( filename );
            totalsByFilename.put( filename, new Integer( val.intValue() + i ) );
        }
    }

    public void endSet()
    {
        synchronized ( this )
        {
            this.currentBlockId++;
        }
    }

    public void endCheck()
    {
        log.debug( "endCheck" );
        end = System.currentTimeMillis();
    }

    public void setLog( final Log log )
    {
        this.log = log;
    }

    public long getElapsed()
    {
        return ( end - start );
    }

    public Record[] getRecords()
    {
        return (Record[]) records.toArray( new Record[records.size()] );
    }

    public int getTotalLinesForFilename( String filename )
    {
        if ( totalsByFilename.containsKey( filename ) )
        {
            Integer i = (Integer) totalsByFilename.get( filename );
            return i.intValue();
        }
        return 0;
    }

    public int getDuplicateLineCount()
    {
        return this.totalLines;
    }

    public int getBlockCount()
    {
        return this.blockCount;
    }

    public int getFileProcessedCount()
    {
        return processed.size();
    }

    public int getTotalSourceLines()
    {
        return this.totalSouceLines;
    }

    public int getFileWithDuplicateCount()
    {
        return totalsByFilename.size();
    }
}
