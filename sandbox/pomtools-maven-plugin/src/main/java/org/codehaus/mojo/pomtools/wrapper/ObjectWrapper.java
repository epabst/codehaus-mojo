package org.codehaus.mojo.pomtools.wrapper;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.mojo.pomtools.PomToolsPluginContext;
import org.codehaus.mojo.pomtools.config.FieldConfiguration;
import org.codehaus.mojo.pomtools.wrapper.modify.AbstractModifiableObject;
import org.codehaus.mojo.pomtools.wrapper.modify.Modifiable;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanField;
import org.codehaus.mojo.pomtools.wrapper.reflection.BeanFields;
import org.codehaus.mojo.pomtools.wrapper.reflection.FactoryBeanField;
import org.codehaus.mojo.pomtools.wrapper.reflection.ModelReflectionException;
import org.codehaus.mojo.pomtools.wrapper.reflection.tostring.ObjectToStringBuilder;
import org.codehaus.mojo.pomtools.wrapper.reflection.tostring.ObjectWrapperToStringBuilder;
import org.codehaus.mojo.pomtools.wrapper.reflection.tostring.ToStringBuilderFactory;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author <a href="mailto:dhawkins@codehaus.org">David Hawkins</a>
 * @version $Id$
 */
public class ObjectWrapper
    extends AbstractModifiableObject    
{
    public static final String FIELD_PATH_SEPARATOR = ".";
    
    private static final ObjectToStringBuilder DEFAULT_TO_STRING_BUILDER = new ObjectWrapperToStringBuilder(); 

    private BeanFields fields;
    
    private final FieldConfiguration fieldConfig;
    
    private final ObjectWrapper parent;

    private Object wrappedObject;
    
    private Class wrappedObjectClass;
    
    private final Map wrappedValueMap = new HashMap();
    
    private final Set createdValueSet = new HashSet();
    
    private final String name;
    
    private String fullName;
    
    private final Set modifiedFields = new HashSet();
    
    private final ObjectToStringBuilder toStringBuilder;
    
    /** Constructs a new ObjectWrapper and specifies the implClass that is used to create
     * an empty object if the objectToWrap is null. 
     * The {@link Modifiable} parent is set to the parent;
     * 
     * @param parent        the parent ObjectWrapper of this object
     * @param objectToWrap  the value object to wrap.  This is a field of the parent
     * @param name          the field name that the objectToWrap is called within the parent
     * @param objectToWrapClass     the Class to use to create a new objectToWrap if the supplied one is null
     */
    public ObjectWrapper( ObjectWrapper parent, Object objectToWrap, String name, Class objectToWrapClass )
    {
        this( parent, parent, objectToWrap, name, objectToWrapClass ); 
    }
    
    /** Constructs a new ObjectWrapper and does not specify a implClass.
     * The objectToWrap cannot be null when using this constructor.  ObjectWrapper 
     * needs an instance of an object to wrap and will create one if it is not supplied. 
     * The {@link Modifiable} parent is set to the parent;
     * 
     * @param parent        the parent ObjectWrapper of this object
     * @param objectToWrap  the value object to wrap.  This is a field of the parent
     * @param name          the field name that the objectToWrap is called within the parent
     */
    public ObjectWrapper( ObjectWrapper parent, Object objectToWrap, String name )
    {
        this( parent, parent, objectToWrap, name, (Class) null );
    }
    
    /** Constructs an ObjectWrapper that wraps the objectToWrap.
     * ObjectWrapper needs an instance of an object to wrap will create one 
     * the supplied objectToWrap is null. The default constructor will be called
     * on the implClass.  
     * 
     * @param parentModifiable allows separate specification of the parentModifiable
     * @param parent        the parent ObjectWrapper of this object
     * @param objectToWrap  the value object to wrap.  This is a field of the parent
     * @param name          the field name that the objectToWrap is called within the parent
     * @param objectToWrapClass     the Class to use to create a new objectToWrap if the supplied one is null
     */
    public ObjectWrapper( Modifiable parentModifiable, ObjectWrapper parent, 
                          Object objectToWrap, String name, Class objectToWrapClass )
    {
        super( parentModifiable );

        this.parent = parent;
        
        this.name = name;
        
        this.fieldConfig = PomToolsPluginContext.getInstance().getFieldConfiguration( this.getFullName() );
        
        if ( objectToWrap == null )
        {
            if ( objectToWrapClass == null )
            {
                throw new IllegalStateException( "ObjectWrapper needs an underlying object, but no object was " 
                        + "supplied and no implClass was supplied to create an empty object." );
            }
            
            this.wrappedObjectClass = objectToWrapClass;
            
            try
            {
                this.wrappedObject = ConstructorUtils.invokeConstructor( objectToWrapClass, null );
            }
            catch ( NoSuchMethodException e )
            {
                throw new ModelReflectionException( e );
            }
            catch ( IllegalAccessException e )
            {
                throw new ModelReflectionException( e );
            }
            catch ( InvocationTargetException e )
            {
                throw new ModelReflectionException( e );
            }
            catch ( InstantiationException e )
            {
                throw new ModelReflectionException( e );
            }
        }
        else
        {
            this.wrappedObject = objectToWrap;
            
            this.wrappedObjectClass = objectToWrap.getClass();
        }
        
        this.fields = new BeanFields( this.getFullName(), 
                                      this.wrappedObject );
        
        this.toStringBuilder = initToStringBuilder();
    }
    
    private ObjectToStringBuilder initToStringBuilder()
    {
        if ( fieldConfig != null && fieldConfig.getToStringBuilder() != null )
        {
            return ToStringBuilderFactory.get( fieldConfig.getToStringBuilder() );
        }
        else
        {
            return DEFAULT_TO_STRING_BUILDER;
        }
    }
    
    /** Creates an instance of our wrapped class by calling its default constructor.
     * TODO: This method could probably use caching when it is being called by isSameAsDefault()
     */
    protected Object createDefaultInstance()
    {
        try
        {
            return ConstructorUtils.invokeConstructor( wrappedObjectClass, null );
        }
        catch ( NoSuchMethodException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( InvocationTargetException e )
        {
            throw new ModelReflectionException( e );
        }
        catch ( InstantiationException e )
        {
            throw new ModelReflectionException( e );
        }
    }
    
    public ObjectWrapper getParent()
    {
        return this.parent;
    }
    
    /** Returns the name of the field to which this object belongs. 
     * 
     * @return this object's field name
     */
    public String getName()
    {
        return this.name;
    }
    
    /** Returns the fully qualified name of the wrapped object.  
     * <p>
     * Objects are comprised of fields which have names.  A fully qualified name
     * is the this name of the field this object belongs to, as well as that field's 
     * owner and up.<br>
     * For example:<br>
     * If this objects name were "dependency", it could have a fullName of:<br>
     * project.dependencies.dependency
     * 
     * @return the full name of the wrapped object
     */
    public String getFullName()
    {
        if ( fullName == null )
        {
            if ( parent == null )
            {
                fullName = this.name;
            }
            else
            {
                fullName = parent.getFullName() + FIELD_PATH_SEPARATOR + this.name;
            }
        }
        
        return fullName;
    }
    
    /** Returns the {@link #toString()} representation of the value with 
     * an appended annotation if this object has been modified.
     *  
     * @return toString() with an annotation of modified
     */
    public final String getValueLabel()
    {
        return toString();
    }
    
    /** Returns the original wrapped object passed to or created in the constructor.
     * <p>
     * Subclasses should excercise caution an never modify this object directly.
     * 
     * @return
     */
    protected Object getInternalWrappedObject() 
    {
        return wrappedObject;
    }
    
    /** Returns the original wrapped object with all modifications applied to it.
     * Note that this method will return null if the object is the same as the default constructor
     * for the object.  
     * @return the wrapped object or null is the object isEmpty() 
     */
    public Object getWrappedObject()
    {
        if ( isEmpty() )
        {
            return null;
        }
        
        for ( Iterator i = getFields().iterator(); i.hasNext(); )
        {
            BeanField field = (BeanField) i.next();
            
            if ( field.isWrappedValue() )
            {
                ObjectWrapper value = (ObjectWrapper) getFieldValue( field, false );
                
                if ( value != null )
                {
                    if ( createdValueSet.contains( field ) && value.isSameAsDefault() )
                    {
                        // we can ignore this value because we created the value AND it 
                        // is the same as the default
                        value = null; 
                    }
                    else
                    {
                        setWrappedObjectValue( field, value.getWrappedObject() );
                    }
                }
            }
            
        }
           
        return wrappedObject;
    }
    
    /** Returns the internal {@link BeanFields} used to describe this object. 
     */
    public final BeanFields getFields() 
    {
        return fields;
    }
    
    /** Iterates through each {@link BeanField} and determines if the value is empty.
     * <p>
     * If the value is an instance of <code>ObjectWrapper</code>, the <code>isEmpty()</code>
     * method is called on that object.<br>
     * If the value is a <code>String</code>, the {@link StringUtils#isNotEmpty(java.lang.String)}
     * method is used.<br>
     * The object is considered to be NOT empty if any field (other than String or ObjectWrapper) is non null.
     * 
     */
    public boolean isEmpty() 
    {
        for ( Iterator i = getFields().iterator(); i.hasNext(); )
        {
            BeanField field = (BeanField) i.next();
            Object value = getFieldValue( field, false );
            
            if ( value != null )
            {
                if ( field.isWrappedValue() )
                {
                    if ( !( (ObjectWrapper) value ).isEmpty() )
                    {
                        return false;
                    }
                }
                else if ( !( value instanceof String ) || StringUtils.isNotEmpty( (String) value ) )
                {
                    return false;
                }                
            }
        }
        
        return true;
    }
    
    /** Returns whether the wrapped object is in the same state as
     * it's default constructor. This is useful in maven to prevent 
     * writing a section that is really only populated with the defaults.
     */
    public boolean isSameAsDefault()
    {
        Object defaultInstance = createDefaultInstance();
        
        for ( Iterator i = getFields().iterator(); i.hasNext(); )
        {
            BeanField field = (BeanField) i.next();
            Object myValue = getFieldValue( field, false );
            Object defaultValue = getWrappedObjectValue( defaultInstance, field );
            
            if ( !equals( myValue, defaultValue, field ) )
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    private boolean equals( Object myValue, Object defaultValue, BeanField field )
    {
        if ( myValue == null )
        {
            return defaultValue == null;
        }
        else if ( defaultValue == null )
        {
            return false;
        }
        else
        {
            if ( field.isWrappedValue() )
            {
                return ( (ObjectWrapper) myValue ).isSameAsDefault();
            }
            else
            {
                return myValue.equals( defaultValue );
            }
        }
    }
    
    
    public String toString()
    {
        return this.toStringBuilder.toString( this );        
    }
    
    private BeanField getField( String fieldName )
    {
        BeanField field = getFields().get( fieldName );
        
        if ( field == null )
        {
            throw new IllegalArgumentException( "\"" + fieldName + "\" is not a valid field for " + getFullName() );
        }
        
        return field;
    }
    
    /** Returns the value for the wrapped object for the specified fieldName.
     * 
     * @param fieldName
     * @throws IllegalArgumentException if the field cannot be found.
     */
    public Object getFieldValue( String fieldName )
    {
        return getFieldValue( getField( fieldName ) );
    }
    
    /** Returns the value for the wrapped object for the specified field.
     * This method creates a value if the specified field is null
     * 
     * @param field
     * @return
     */
    public Object getFieldValue( BeanField field )
    {
        return getFieldValue( field, true );
    }
    
    /** Returns the value for the wrapped object for the specified field.
     * Specifies whether to create the value if the underlying object's value
     * is null.  Turning off object creation is useful in testing isEmpty and 
     * equality in that it prevents having to do deep comparisons when the value is 
     * actually null.
     * 
     * @param field
     * @return
     */
    public Object getFieldValue( BeanField field, boolean createIfNull )
    {
        if ( field.isWrappedValue() )
        {
            ObjectWrapper wrappedValue = (ObjectWrapper) wrappedValueMap.get( field );
            
            // Create a wrapper if we don't already have a wrapper for this value
            if ( wrappedValue == null )
            {
                Object objectToWrap = getWrappedObjectValue( field );
                
                if ( objectToWrap != null || createIfNull )
                {
                    if ( objectToWrap == null )
                    {
                        // add the value to our createdValueSet so we can identify 
                        // values created vs values that were existing.
                        createdValueSet.add( field );
                    }
                    
                    wrappedValue = ( (FactoryBeanField) field ).createWrapperObject( this, objectToWrap );
                    
                    wrappedValueMap.put( field, wrappedValue );
                }
            }
            
            return wrappedValue;
        }
        else
        {
            return getWrappedObjectValue( field );
        }
    }
    
    
    /** Sets the value for specified field of the underlying wrapped object 
     * to the specified value.
     * 
     * @throws IllegalArgumentException if the field cannot be found. 
     */
    public void setFieldValue( String fieldName, Object value )
    {
        setFieldValue( getField( fieldName ), value );
    }
    
    /** Sets the value for specified field of the underlying wrapped object 
     * to the specified value.
     */
    public void setFieldValue( BeanField field, Object value )
    {
        if ( field.isWrappedValue() )
        {
            ObjectWrapper obj = (ObjectWrapper) wrappedValueMap.get( field );
            
            if ( obj == null )
            {
                throw new IllegalStateException( "Attempted to set value for a wrapped object that did not exist." );
            }
            
            obj.setFieldValue( field, value );
            
            setModified( field );
        }
        else
        {
            // Don't set the value if it didn't change.
            if ( !StringUtils.equals( String.valueOf( getFieldValue( field, false ) ), String.valueOf( value ) ) )
            {
                setWrappedObjectValue( field, value );
                
                setModified( field );
            }
        }
    }
    
    public boolean isFieldModified( BeanField field )
    {
        if ( modifiedFields.contains( field ) )
        {
            return true;
        }
        
        if ( field.isWrappedValue() )
        {
            ObjectWrapper wrapper = (ObjectWrapper) getFieldValue( field, false );
            if ( wrapper != null )
            {
                return wrapper.isModified();
            }
        }
        
        return false;
    }
    
    private Object getWrappedObjectValue( BeanField field )
    {
        return getWrappedObjectValue( wrappedObject, field );
    }
    
    private Object getWrappedObjectValue( Object obj, BeanField field )
        throws ModelReflectionException
    {
        String msg = "Unable to get value for " + getName() + "." + field.getFieldName();

        try
        {
            return (Object) PropertyUtils.getProperty( obj, field.getFieldName() );
        }
        catch ( IllegalAccessException e )
        {
            throw new ModelReflectionException( msg, e );
        }
        catch ( InvocationTargetException e )
        {
            throw new ModelReflectionException( msg, e );
        }
        catch ( NoSuchMethodException e )
        {
            throw new ModelReflectionException( msg, e );
        }
    }

    private void setWrappedObjectValue( BeanField field, Object value )
        throws ModelReflectionException
    {
        String msg = "Unable to set value for " + getName() + "." + field.getFieldName();
        
        try
        {
            BeanUtils.setProperty( wrappedObject, field.getFieldName(), value );            
        }
        catch ( IllegalAccessException e )
        {
            throw new ModelReflectionException( msg, e );
        }
        catch ( InvocationTargetException e )
        {
            throw new ModelReflectionException( msg, e );
        }
    }

    /** Sets the modified flag to true and adds the field to our modified fields.;
     *  
     */
    public void setModified( BeanField field )
    {
        setModified();
        modifiedFields.add( field );
    }
    
    /** Clears our list of modified fields if setting to false;
     *  
     */
    public void setModified( boolean modified )
    {
        super.setModified( modified );
        
        if ( !modified )
        {
            this.modifiedFields.clear();            
        }
    }
    
}
