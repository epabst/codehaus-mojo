package org.codehaus.mojo.cis.core;


/**
 * A data bean for specifying cis config settings.
 */
public class CisConfig
{
    private Boolean startMonitoringThread;
    private Boolean requestClientHost;
    private Boolean debugMode;
    private String multiLanguageManager;
    private String onlineHelpManager;
    private Integer sessionTimeout;
    private Boolean useOwnClassLoader;
    private Boolean browserPopupOnError;
    private Boolean logToScreen;
    private Boolean zipContent;
    private String textEncoding;

    /**
     * Returns, whether to start a monitoring thread. This option should be true on for nearly
     * all scenarios!
     */
    public Boolean getStartMonitoringThread()
    {
        return startMonitoringThread;
    }

    /**
     * Sets, whether to start a monitoring thread. This option should be true on for nearly
     * all scenarios!
     */
    public void setStartMonitoringThread( Boolean pStartMonitoringThread )
    {
        startMonitoringThread = pStartMonitoringThread;
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
        return requestClientHost;
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
        requestClientHost = pRequestClientHost;
    }

    /**
     * Returns, whether debug information is written to the file log. Should
     * be switched off for normal operations - and only switched to
     * true when you want to analyse some error situation.
     */
    public Boolean getDebugMode()
    {
        return debugMode;
    }

    /**
     * Sets, whether debug information is written to the file log. Should
     * be switched off for normal operations - and only switched to
     * true when you want to analyse some error situation.
     */
    public void setDebugMode( Boolean pDebugMode )
    {
        debugMode = pDebugMode;
    }

    /**
     * Returns the name of a class implementing the IMLManager interface.
     * You can specify an own class here. The MLManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public String getMultiLanguageManager()
    {
        return multiLanguageManager;
    }

    /**
     * Sets the name of a class implementing the IMLManager interface.
     * You can specify an own class here. The MLManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public void setMultiLanguageManager( String pMultiLanguageManager )
    {
        multiLanguageManager = pMultiLanguageManager;
    }

    /**
     * Returns the name of class implementing the IOHManager interface.
     * You can specify an own class here. The OHManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public String getOnlineHelpManager()
    {
        return onlineHelpManager;
    }

    /**
     * Sets the name of class implementing the IOHManager interface.
     * You can specify an own class here. The OHManagerFactory
     * creates an instance using a constructor without any parameter.
     */
    public void setOnlineHelpManager( String pOnlineHelpManager )
    {
        onlineHelpManager = pOnlineHelpManager;
    }

    /**
     * Returns the time in seconds after which a session is cleaned up automatically.
     * The default is 7200 (= 2 hours) - this default is used if no
     * parameter is specified in this configuration file. 
     */
    public Integer getSessionTimeout()
    {
        return sessionTimeout;
    }

    /**
     * Sets the time in seconds after which a session is cleaned up automatically.
     * The default is 7200 (= 2 hours) - this default is used if no
     * parameter is specified in this configuration file. 
     */
    public void setSessionTimeout( Integer pSessionTimeout )
    {
        sessionTimeout = pSessionTimeout;
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
        return useOwnClassLoader;
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
        useOwnClassLoader = pUseOwnClassLoader;
    }

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
        return browserPopupOnError;
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
        browserPopupOnError = pBrowserPopupOnError;
    }

    /**
     * Returns, whether the log is copied to System.out - otherwise
     * the log is only output into the corresponding log file. Should only be
     * "true" for development puposes. Default is "false".
     */
    public Boolean getLogToScreen()
    {
        return logToScreen;
    }

    /**
     * Sets, whether the log is copied to System.out - otherwise
     * the log is only output into the corresponding log file. Should only be
     * "true" for development puposes. Default is "false".
     */
    public void setLogToScreen( Boolean pLogtoscreen )
    {
        logToScreen = pLogtoscreen;
    }

    /**
     * Returns, whether the adapters HTTP response is being compressed or not.
     * Default is "true".
     */
    public Boolean getZipContent()
    {
        return zipContent;
    }

    /**
     * Sets, whether the adapters HTTP response is being compressed or not.
     * Default is "true".
     */
    public void setZipContent( Boolean pZipContent )
    {
        zipContent = pZipContent;
    }

    /**
     * Returns the encoding that is used for writing layout definitions and multi language
     * files. 
     */
    public String getTextEncoding()
    {
        return textEncoding;
    }

    /**
     * Sets the encoding that is used for writing layout definitions and multi language
     * files. 
     */
    public void setTextEncoding( String pTextEncoding )
    {
        textEncoding = pTextEncoding;
    }
}
