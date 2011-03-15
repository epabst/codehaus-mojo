package org.codehaus.mojo.buildinfo.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.codehaus.mojo.buildinfo.model.BuildInfo;

public class BuildInfoGenerator
{
    
    public void writeXml( String systemPropertyKeys, File outputFile )
        throws IOException
    {
        BuildInfo buildInfo = new BuildInfo();

        Properties systemProperties = System.getProperties();
        
        if ( systemPropertyKeys != null )
        {
            String[] keys = systemPropertyKeys.split( "," );
            for ( int i = 0; i < keys.length; i++ )
            {
                String value = systemProperties.getProperty( keys[i], BuildInfoConstants.MISSING_INFO_PLACEHOLDER );

                buildInfo.addSystemProperty( keys[i], value );
            }
        }        
        BuildInfoUtils.writeXml( buildInfo, outputFile );
    }

}
