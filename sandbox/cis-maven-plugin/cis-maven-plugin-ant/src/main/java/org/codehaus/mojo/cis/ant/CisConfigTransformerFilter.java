package org.codehaus.mojo.cis.ant;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.codehaus.mojo.cis.core.CisConfig;
import org.codehaus.mojo.cis.core.CisConfigTransformer;
import org.codehaus.mojo.cis.core.CisCoreException;
import org.xml.sax.InputSource;


/**
 * An Ant task for editing the {@code cisconfig.xml} file.
 */
public class CisConfigTransformerFilter extends AbstractCisFilter
{
    private final CisConfig cisConfig = new CisConfig();

    /**
     * Returns, how the CIS should handle errors thrown by a CIS page:
     * (1 = false = default): the browser switches to an error screen; in the
     * screen the user can only abort the current function. This is the default
     * way because any kind of inconsistencies are automatically ommitted.
     * (2 = true): the browser opens a popup window, in which the error is output.
     * This way should only be used during development because it might cause
     * inconsistencies in the application.
     */
    public Boolean getBrowserPopupOnError()
    {
        return cisConfig.getBrowserPopupOnError();
    }

    /**
     * Sets, how the CIS should handle errors thrown by a CIS page:
     * (1 = false = default): the browser switches to an error screen; in the
     * screen the user can only abort the current function. This is the default
     * way because any kind of inconsistencies are automatically ommitted.
     * (2 = true): the browser opens a popup window, in which the error is output.
     * This way should only be used during development because it might cause
     * inconsistencies in the application.
     */
    public void setBrowserPopupOnError( Boolean pBrowserPopupOnError )
    {
        cisConfig.setBrowserPopupOnError( pBrowserPopupOnError );
    }

    /**
     * Returns, whether debug information is written to the file log. Should
     * be switched off for normal operations - and only switched to
     * true when you want to analyse some error situation.
     */
    public Boolean getDebugMode()
    {
        return cisConfig.getDebugMode();
    }

    /**
     * Sets, whether debug information is written to the file log. Should
     * be switched off for normal operations - and only switched to
     * true when you want to analyse some error situation.
     */
    public void setDebugMode( Boolean pDebugMode )
    {
        cisConfig.setDebugMode( pDebugMode );
    }

    /**
     * Returns, whether the log is copied to System.out - otherwise
     * the log is only output into the corresponding log file. Should only be
     * "true" for development puposes. Default is "false".
     */
    public Boolean getLogToScreen()
    {
        return cisConfig.getLogToScreen();
    }

    /**
     * Sets, whether the log is copied to System.out - otherwise
     * the log is only output into the corresponding log file. Should only be
     * "true" for development puposes. Default is "false".
     */
    public void setLogToScreen( Boolean pLogtoscreen )
    {
        cisConfig.setLogToScreen( pLogtoscreen );
    }

    /**
     * Returns the name of a class implementing the IMLManager interface.
     * You can specify an own class here. The MLManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public String getMultiLanguageManager()
    {
        return cisConfig.getMultiLanguageManager();
    }

    /**
     * Sets the name of a class implementing the IMLManager interface.
     * You can specify an own class here. The MLManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public void setMultiLanguageManager( String pMultiLanguageManager )
    {
        cisConfig.setMultiLanguageManager( pMultiLanguageManager );
    }

    /**
     * Returns the name of class implementing the IOHManager interface.
     * You can specify an own class here. The OHManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public String getOnlineHelpManager()
    {
        return cisConfig.getOnlineHelpManager();
    }

    /**
     * Sets the name of class implementing the IOHManager interface.
     * You can specify an own class here. The OHManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public void setOnlineHelpManager( String pOnlineHelpManager )
    {
        cisConfig.setOnlineHelpManager( pOnlineHelpManager );
    }

    /**
     * Returns, whether to determine detailed information about the client,
     * if it is sending its first HTTP request. This
     * operation sometimes is quite expensive - so there is the option
     * to switch it off. There is no disadvantag in normal operation if
     * switched off, besides in the monitoring you cannot identify which
     * session belongs to which client.
     */
    public Boolean getRequestClientHost()
    {
        return cisConfig.getRequestClientHost();
    }

