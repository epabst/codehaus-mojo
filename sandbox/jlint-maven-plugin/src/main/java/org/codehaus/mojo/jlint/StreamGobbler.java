package org.codehaus.mojo.jlint;

import java.util.*;
import java.io.*;

import org.codehaus.mojo.jlint.Constants;

public class StreamGobbler
    extends Thread
{
    public final static int INITIALIZED = 1;

    public final static int STARTED = 2;

    public final static int COMPLETED = 3;

    InputStream is;

    String type;

    OutputStream os;

    OutputStream os_txt;

    int noOfLines;

    int processing_state;

    JlintViolationHandler violationHandler;

    ArrayList<String> errorMsgList;

    StreamGobbler( InputStream is, String type )
    {
        this( is, null, type );
    }

    StreamGobbler( InputStream is, OutputStream redirect )
    {
        this( is, redirect, null );
    }

    StreamGobbler( InputStream is, OutputStream redirect, String type )
    {
        this.is = is;
        this.type = type;
        this.os = redirect;
        this.os_txt = null;

        noOfLines = 0;
        violationHandler = null;
        processing_state = INITIALIZED;
        errorMsgList = new ArrayList<String>();
    }

    StreamGobbler( InputStream is, OutputStream xmlOutput, OutputStream txtOutput, String type )
    {
        this( is, xmlOutput, type );
        this.os_txt = txtOutput;
    }

    public void run()
    {
        processing_state = STARTED;
        try
        {
            PrintWriter pw = null;
            PrintWriter pw_txt = null;

            if ( os != null )
                pw = new PrintWriter( os );

            if ( os_txt != null )
                pw_txt = new PrintWriter( os_txt );

            InputStreamReader isr = new InputStreamReader( is );
            BufferedReader br = new BufferedReader( isr );
            String line = null;

            /* if the type is INPUT print the Header */
            if ( pw != null && "INPUT".equals( type ) )
            {
                // System.out.println("Printing Header");
                pw.println( Constants.XML_HEADER );
                pw.println( " " );
                pw.println( Constants.ROOT_START_TAG );
            }

            while ( ( line = br.readLine() ) != null )
            {
                if ( pw_txt != null )
                {
                    pw_txt.println( line );
                }

                if ( pw != null )
                {
                    if ( violationHandler != null && "INPUT".equals( type ) )
                    {
                        // System.out.println("Printing Violations in XML");
                        pw.println( violationHandler.violationToXML( line ) );
                    }
                    else
                    {
                        pw.println( line );
                    }
                }

                /*
                 * When processing the error stream ignore errors of XX isn't correct Java class file log these error to
                 * the error file, but do not show on screen. other errors show on screen
                 */
                if ( pw != null && "ERROR".equals( type ) )
                {
                    if ( line.indexOf( "isn't correct Java class file" ) < 0 )
                    {
                        errorMsgList.add( line );
                    }
                }
                noOfLines++;
            }

            /*
             * If any violation has been written, close the last <file> tag
             */
            if ( pw != null && "INPUT".equals( type ) )
            {
                if ( noOfLines > 0 )
                {
                    pw.println( Constants.FILE_END_TAG );
                    pw.println( "" );
                }

                // end the INPUT file with a closing XML tag
                pw.println( Constants.ROOT_END_TAG );
                pw.println( "" );
                pw.flush();
            }

            if ( pw != null )
                pw.close();

            if ( pw_txt != null )
                pw_txt.close();

        }
        catch ( IOException ioe )
        {
            ioe.printStackTrace();
        }

        processing_state = COMPLETED;
    }

    public int getNofLines()
    {
        return noOfLines;
    }

    public ArrayList<String> getErrorMsgList()
    {
        return errorMsgList;
    }

    public void setViolationHandler( JlintViolationHandler violationHandler )
    {
        this.violationHandler = violationHandler;
    }
}
