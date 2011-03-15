package org.codehaus.mojo.ruby;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.factory.jruby.JRubyComponentFactory;
import org.codehaus.plexus.component.jruby.JRubyInvoker;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * Wraps the plexus jruby factory.
 * @author Eric Redmond
 */
public class JRubyMojoComponentFactory
    extends JRubyComponentFactory
{
    public Object newInstance( ComponentDescriptor descriptor, ClassRealm realm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        descriptor.setComponentComposer( "map-oriented" );
        descriptor.setComponentConfigurator( "jruby" );

        return new DefaultRubyMojo( (JRubyInvoker)super.newInstance( descriptor, realm, container ) );
    }
}
