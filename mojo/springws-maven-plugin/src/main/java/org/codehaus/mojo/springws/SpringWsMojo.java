package org.codehaus.mojo.springws;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ws.wsdl.WsdlDefinition;

/**
 * Exports Spring-ws WSDL definitions to files.
 * 
 * @goal springws
 * @phase process-resources
 * @requiresDependencyResolution
 */
public class SpringWsMojo
    extends AbstractMojo
{
    
    /**
     * Suffix of generated wsdl files.
     * @parameter default-value=".wsdl" expression="${springws.suffix}"
     * @required
     */
    private String suffix;
    
    /**
     * ContextFiles to look for WSDL generators in.
     * 
     * @parameter expression="${springws.contextLocations}"
     */
    private String[] contextLocations;
    
    /**
     * Directory for generated .wsdl files.
     * @parameter expression="${project.build.directory}/generated-resources/springws"
     */
    private File targetDirectory;
    
    private final Transformer transformer;
    
    
    public SpringWsMojo()
        throws TransformerException
    {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer();
    }

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        targetDirectory.mkdirs();

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext( contextLocations );
        Map beanMap = applicationContext.getBeansOfType( WsdlDefinition.class );
        for ( Iterator i = beanMap.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            String serviceName = entry.getKey().toString();
            WsdlDefinition wsdlDefinition = (WsdlDefinition) entry.getValue();
            Source source = wsdlDefinition.getSource();
            File file = export( source, serviceName );
            getLog().debug( "Generated: " + file.getAbsolutePath() );
        }
    }
    
    private File export( Source source, String serviceName )
        throws MojoExecutionException
    {
        File file = new File( targetDirectory, serviceName + suffix );
        Result result = new StreamResult( file );
        try 
        {    
            transformer.transform( source, result );
        }
        catch ( TransformerException e ) 
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        
        return file;
    }
}
