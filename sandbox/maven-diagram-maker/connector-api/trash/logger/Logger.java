package org.apache.maven.diagrams.connector_api.logger;

/**
 * Internal connector's logging interface
 * 
 * @author Piotr Tabor
 *
 */
public interface Logger
{

    public abstract boolean isDebugEnabled();

    public abstract void debug( java.lang.CharSequence arg0 );

    public abstract void debug( java.lang.CharSequence arg0, java.lang.Throwable arg1 );

    public abstract void debug( java.lang.Throwable arg0 );

    public abstract boolean isInfoEnabled();

    public abstract void info( java.lang.CharSequence arg0 );

    public abstract void info( java.lang.CharSequence arg0, java.lang.Throwable arg1 );

    public abstract void info( java.lang.Throwable arg0 );

    public abstract boolean isWarnEnabled();

    public abstract void warn( java.lang.CharSequence arg0 );

    public abstract void warn( java.lang.CharSequence arg0, java.lang.Throwable arg1 );

    public abstract void warn( java.lang.Throwable arg0 );

    public abstract boolean isErrorEnabled();

    public abstract void error( java.lang.CharSequence arg0 );

    public abstract void error( java.lang.CharSequence arg0, java.lang.Throwable arg1 );

    public abstract void error( java.lang.Throwable arg0 );
}
