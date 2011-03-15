package org.codehaus.mojo.jlint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.logging.Log;

public class JlintViolationHandler
{

    private ArrayList<JlintMessageItem> configuration;

    private String currentClass;

    private Boolean firstBug;

    private Log logger;

    private String targetDir;

    private String classesDir;

    public JlintViolationHandler( Log logger )
    {
        firstBug = Boolean.TRUE;
        this.logger = logger;
        loadJlintConfigFile();
    }

    public void setTargetDir( String dir )
    {
        this.targetDir = dir;
    }

    public void setClassesDir( String dir )
    {
        this.classesDir = dir;
    }

    /**
     * On windows, the output produced by Jlint does not include the full pathname. E.g. this is the output of Jlint on
     * windows when the following command is run c:\> jlint <project dir>\target\classes
     * org/jgroups/demos/TotalTokenDemo.java:175: Comparison always produces the same result.
     * org/jgroups/demos/TotalTokenDemo.java:192: Comparison always produces the same result. Jlint does not output the
     * full path of the java source file. It only outputs the package structure - which is what is expected. On
     * Linux/Ubuntu 9.04, Jlint outputs the full path name. E.g.
     * target/classes/com/symcor/bs/common/util/jmx/JMXUtils.java:92: Value of referenced variable 'servers' may be
     * NULL. This leading directory path information has to be stripped out, if it exists.
     */

    public String getErrorClass( String message )
    {
        /*
         * In some cases jlint appends the path upto the "target" directory and then the package structure - leaving out
         * the "classes" directory In other cases jlint appends the full "target" dirpath + /classes to the package
         * structure Hence remove this path prefix in 2 steps if it exists
         */

        // Step 1 - Check for the path upto the "target" directory
        // Step 1a - change the value in TargetDir to Unix Path naming. Since this issue does not appear on
        // windows, it will not affect jlint output generated on Windows.

        logger.debug( "jlintViolationHandler: getErrorClass(): message = [" + message + "]" );

        String unixTargetDir = FilenameUtils.separatorsToUnix( targetDir );

        logger.debug( "jlintViolationHandler: getErrorClass(): unixTargetDir = [" + unixTargetDir + "]" );

        if ( unixTargetDir != null )
        {
            logger.debug( "JlintViolationHandler: TargetDir: " + unixTargetDir );
            message = message.replaceFirst( unixTargetDir, "" );
            logger.debug( "JlintViolationHandler: Message: " + message );
        }

        // Step 2 - Check if the remaining string starts with "/classes"
        if ( classesDir != null && message.startsWith( classesDir ) )
        {
            logger.debug( "JlintViolationHandler: ClassesDir: " + classesDir );
            message = message.replaceFirst( classesDir, "" );
        }

        // if there is a leading "/" - remove it.
        message = message.replaceFirst( "^/", "" );

        // By now we shoud be left with just the package structure
        return message.replaceAll( ".java", "" ).replace( "/", "." );
    }

    public ArrayList<JlintMessageItem> getDefaultMessageList()
    {
        return configuration;
    }

    private void loadJlintConfigFile()
    {
        String configLine;
        String[] configFields;
        JlintMessageItem configItem;

        // DEBUG STUFF
        URL c = this.getClass().getResource( "/config/Jlint_msg.cfg" );
        logger.debug( "CFG FILE: " + c );
        // end DEBUG

        configuration = new ArrayList<JlintMessageItem>();

        try
        {
            BufferedReader configFileReader =
                new BufferedReader(
                                    new InputStreamReader(
                                                           this.getClass().getResourceAsStream(
                                                                                                Constants.JLINT_CFG_FILENAME ) ) );

            while ( ( configLine = configFileReader.readLine() ) != null )
            {
                // DEBUG
                // logger.debug("CONFIGLINE: " + configLine);

                configFields = configLine.split( Constants.FIELD_SEPARATOR, 5 );

                // DEBUG
                // logger.debug("CONFIGFILEDS: " + configFields[0] +
                // " / " + configFields[1] +
                // " / " + configFields[2]);

                configItem = new JlintMessageItem( configFields );

                // DEBUG
                // logger.debug("CREATED: " + configItem);

                configuration.add( configItem );
            }
        }
        catch ( IOException ioe )
        {
            logger.error( "ERROR: " + ioe.toString() );
            ioe.printStackTrace();
        }

    }

