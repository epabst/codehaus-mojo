package org.codehaus.mojo.buildinfo.ant;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.codehaus.mojo.buildinfo.util.BuildInfoGenerator;

public class WriteBuildInfoTask
    extends Task
{
    
    private static final String SYSTEM_PROPERTIES_PROPERTY = "buildinfo.systemProperties";
    
    private static final String OUTPUT_FILE_PROPERTY = "buildinfo.outputFile";
    
    public void execute()
        throws BuildException
    {
        Project project = getProject();
        
        String systemPropertyKeys = project.getProperty( SYSTEM_PROPERTIES_PROPERTY );
        
        File outputFile = new File( (String) project.getProperty( OUTPUT_FILE_PROPERTY ) );
        
        BuildInfoGenerator buildInfoGenerator = new BuildInfoGenerator();
        
        try
        {
            buildInfoGenerator.writeXml( systemPropertyKeys, outputFile );
        }
        catch ( IOException e )
        {
            throw new BuildException( "Error writing buildinfo XML file. Reason: " + e.getMessage(), e );
        }
    }

}