    /**
     * Sets, whether to determine detailed information about the client,
     * if it is sending its first HTTP request. This
     * operation sometimes is quite expensive - so there is the option
     * to switch it off. There is no disadvantag in normal operation if
     * switched off, besides in the monitoring you cannot identify which
     * session belongs to which client.
     */
    public void setRequestClientHost( Boolean pRequestClientHost )
    {
        cisConfig.setRequestClientHost( pRequestClientHost );
    }

    /**
     * Returns the time in seconds after which a session is cleaned up automatically.
     * The default is 7200 (= 2 hours) - this default is used if no
     * parameter is specified in this configuration file. 
     */
    public Integer getSessionTimeout()
    {
        return cisConfig.getSessionTimeout();
    }

    /**
     * Sets the time in seconds after which a session is cleaned up automatically.
     * The default is 7200 (= 2 hours) - this default is used if no
     * parameter is specified in this configuration file. 
     */
    public void setSessionTimeout( Integer pSessionTimeout )
    {
        cisConfig.setSessionTimeout( pSessionTimeout );
    }

    /**
     * Returns, whether to start a monitoring thread. This option should be true on for nearly
     * all scenarios!
     */
    public Boolean getStartMonitoringThread()
    {
        return cisConfig.getStartMonitoringThread();
    }

    /**
     * Sets, whether to start a monitoring thread. This option should be true on for nearly
     * all scenarios!
     */
    public void setStartMonitoringThread( Boolean pStartMonitoringThread )
    {
        cisConfig.setStartMonitoringThread( pStartMonitoringThread );
    }

    /**
     * Returns the encoding that is used for writing layout definitions and multi language
     * files. 
     */
    public String getTextEncoding()
    {
        return cisConfig.getTextEncoding();
    }

    /**
     * Sets the encoding that is used for writing layout definitions and multi language
     * files. 
     */
    public void setTextEncoding( String pTextEncoding )
    {
        cisConfig.setTextEncoding( pTextEncoding );
    }

    /**
     * Returns, whether the CIS runtime environment uses its own class loader
     * for loading application classes. The default is "true" - you can switch off
     * the class loader in special runtime scenarios in which you want the
     * class loader management to be taken over by an instance around CIS, e.g.
     * the application server - and if at same point of time this instance requires
     * all application classes to run in the instance's class loader management.
     * PLEASE PAY ATTENTION: the CIS class loader automatically searches for
     * classes in certain directories (&lt;project&gt;/appclasses/classes and
     * &lt;project&gt;/appclasses/lib) - in cases you do not used the CIS
     * class loader you have to set up your environment accordingly.
     */
    public Boolean getUseOwnClassLoader()
    {
        return cisConfig.getUseOwnClassLoader();
    }

    /**
     * Sets, whether the CIS runtime environment uses its own class loader
     * for loading application classes. The default is "true" - you can switch off
     * the class loader in special runtime scenarios in which you want the
     * class loader management to be taken over by an instance around CIS, e.g.
     * the application server - and if at same point of time this instance requires
     * all application classes to run in the instance's class loader management.
     * PLEASE PAY ATTENTION: the CIS class loader automatically searches for
     * classes in certain directories (&lt;project&gt;/appclasses/classes and
     * &lt;project&gt;/appclasses/lib) - in cases you do not used the CIS
     * class loader you have to set up your environment accordingly.
     */
    public void setUseOwnClassLoader( Boolean pUseOwnClassLoader )
    {
        cisConfig.setUseOwnClassLoader( pUseOwnClassLoader );
    }

    /**
     * Returns, whether the adapters HTTP response is being compressed or not.
     * Default is "true".
     */
    public Boolean getZipContent()
    {
        return cisConfig.getZipContent();
    }

    /**
     * Sets, whether the adapters HTTP response is being compressed or not.
     * Default is "true".
     */
    public void setZipContent( Boolean pZipContent )
    {
        cisConfig.setZipContent( pZipContent );
    }

    public Reader chain( Reader pReader ) throws BuildException
    {
        final InputSource inputSource = new InputSource( pReader );
        final StringWriter sw = new StringWriter();
        final StreamResult result = new StreamResult( sw );
        CisConfigTransformer cisConfigTransformer = new CisConfigTransformer();
        cisConfigTransformer.setCisConfig( cisConfig );
        try
        {
            cisConfigTransformer.transform( inputSource, result );
        }
        catch ( CisCoreException e )
        {
            throw new BuildException( e.getMessage(), e );
        }
        return new StringReader( sw.toString() );
    }
}
