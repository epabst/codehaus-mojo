package org.codehaus.mojo.ruby.plexus.component.configurator.converters.composite;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

import org.codehaus.mojo.ruby.plexus.component.configurator.converters.RubyComponentValueSetter;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class RubyObjectWithFieldsConverter
    extends AbstractConfigurationConverter
{
    /**
     * @param type
     * @return
     * @todo I am not sure what should go into this method
     */
    public boolean canConvert( Class type )
    {
        boolean retValue = true;

        if ( Dictionary.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }
        else if ( Map.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }
        else if ( Collection.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }

        return retValue;

//        return true;
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                    Class baseType, ClassLoader classLoader, ExpressionEvaluator expressionEvaluator,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Object retValue = fromExpression( configuration, expressionEvaluator, type );
        if ( retValue == null )
        {
            try
            {
                // it is a "composite" - we compose it from its children. It does not have a value of its own
                Class implementation = getClassForImplementationHint( type, configuration, classLoader );

                retValue = instantiateObject( implementation );

                processConfiguration( converterLookup, retValue, classLoader, configuration, expressionEvaluator, listener );
            }
            catch ( ComponentConfigurationException e )
            {
                if ( e.getFailedConfiguration() == null )
                {
                    e.setFailedConfiguration( configuration );
                }

                throw e;
            }
        }
        return retValue;
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassLoader classLoader,
                                     PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        processConfiguration( converterLookup, object, classLoader, configuration, null );
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassLoader classLoader,
                                     PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator )
        throws ComponentConfigurationException
    {
        processConfiguration( converterLookup, object, classLoader, configuration, expressionEvaluator, null );
    }

    public void processConfiguration( ConverterLookup converterLookup, Object object, ClassLoader classLoader,
                                     PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        int items = configuration.getChildCount();

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration childConfiguration = configuration.getChild( i );

            String elementName = childConfiguration.getName();

            Class type = String.class;
            String implementation = null;
            try
            {
                implementation = childConfiguration.getAttribute( "implementation" );

//System.out.println( elementName + " " + implementation );

                if( implementation != null )
                {
                    type = Class.forName( implementation );
                }
                else if ( childConfiguration.getChildCount() > 0 )
                {
                    type = Map.class;
                }
            }
            catch( Exception e )
            {
                throw new ComponentConfigurationException( implementation + " is not a found class type. Check the spelling", e );
            }

            RubyComponentValueSetter valueSetter = 
                new RubyComponentValueSetter( fromXML( elementName ), type, object, converterLookup, listener );

            valueSetter.configure( childConfiguration, classLoader, expressionEvaluator );
        }
    }
}
