package org.codehaus.mojo.ruby.plexus.component.configurator;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.mojo.ruby.plexus.component.configurator.converters.composite.RubyObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class RubyComponentConfigurator
    extends AbstractComponentConfigurator
{
    public void configureComponent( Object component, PlexusConfiguration configuration,
                                   ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                   ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        converterLookup.registerConverter( new ClassRealmConverter( containerRealm ) );

// TODO: need the "MojoDescriptor" to load script configurations correctly
        
        RubyObjectWithFieldsConverter converter = new RubyObjectWithFieldsConverter();

        converter.processConfiguration( converterLookup, component, containerRealm.getClassLoader(), configuration,
                expressionEvaluator, listener );
    }
}
