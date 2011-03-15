package org.codehaus.mojo.syslog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.codehaus.plexus.util.StringUtils;
import org.productivity.java.syslog4j.impl.message.processor.structured.StructuredSyslogMessageProcessor;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class RedirectSyslogMessageStructureProcessor
    extends StructuredSyslogMessageProcessor {

    private static final long serialVersionUID = 1L;
    
    private String fromHost;

    public void setFromHost( String fromHost ) {
        this.fromHost = fromHost;
    }

    public String createSyslogHeader( final int facility, final int level, final boolean sendLocalTimestamp,
                                      final boolean sendLocalName ) {
        
        final StringBuffer buffer = new StringBuffer();

        final int priority = ( facility | level );

        buffer.append( "<" );
        buffer.append( priority );
        buffer.append( ">" );
        buffer.append( VERSION );
        buffer.append( ' ' );

        final SimpleDateFormat dateFormat = new SimpleDateFormat( STRUCTURED_DATA_MESSAGE_DATEFORMAT, Locale.ENGLISH );

        // ISO standard requires a colon in the timezone
        final String datePrefix = dateFormat.format( new Date() );
        buffer.append( datePrefix.substring( 0, 22 ) );
        buffer.append( ':' );
        buffer.append( datePrefix.substring( 22 ) );
        buffer.append( ' ' );

        final String localName = StringUtils.isBlank( fromHost )?SyslogUtility.getLocalName():fromHost;

        buffer.append( localName );
        buffer.append( ' ' );

        buffer.append( StructuredSyslogMessage.nilProtect( this.getApplicationName() ) ).append( ' ' );

        buffer.append( StructuredSyslogMessage.nilProtect( this.getProcessId() ) ).append( ' ' );

        return buffer.toString();

    }

}
