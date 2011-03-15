package org.codehaus.mojo.pomtools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.config.PomToolsConfig;
import org.codehaus.mojo.pomtools.wrapper.ListWrapper;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.custom.ProjectWrapper;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanFields;
import org.codehaus.mojo.pomtools.wrapper.reflection.BooleanField;
import org.codehaus.mojo.pomtools.wrapper.reflection.ListField;
import org.codehaus.mojo.pomtools.wrapper.reflection.StringField;
import org.codehaus.plexus.util.StringUtils;


/** Dummy utility class which attempts to create a fully populated pom file
 * for use in testing the editor.
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class PomGenerator
{
    private static final String PATTERN_PROPERTY_NAME = "pattern.";

    private Map namePatterns = new HashMap();
    
    private File baseDir;

    public PomGenerator()
    {
        super();
    }

    public File getBaseDir()
    {
        if ( baseDir == null )
        {
            baseDir = new File( "" );            
        }
        
        return baseDir;
    }

    protected void initializeContext()
        throws MojoExecutionException
    {
        Log log = new SystemStreamLog();

        PomToolsConfig config = new PomToolsConfig();
        
        FieldConfiguration fieldConfig = new FieldConfiguration();
        fieldConfig.setFieldNamePattern( "^.*\\.distributionManagement\\.status$" );
        fieldConfig.setIgnore( true );
        
        config.addFieldConfiguration( fieldConfig );
        
        fieldConfig = new FieldConfiguration();
        fieldConfig.setFieldNamePattern( "^.*\\.modules$" );
        fieldConfig.setIgnore( true );
        
        config.addFieldConfiguration( fieldConfig );
        
        PomToolsPluginContext modelContext = new PomToolsPluginContext( null, null, null, config, true, log );

        PomToolsPluginContext.setInstance( modelContext );
    }
    
    public void createTestPom()
        throws IOException, MojoExecutionException
    {
        initializeContext();
        
        Model model = new Model();

        ObjectWrapper obj = new ObjectWrapper( null, null, model, "project", Model.class );

        Properties props = new Properties();
        props.load( PomGenerator.class.getClassLoader().getResourceAsStream( "pom-creation.properties" ) );

        for ( Iterator iter = props.keySet().iterator(); iter.hasNext(); )
        {
            String propName = (String) iter.next();

            if ( propName.startsWith( PATTERN_PROPERTY_NAME ) )
            {
                String patternStr = propName.substring( PATTERN_PROPERTY_NAME.length() );
                Pattern p = Pattern.compile( StringUtils.replace( patternStr, "\\\\", "\\" ) );

                this.namePatterns.put( p, props.getProperty( propName ) );
            }
        }

        populateObject( obj, props );
        
        Model resultModel = (Model) obj.getWrappedObject();

        String tempDir = System.getProperty( "java.io.tmpdir" );
        writeModel( new File( tempDir, "generated-testpom/pom.xml" ),      resultModel );
        writeModel( new File( tempDir, "generated-testpom/pom-copy.xml" ), resultModel );
    }
    
    protected void writeModel( File pomFile, Model model )
        throws IOException
    {
        pomFile.getParentFile().mkdirs();

        ProjectWrapper.writeModel( pomFile, model );
        
        System.out.println( "Wrote pom to: " + pomFile.getAbsolutePath() );
    }

    public Object generateValue( BeanField field, ObjectWrapper obj, Properties props )
    {
        String fieldName = field.getFullFieldName( obj );

        // First see if we configured a value for this field
        Object value = getConfiguredValue( fieldName, props );

        if ( value != null )
        {
            return value;
        }

        // Next see if the value has a default;
        value = obj.getFieldValue( field );

        if ( value != null )
        {
            return value;
        }

        // Next, generate a new value
        if ( field instanceof BooleanField )
        {
            return "true";
        }
        else if ( field instanceof StringField )
        {
            if ( field.getPossibleValues() != null && !field.getPossibleValues().isEmpty() )
            {
                return (String) field.getPossibleValues().get( 0 );
            }
            else
            {
                return "stringValue-" + fieldName;
            }
        }
        else
        {
            throw new IllegalArgumentException( "Unknown field type to generate value for: "
                + field.getClass().getName() );
        }
    }

    protected String getConfiguredValue( String fieldName, Properties props )
    {
        String value = props.getProperty( fieldName );

        if ( value == null )
        {
            for ( Iterator iter = namePatterns.entrySet().iterator(); iter.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) iter.next();

                Matcher m = ( (Pattern) entry.getKey() ).matcher( fieldName );

                if ( m.find() )
                {
                    value = (String) entry.getValue();
                    break;
                }
            }
        }

        return value;
    }

    protected void populateObject( ObjectWrapper obj, Properties props )
    {
        BeanFields fields = obj.getFields();

        for ( Iterator iter = fields.iterator(); iter.hasNext(); )
        {
            BeanField field = (BeanField) iter.next();

            if ( field.isWrappedValue() )
            {
                if ( field instanceof ListField )
                {
                    ListWrapper list = (ListWrapper) obj.getFieldValue( field );
                    populateObject( (ObjectWrapper) list.createItem( null ), props );
                }
                else
                {
                    populateObject( (ObjectWrapper) obj.getFieldValue( field ), props );
                }
            }
            else
            {
                obj.setFieldValue( field, generateValue( field, obj, props ) );
            }
        }
    }
    
    public static void main( String[] args )
        throws MojoExecutionException, IOException
    {
        PomGenerator generator = new PomGenerator();
        
        generator.createTestPom();
        
        System.out.println( "Test poms created" );
    }
}
