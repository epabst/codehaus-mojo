package org.codehaus.mojo.ruby;

import org.apache.maven.plugin.ContextEnabled;
import org.apache.maven.plugin.Mojo;
import org.codehaus.plexus.component.MapOrientedComponent;

public interface RubyMojo
    extends Mojo, ContextEnabled, MapOrientedComponent
{
    public static final String ROLE = Mojo.class.getName();

    void set( String key, Object value );
}
