package org.codehaus.mojo.was6;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.MojoExecutionException;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Executes the endpoint enabler ant task on the EAR archive.
 * 
 * @see <a href="http://publib.boulder.ibm.com/infocenter/wasinfo/v6r1/index.jsp?topic=/com.ibm.websphere.javadoc.doc/public_html/api/com/ibm/websphere/ant/tasks/endptEnabler.html">javadocs</a> for more information.
 * @goal endpointEnabler
 * @author karltdav
 * @since 1.1.1
 */
public class EndpointEnabler
    extends AbstractWas6Mojo
{

    /**
     * The earFile to process. See <a href="http://publib.boulder.ibm.com/infocenter/dmndhelp/v6r1mx/index.jsp?topic=/com.ibm.websphere.wbpmcore.javadoc.610.doc/web/apidocs/com/ibm/websphere/ant/tasks/endptEnabler.html">javadoc</a> for a description.
     * 
     * @parameter expression="${was6.earFile}" default-value="${project.artifact.file}"
     * @required
     */
    private File earFile;

    /**
     * @parameter
     */
    private Map properties;

    /**
     * {@inheritDoc}
     */
    protected void configureBuildScript( Document document )
        throws MojoExecutionException
    {
        configureTaskAttribute( document, "earFile", earFile );

        if ( properties != null ) {
            Element element = getTaskElement( document );
            for ( Iterator i = properties.entrySet().iterator(); i.hasNext(); )
            {
                Entry entry = (Entry) i.next();
                Element propertyElement = element.addElement( "property" );
                propertyElement.addAttribute( "key", entry.getKey().toString() );
                propertyElement.addAttribute( "value", entry.getValue().toString() );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String getTaskName()
    {
        return "wsEndpointEnabler";
    }

}
