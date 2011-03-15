package org.codehaus.mojo.pomtools.wrapper;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.pomtools.AbstractPomToolsPluginTestCase;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.config.PomToolsConfig;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ObjectWrapperTest
    extends AbstractPomToolsPluginTestCase
{

    public ObjectWrapperTest()
    {
        super();
    }
    protected PomToolsConfig getPluginConfiguration()
        throws MojoExecutionException
    {
        PomToolsConfig pluginConfig = super.getPluginConfiguration();
        
        // ListFields
        FieldConfiguration fieldConfig = new FieldConfiguration();
        
        fieldConfig.setFieldNamePattern( "^.*\\.listFields$" );
        fieldConfig.setItemClassName( DummyBean.class.getName() );
        
        pluginConfig.addFieldConfiguration( fieldConfig );
        
        return pluginConfig;
    
    }
    
    public void testIsSameAsDefault()
        throws Exception
    {
        ObjectWrapper ow = new ObjectWrapper( null, new DummyBean(), "dummy" );
        
        assertTrue( ow.isSameAsDefault() );
        assertFalse( ow.isEmpty() );
        assertEquals( ow.getFieldValue( "defaultingField" ), "defaultValue"  );
        
        ow.setFieldValue( "stringField", "someValue" );
        
        assertFalse( ow.isSameAsDefault() );
    }
    
    public void testIsEmpty()
        throws Exception
    {
        ObjectWrapper ow = new ObjectWrapper( null, new DummyBean(), "dummy" );
        
        assertTrue( ow.isSameAsDefault() );
        assertFalse( ow.isEmpty() );
        assertEquals( ow.getFieldValue( "defaultingField" ), "defaultValue"  );
        
        ow.setFieldValue( "defaultingField", null );
        
        assertFalse( ow.isSameAsDefault() );
        
        assertFalse( ow.isEmpty() );
    }
    
    public void testGetSetValue()
    {
        ObjectWrapper ow = new ObjectWrapper( null, new DummyBean(), "dummy" );
        
        assertNull( ow.getFieldValue( "stringField" ) );
        ow.setFieldValue( "stringField", "someValue" );
        assertEquals( "someValue", ow.getFieldValue( "stringField" ) );
        
        assertNotNull( ow.getFieldValue( "booleanField" ) );
        assertEquals( Boolean.FALSE, ow.getFieldValue( "booleanField" ) );
        ow.setFieldValue( "booleanField", "true" );
        assertEquals( Boolean.TRUE, ow.getFieldValue( "booleanField" ) );
        
        ow.setFieldValue( "booleanField", "false" );
        assertEquals( Boolean.FALSE, ow.getFieldValue( "booleanField" ) );
        
        ow.setFieldValue( "booleanField", "junkText" );
        assertEquals( Boolean.FALSE, ow.getFieldValue( "booleanField" ) );
    }
    
    public void testModified() 
    {
        ObjectWrapper parent = new ObjectWrapper( null, new DummyBean(), "parentDummy" );
        ObjectWrapper child = new ObjectWrapper( parent, new DummyBean(), "dummy" );
        
        assertFalse( parent.isModified() );
        assertFalse( child.isModified() );
        
        for ( Iterator iter = child.getFields().iterator(); iter.hasNext(); )
        {
            assertFalse( child.isFieldModified( (BeanField) iter.next() ) );
        }
        
        child.setFieldValue( "stringField", "someValue" );
        
        assertTrue( parent.isModified() );
        assertTrue( child.isModified() );
    }
    
}
