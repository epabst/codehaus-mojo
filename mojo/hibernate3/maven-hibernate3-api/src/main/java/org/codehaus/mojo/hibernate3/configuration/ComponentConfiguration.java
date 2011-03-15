package org.codehaus.mojo.hibernate3.configuration;

import org.hibernate.cfg.Configuration;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.hibernate3.ExporterMojo;

public interface ComponentConfiguration
{
    String ROLE = ComponentConfiguration.class.getName();

    Configuration getConfiguration( ExporterMojo exporterMojo )
        throws MojoExecutionException;

    String getName();
}
