/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.6</a>, using an XML
 * Schema.
 * $Id$
 */

package org.opennms.netmgt.config.vacuumd;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Triggers and Actions hooked up and run by the Vacuumd
 *  schedule using interval for frequency
 * 
 * @version $Revision$ $Date$
 */
public class Automation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The name of this automation
     */
    private java.lang.String _name;

    /**
     * How ofter should this autmation run
     */
    private int _interval;

    /**
     * keeps track of state for field: _interval
     */
    private boolean _has_interval;

    /**
     * reference a trigger from the collection of
     *  triggers
     */
    private java.lang.String _triggerName;

    /**
     * reference an action from the collection of
     *  actions
     */
    private java.lang.String _actionName;

    /**
     * specify an event UEI to send
     */
    private java.lang.String _autoEventName;

    /**
     * enable/disable this automation
     */
    private boolean _active;

    /**
     * keeps track of state for field: _active
     */
    private boolean _has_active;


      //----------------/
     //- Constructors -/
    //----------------/

    public Automation() {
        super();
    } //-- org.opennms.netmgt.config.vacuumd.Automation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteActive
     * 
     */
    public void deleteActive()
    {
        this._has_active= false;
    } //-- void deleteActive() 

    /**
     * Method deleteInterval
     * 
     */
    public void deleteInterval()
    {
        this._has_interval= false;
    } //-- void deleteInterval() 

    /**
     * Returns the value of field 'actionName'. The field
     * 'actionName' has the following description: reference an
     * action from the collection of
     *  actions
     * 
     * @return String
     * @return the value of field 'actionName'.
     */
    public java.lang.String getActionName()
    {
        return this._actionName;
    } //-- java.lang.String getActionName() 

    /**
     * Returns the value of field 'active'. The field 'active' has
     * the following description: enable/disable this automation
     * 
     * @return boolean
     * @return the value of field 'active'.
     */
    public boolean getActive()
    {
        return this._active;
    } //-- boolean getActive() 

    /**
     * Returns the value of field 'autoEventName'. The field
     * 'autoEventName' has the following description: specify an
     * event UEI to send
     * 
     * @return String
     * @return the value of field 'autoEventName'.
     */
    public java.lang.String getAutoEventName()
    {
        return this._autoEventName;
    } //-- java.lang.String getAutoEventName() 

    /**
     * Returns the value of field 'interval'. The field 'interval'
     * has the following description: How ofter should this
     * autmation run
     * 
     * @return int
     * @return the value of field 'interval'.
     */
    public int getInterval()
    {
        return this._interval;
    } //-- int getInterval() 

    /**
     * Returns the value of field 'name'. The field 'name' has the
     * following description: The name of this automation
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'triggerName'. The field
     * 'triggerName' has the following description: reference a
     * trigger from the collection of
     *  triggers
     * 
     * @return String
     * @return the value of field 'triggerName'.
     */
    public java.lang.String getTriggerName()
    {
        return this._triggerName;
    } //-- java.lang.String getTriggerName() 

    /**
     * Method hasActive
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasActive()
    {
        return this._has_active;
    } //-- boolean hasActive() 

    /**
     * Method hasInterval
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasInterval()
    {
        return this._has_interval;
    } //-- boolean hasInterval() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'actionName'. The field 'actionName'
     * has the following description: reference an action from the
     * collection of
     *  actions
     * 
     * @param actionName the value of field 'actionName'.
     */
    public void setActionName(java.lang.String actionName)
    {
        this._actionName = actionName;
    } //-- void setActionName(java.lang.String) 

    /**
     * Sets the value of field 'active'. The field 'active' has the
     * following description: enable/disable this automation
     * 
     * @param active the value of field 'active'.
     */
    public void setActive(boolean active)
    {
        this._active = active;
        this._has_active = true;
    } //-- void setActive(boolean) 

    /**
     * Sets the value of field 'autoEventName'. The field
     * 'autoEventName' has the following description: specify an
     * event UEI to send
     * 
     * @param autoEventName the value of field 'autoEventName'.
     */
    public void setAutoEventName(java.lang.String autoEventName)
    {
        this._autoEventName = autoEventName;
    } //-- void setAutoEventName(java.lang.String) 

    /**
     * Sets the value of field 'interval'. The field 'interval' has
     * the following description: How ofter should this autmation
     * run
     * 
     * @param interval the value of field 'interval'.
     */
    public void setInterval(int interval)
    {
        this._interval = interval;
        this._has_interval = true;
    } //-- void setInterval(int) 

    /**
     * Sets the value of field 'name'. The field 'name' has the
     * following description: The name of this automation
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'triggerName'. The field
     * 'triggerName' has the following description: reference a
     * trigger from the collection of
     *  triggers
     * 
     * @param triggerName the value of field 'triggerName'.
     */
    public void setTriggerName(java.lang.String triggerName)
    {
        this._triggerName = triggerName;
    } //-- void setTriggerName(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.opennms.netmgt.config.vacuumd.Automation) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.Automation.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
