package org.codehaus.mojo.ruby.plexus.component.configurator.converters;

import org.codehaus.mojo.ruby.RubyMojo;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class RubyComponentValueSetter
{
    private RubyMojo mojo;

    private String fieldName;

    private ConverterLookup lookup;

    private Class setterParamType;

    private ConfigurationConverter setterTypeConverter;

    private ConfigurationListener listener;

    public RubyComponentValueSetter( String fieldName, Class setterParamType, Object object, ConverterLookup lookup,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        this.fieldName = fieldName;
        this.mojo = (RubyMojo) object;
        this.lookup = lookup;
        this.listener = listener;
        this.setterParamType = setterParamType;

        if ( object == null )
        {
            throw new ComponentConfigurationException( "Component is null" );
        }

        initSetter();
    }

    private void initSetter()
    {
        try
        {
            if( Object.class.equals( setterParamType ) )
            {
                setterTypeConverter = new ObjectWithFieldsConverter();
            }
            else
            {
                setterTypeConverter = lookup.lookupConverterForType( setterParamType );
            }
        }
        catch(ComponentConfigurationException e) { }
    }

    private void setValueUsingSetter( Object value )
        throws ComponentConfigurationException
    {
        if ( setterParamType == null )
        {
            throw new ComponentConfigurationException( "No setter found" );
        }

        if ( listener != null )
        {
            listener.notifyFieldChangeUsingSetter( fieldName, value, mojo );
        }

        try
        {
            mojo.set( fieldName, value );
        }
        catch ( IllegalArgumentException e )
        {
            String exceptionInfo = mojo.getClass().getName() + "." + fieldName + "( "
                + setterParamType.getClass().getName() + " )";

            throw new ComponentConfigurationException( "Invalid parameter supplied while setting '" + value + "' to "
                + exceptionInfo, e );
        }
    }

    public void configure( PlexusConfiguration config, ClassLoader cl, ExpressionEvaluator evaluator )
        throws ComponentConfigurationException
    {
        //System.out.println( fieldName + " converter: " + setterTypeConverter.getClass() );

        Object value = 
            setterTypeConverter.fromConfiguration( lookup, config, setterParamType, mojo.getClass(), cl, evaluator, listener );

        //System.out.println( value );

        if ( value != null )
        {
            setValueUsingSetter( value );
        }
    }
}
