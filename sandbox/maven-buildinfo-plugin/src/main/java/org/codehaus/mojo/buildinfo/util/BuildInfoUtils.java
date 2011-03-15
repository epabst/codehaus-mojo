package org.codehaus.mojo.buildinfo.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.codehaus.mojo.buildinfo.model.BuildInfo;
import org.codehaus.mojo.buildinfo.model.io.xpp3.BuildInfoXpp3Reader;
import org.codehaus.mojo.buildinfo.model.io.xpp3.BuildInfoXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class BuildInfoUtils
{

    private static final String STANDARD_FILENAME_FORMAT = "@artifactId@-@version@-buildinfo.xml";

    public static File getStandardProjectBuildInfoFile( File outputDirectory, String artifactId, String version )
    {
        String filename = STANDARD_FILENAME_FORMAT;
        filename = filename.replaceAll( "@artifactId@", artifactId );
        filename = filename.replaceAll( "@version@", version );

        return new File( outputDirectory, filename );
    }

    public static void writeXml( BuildInfo buildInfo, File outputFile )
        throws IOException
    {
        BuildInfoXpp3Writer writer = new BuildInfoXpp3Writer();
        FileWriter fWriter = null;
        try
        {
            fWriter = new FileWriter( outputFile );
            writer.write( fWriter, buildInfo );
        }
        finally
        {
            IOUtil.close( fWriter );
        }
    }

    public static BuildInfo readXml( File inputFile )
        throws IOException, BuildInfoConstructionException
    {
        BuildInfoXpp3Reader reader = new BuildInfoXpp3Reader();
        FileReader fReader = null;
        try
        {
            fReader = new FileReader( inputFile );
            return reader.read( fReader );
        }
        catch ( XmlPullParserException e )
        {
            throw new BuildInfoConstructionException( "Error parsing buildinfo from XML file. Reason: " + e.getMessage(), e );
        }
        finally
        {
            IOUtil.close( fReader );
        }
    }

}