    public String violationToXML( String violation )
    {

        String error_class;
        String error_message;
        String lineNo;
        StringBuilder xmlViolation;

        logger.debug( "jlintViolationHandler: violatioToXML(): violation [" + violation + "]" );

        String[] message = violation.split( ":", 3 );

        logger.debug( "jlintViolationHandler: violatioToXML(): message[0] = [" + message[0] + "]" );
        logger.debug( "jlintViolationHandler: violatioToXML(): message[1] = [" + message[1] + "]" );
        logger.debug( "jlintViolationHandler: violatioToXML(): message[2] = [" + message[2] + "]" );

        error_class = getErrorClass( message[0] );
        lineNo = new String( message[1] );
        error_message = new String( message[2] );

        JlintMessageItem configItem = getConfigItemForMessage( error_message );

        if ( configItem == null )
        {
            logger.error( "FATAL ERROR: Cannot find matching message from config file." );
            logger.error( "Message: " + error_message );
            System.exit( -1 );
        }

        final String newLine = System.getProperty( "line.separator" );

        xmlViolation = new StringBuilder();

        if ( !error_class.equals( currentClass ) )
        {
            if ( firstBug == Boolean.FALSE )
            {
                xmlViolation.append( Constants.FILE_END_TAG );
                xmlViolation.append( newLine );
            }
            else
            {
                xmlViolation.append( newLine );
                firstBug = Boolean.FALSE;
            }
            xmlViolation.append( newLine );
            xmlViolation.append( Constants.FILE_START_TAG );
            xmlViolation.append( "\"" );
            xmlViolation.append( error_class );
            xmlViolation.append( "\" >" );
            xmlViolation.append( newLine );
            currentClass = error_class;
        }

        xmlViolation.append( Constants.BUG_START_TAG );

        xmlViolation.append( Constants.BUGATTR_TYPE );
        xmlViolation.append( "\"" );
        xmlViolation.append( configItem.getType() );
        xmlViolation.append( "\"" );
        xmlViolation.append( Constants.BUGATTR_PRIORITY );
        xmlViolation.append( "\"" );
        xmlViolation.append( configItem.getPriority() );
        xmlViolation.append( "\"" );
        xmlViolation.append( Constants.BUGATTR_CATEGORY );
        xmlViolation.append( "\"" );
        xmlViolation.append( configItem.getCategory() );
        xmlViolation.append( "\"" );
        xmlViolation.append( Constants.BUGATTR_MESSAGE );
        xmlViolation.append( "\"" );
        xmlViolation.append( convertToValidXML( error_message ) );
        xmlViolation.append( "\"" );
        xmlViolation.append( Constants.BUGATTR_LINENO );
        xmlViolation.append( "\"" );
        xmlViolation.append( lineNo );
        xmlViolation.append( "\"" );
        xmlViolation.append( Constants.BUG_END_TAG );
        xmlViolation.append( newLine );

        return xmlViolation.toString();

    }

    private JlintMessageItem getConfigItemForMessage( String message )
    {
        String tmp;

        logger.debug( "getConfigItemForMessage: Parameter (message): [" + message + "]" );

        for ( JlintMessageItem ci : configuration )
        {
            // DEBUG
            logger.debug( "" );
            logger.debug( "PATTERN:  [" + ci.getUniqueMessagePattern() + "]" );

            // The replace function is called to remove leading space in the string
            tmp = formatMessage( message );

            // DEBUG
            logger.debug( "MESSAGE:  [" + tmp + "]" );

            if ( tmp.matches( ci.getUniqueMessagePattern() ) )
            {
                logger.debug( "******** MATCH FOUND ***********" );
                logger.debug( ci.toString() );

                return new JlintMessageItem( ci );
            }
        }

        // This will raise an error in the calling function
        return null;
    }

    private String formatMessage( String message )
    {
        return message.replaceFirst( " ", "" );

    }

    private String convertToValidXML( String message )
    {
        // Replace the 5 illegal XML characters with valid
        // XML representation and remove the leading space
        return message.replace( "&", "&amp;" ).replace( "\"", "&quot;" ).replace( "<", "&lt;" ).replace( ">", "&gt;" ).replace(
                                                                                                                                "'",
                                                                                                                                "&apos;" ).replaceFirst(
                                                                                                                                                         " ",
                                                                                                                                                         "" );
    }

}
