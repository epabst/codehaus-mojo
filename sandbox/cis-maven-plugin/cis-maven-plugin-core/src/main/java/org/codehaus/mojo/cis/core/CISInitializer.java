package org.codehaus.mojo.cis.core;

import java.io.File;
import java.lang.reflect.Field;



/**
 * A class for initializing the CIS application. By default,
 * this would be done using within
 * {@code com.softwareag.cis.server.Params.init(String,String,javax.servlet.ServletConfig)}.
 * We do not have a servlet environment, therefore we fake this call.
 */
public class CISInitializer
{
    private File cisHomeDir, tempDir, logDir;
    private CisUtils cisUtils;

    /**
     * Returns the temporary directory to use.
     */
    public File getTempDir()
    {
        if ( tempDir == null )
        {
            setTempDir( new File( System.getProperty( "java.io.tmpdir" ) ) );
        }
        return tempDir;
    }

    /**
     * Sets the temporary directory to use.
     */
    public void setTempDir( File pTempDir )
    {
        tempDir = pTempDir;
    }

    /**
     * Returns the log directory to use.
     */
    public File getLogDir()
    {
        if ( logDir == null )
        {
            setLogDir( new File( getCisHomeDir(), "log" ) );
        }
        return logDir;
    }

    /**
     * Sets the log directory to use.
     */
    public void setLogDir( File pLogDir )
    {
        logDir = pLogDir;
    }

    /**
     * Returns the {@link CisUtils} to use.
     */
    public CisUtils getCisUtils()
    {
        return cisUtils;
    }

    /**
     * Sets the {@link CisUtils} to use.
     */
    public void setCisUtils( CisUtils pCisUtils )
    {
        cisUtils = pCisUtils;
    }

    /**
     * Returns the CIS home directory.
     */
    public File getCisHomeDir()
    {
        return cisHomeDir;
    }

    /**
     * Sets the CIS home directory.
     */
    public void setCisHomeDir( File pCisHomeDir )
    {
        cisHomeDir = pCisHomeDir;
    }

    private void setParam( Class pClass, String pParam, Object pValue )
        throws CisCoreException
    {
        try
        {
            Field field = null;
            try
            {
                field = pClass.getDeclaredField( pParam ); 
            }
            catch ( NoSuchFieldException e )
            {
                if ( pParam.startsWith( "_fld" ) )
                {
                    try
                    {
                        field = pClass.getDeclaredField( pParam.substring( "_fld".length() ) );
                    }
                    catch ( NoSuchFieldException e1 )
                    {
                        // Nothing to do, we'll throw an exception later on.
                    }
                }
            }
            if ( field == null )
            {
                throw new CisCoreException( "No such field in class " + pClass.getName()
                                            + ": " + pParam );
            }
            if ( !field.isAccessible() )
            {
                field.setAccessible( true );
            }
            field.set( null, pValue );
        }
        catch ( SecurityException e )
        {
            throw new CisCoreException( "Failed to access field " + pParam
                                        + " of class "
                                        + pClass.getName() + ": " + e.getMessage(),
                                        e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new CisCoreException( "Illegal argument for field " + pParam
                                        + " in class "
                                        + pClass.getName() + ": " + e.getMessage(),
                                        e );
        }
        catch ( IllegalAccessException e )
        {
            throw new CisCoreException( "Illegal access to field " + pParam
                                        + " in class "
                                        + pClass.getName() + ": " + e.getMessage(),
                                        e );
        }
    }

    /**
     * Called to initialize the CIS environment.
     */
    public void init(ClassLoader pClassLoader)
        throws CisCoreException
    {
        final String paramsClassName = "com.softwareag.cis.server.Params";
        final Class paramsClass;
        try
        {
            paramsClass = pClassLoader.loadClass( paramsClassName );
        }
        catch (ClassNotFoundException e)
        {
            throw new CisCoreException("Failed to load class "
                                       + paramsClassName + ": " + e.getMessage(), e );
        }
        setParam( paramsClass, "_flddo", getTempDir().getPath() + "/" );
        setParam( paramsClass, "_fldtry", getCisHomeDir().getPath() + "/" );
        setParam( paramsClass, "_fldint", getLogDir().getPath() + "/" );
    }
}
