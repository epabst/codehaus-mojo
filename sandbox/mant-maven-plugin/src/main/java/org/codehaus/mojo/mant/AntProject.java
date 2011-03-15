package org.codehaus.mojo.mant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * Wraps an ant document and provides a means of executing the contained target.
 */
public class AntProject
{
    private final Document document;

    public AntProject( Document document )
    {
        this.document = document;
    }

    /**
     * Executes the single target contained in the supplied document.
     * @param document
     * @throws Exception
     */
    public void execute()
        throws Exception
    {
        File buildFile = createBuildFile( document );
        Project project = new Project();
        project.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
        project.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference( "ant.projectHelper", helper );
        helper.parse( project, buildFile );
        project.executeTarget( project.getDefaultTarget() );
    }

    /**
     * Writes the given document to a temporary build file for executing.
     * @param document
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws Exception
     */
    private File createBuildFile( Document document )
        throws IOException, FileNotFoundException, Exception
    {
        File buildFile = File.createTempFile( "mant", ".xml" );
        buildFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream( buildFile );
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" );
        XMLWriter writer = new XMLWriter( out, outformat );
        writer.write( document );
        writer.flush();
        return buildFile;
    }

    public String toString()
    {
        try
        {
            StringWriter out = new StringWriter();
            OutputFormat outformat = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter( out, outformat );
            writer.write( document );
            writer.flush();
            return out.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
