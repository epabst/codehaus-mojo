package org.codehaus.mojo.hibernate3.configuration;

import org.hibernate.cfg.Configuration;

import java.io.File;

public class DefaultComponentConfiguration
    extends AbstractComponentConfiguration
{
// --------------------- Interface ComponentConfiguration ---------------------

    public String getName()
    {
        return "configuration";
    }

    protected Configuration createConfiguration()
    {
        return new Configuration();
    }

    protected void doConfiguration( Configuration configuration )
    {
        super.doConfiguration( configuration );

        // if the mojo has the scan-classes flag on then scna the output directories for hbm.xml files
        if ( getExporterMojo().getComponentProperty( "scan-classes", false ) )
        {
            // add the output directory
            File outputDirectory = new File( getExporterMojo().getProject().getBuild().getOutputDirectory() );
            if ( outputDirectory.exists() && outputDirectory.isDirectory() )
            {
                configuration.addDirectory( outputDirectory );
            }

            File testOutputDirectory = new File( getExporterMojo().getProject().getBuild().getTestOutputDirectory() );
            if ( testOutputDirectory.exists() && testOutputDirectory.isDirectory() )
            {
                configuration.addDirectory( testOutputDirectory );
            }
        }
    }
}
