package org.codehaus.mojo.pomtools.wrapper.reflection;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.mojo.pomtools.AbstractPomToolsPluginTestCase;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.config.PomToolsConfig;
import org.codehaus.mojo.pomtools.wrapper.DummyBean;
import org.codehaus.mojo.pomtools.wrapper.ObjectWrapper;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class BeanFieldsTest
    extends AbstractPomToolsPluginTestCase
{

    public BeanFieldsTest()
    {
        super();
    }

    protected PomToolsConfig getPluginConfiguration()
        throws MojoExecutionException
    {
        PomToolsConfig pluginConfig = super.getPluginConfiguration();
        
        // ListFields
        FieldConfiguration fieldConfig = new FieldConfiguration();
        
        fieldConfig.setFieldName( "dummy.listFields" );
        fieldConfig.setItemClassName( DummyBean.class.getName() );
        
        pluginConfig.addFieldConfiguration( fieldConfig );
        
        // ChangeMyLabel
        fieldConfig = new FieldConfiguration();
        
        fieldConfig.setFieldName( "dummy.changeMyLabel" );
        fieldConfig.setLabel( "New fancy label" );
        
        pluginConfig.addFieldConfiguration( fieldConfig );
        
        //IgnoreMe
        fieldConfig = new FieldConfiguration();
        
        fieldConfig.setFieldName( "dummy.ignoreMe" );
        fieldConfig.setIgnore( true );
        
        pluginConfig.addFieldConfiguration( fieldConfig );
        
        return pluginConfig;
        
    }
    
    public void testIntrospection()
    {
        ObjectWrapper ow = new ObjectWrapper( null, null, new DummyBean(), "dummy", DummyBean.class );
        
        BeanFields fields = ow.getFields();
        
        assertEquals( 6, fields.size() );
        
        int index = 0;
        BeanField field = fields.get( index++ );
        assertEquals( "booleanField",       field.getFieldName() );
        assertEquals( "dummy.booleanField", field.getFullFieldName( ow ) );
        assertEquals( BooleanField.class,   field.getClass() );
        
        field = fields.get( index++ );
        assertEquals( "changeMyLabel",       field.getFieldName() );
        assertEquals( "dummy.changeMyLabel", field.getFullFieldName( ow ) );
        assertEquals( "New fancy label",     field.getLabel() );
        assertEquals( StringField.class,     field.getClass() );
        
        field = fields.get( index++ );
        assertEquals( "defaultingField",       field.getFieldName() );
        assertEquals( "dummy.defaultingField", field.getFullFieldName( ow ) );
        assertEquals( StringField.class,       field.getClass() );
        
        field = fields.get( index++ );
        assertEquals( "listFields",       field.getFieldName() );
        assertEquals( "dummy.listFields", field.getFullFieldName( ow ) );
        assertEquals( ListField.class,    field.getClass() );
        
        field = fields.get( index++ );
        assertEquals( "objectField",       field.getFieldName() );
        assertEquals( "dummy.objectField", field.getFullFieldName( ow ) );
        assertEquals( CompositeField.class, field.getClass() );
        
        field = fields.get( index++ );
        assertEquals( "stringField",       field.getFieldName() );
        assertEquals( "dummy.stringField", field.getFullFieldName( ow ) );
        assertEquals( StringField.class,   field.getClass() );
    }
}
