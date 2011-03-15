package org.apache.maven.diagrams.connector_api.logger;

import org.apache.maven.embedder.MavenEmbedderLogger;

/**
 * The adapter from ConnectorLogger to MavenEmbaddedLogger 
 * 
 * @author Piotr Tabor
 */
public class ConnectorLoggerToMavenEmbadderLoggerAdapter implements MavenEmbedderLogger
{
    private Logger logger;

    public ConnectorLoggerToMavenEmbadderLoggerAdapter( Logger logger )
    {
        this.logger = logger;
    }

    public void debug( String arg0 )
    {
        logger.debug( arg0 );
    }

    public void debug( String arg0, Throwable arg1 )
    {
        logger.debug( arg0, arg1 );
    }

    public void error( String arg0 )
    {
        logger.error( arg0 );

    }

    public void error( String arg0, Throwable arg1 )
    {
        logger.error( arg0, arg1 );

    }

    public void fatalError( String arg0 )
    {
        logger.error( arg0 );
    }

    public void fatalError( String arg0, Throwable arg1 )
    {
        logger.error( arg0, arg1 );

    }

    public int getThreshold()
    {
        if ( logger.isDebugEnabled() )
            return MavenEmbedderLogger.LEVEL_DEBUG;
        if ( logger.isInfoEnabled() )
            return MavenEmbedderLogger.LEVEL_INFO;
        if ( logger.isWarnEnabled() )
            return MavenEmbedderLogger.LEVEL_WARN;
        if ( logger.isErrorEnabled() )
            return MavenEmbedderLogger.LEVEL_ERROR;

        return MavenEmbedderLogger.LEVEL_DISABLED;
    }

    public void info( String arg0 )
    {
        logger.info( arg0 );
    }

    public void info( String arg0, Throwable arg1 )
    {
        logger.info( arg0, arg1 );

    }

    public boolean isDebugEnabled()
    {

        return logger.isDebugEnabled();
    }

    public boolean isErrorEnabled()
    {
        return logger.isErrorEnabled();
    }

    public boolean isFatalErrorEnabled()
    {
        return logger.isErrorEnabled();
    }

    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    public boolean isWarnEnabled()
    {
        return logger.isWarnEnabled();
    }

    public void setThreshold( int arg0 )
    {
    }

    public void warn( String arg0 )
    {
        logger.warn( arg0 );
    }

    public void warn( String arg0, Throwable arg1 )
    {
        logger.warn( arg0, arg1 );
    }

}
