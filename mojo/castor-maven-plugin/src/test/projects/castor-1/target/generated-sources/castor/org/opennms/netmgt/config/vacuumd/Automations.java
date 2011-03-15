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
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Automations.
 * 
 * @version $Revision$ $Date$
 */
public class Automations implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Triggers and Actions hooked up and run by the Vacuumd
     *  schedule using interval for frequency
     */
    private java.util.ArrayList _automationList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Automations() {
        super();
        _automationList = new ArrayList();
    } //-- org.opennms.netmgt.config.vacuumd.Automations()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAutomation
     * 
     * 
     * 
     * @param vAutomation
     */
    public void addAutomation(org.opennms.netmgt.config.vacuumd.Automation vAutomation)
        throws java.lang.IndexOutOfBoundsException
    {
        _automationList.add(vAutomation);
    } //-- void addAutomation(org.opennms.netmgt.config.vacuumd.Automation) 

    /**
     * Method addAutomation
     * 
     * 
     * 
     * @param index
     * @param vAutomation
     */
    public void addAutomation(int index, org.opennms.netmgt.config.vacuumd.Automation vAutomation)
        throws java.lang.IndexOutOfBoundsException
    {
        _automationList.add(index, vAutomation);
    } //-- void addAutomation(int, org.opennms.netmgt.config.vacuumd.Automation) 

    /**
     * Method clearAutomation
     * 
     */
    public void clearAutomation()
    {
        _automationList.clear();
    } //-- void clearAutomation() 

    /**
     * Method enumerateAutomation
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateAutomation()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_automationList.iterator());
    } //-- java.util.Enumeration enumerateAutomation() 

    /**
     * Method getAutomation
     * 
     * 
     * 
     * @param index
     * @return Automation
     */
    public org.opennms.netmgt.config.vacuumd.Automation getAutomation(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _automationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.opennms.netmgt.config.vacuumd.Automation) _automationList.get(index);
    } //-- org.opennms.netmgt.config.vacuumd.Automation getAutomation(int) 

    /**
     * Method getAutomation
     * 
     * 
     * 
     * @return Automation
     */
    public org.opennms.netmgt.config.vacuumd.Automation[] getAutomation()
    {
        int size = _automationList.size();
        org.opennms.netmgt.config.vacuumd.Automation[] mArray = new org.opennms.netmgt.config.vacuumd.Automation[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.opennms.netmgt.config.vacuumd.Automation) _automationList.get(index);
        }
        return mArray;
    } //-- org.opennms.netmgt.config.vacuumd.Automation[] getAutomation() 

    /**
     * Method getAutomationCollection
     * 
     * Returns a reference to 'automation'. No type checking is
     * performed on any modications to the Collection.
     * 
     * @return ArrayList
     * @return returns a reference to the Collection.
     */
    public java.util.ArrayList getAutomationCollection()
    {
        return _automationList;
    } //-- java.util.ArrayList getAutomationCollection() 

    /**
     * Method getAutomationCount
     * 
     * 
     * 
     * @return int
     */
    public int getAutomationCount()
    {
        return _automationList.size();
    } //-- int getAutomationCount() 

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
     * Method removeAutomation
     * 
     * 
     * 
     * @param vAutomation
     * @return boolean
     */
    public boolean removeAutomation(org.opennms.netmgt.config.vacuumd.Automation vAutomation)
    {
        boolean removed = _automationList.remove(vAutomation);
        return removed;
    } //-- boolean removeAutomation(org.opennms.netmgt.config.vacuumd.Automation) 

    /**
     * Method setAutomation
     * 
     * 
     * 
     * @param index
     * @param vAutomation
     */
    public void setAutomation(int index, org.opennms.netmgt.config.vacuumd.Automation vAutomation)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _automationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _automationList.set(index, vAutomation);
    } //-- void setAutomation(int, org.opennms.netmgt.config.vacuumd.Automation) 

    /**
     * Method setAutomation
     * 
     * 
     * 
     * @param automationArray
     */
    public void setAutomation(org.opennms.netmgt.config.vacuumd.Automation[] automationArray)
    {
        //-- copy array
        _automationList.clear();
        for (int i = 0; i < automationArray.length; i++) {
            _automationList.add(automationArray[i]);
        }
    } //-- void setAutomation(org.opennms.netmgt.config.vacuumd.Automation) 

    /**
     * Method setAutomation
     * 
     * Sets the value of 'automation' by copying the given
     * ArrayList.
     * 
     * @param automationCollection the Vector to copy.
     */
    public void setAutomation(java.util.ArrayList automationCollection)
    {
        //-- copy collection
        _automationList.clear();
        for (int i = 0; i < automationCollection.size(); i++) {
            _automationList.add((org.opennms.netmgt.config.vacuumd.Automation)automationCollection.get(i));
        }
    } //-- void setAutomation(java.util.ArrayList) 

    /**
     * Method setAutomationCollection
     * 
     * Sets the value of 'automation' by setting it to the given
     * ArrayList. No type checking is performed.
     * 
     * @param automationCollection the ArrayList to copy.
     */
    public void setAutomationCollection(java.util.ArrayList automationCollection)
    {
        _automationList = automationCollection;
    } //-- void setAutomationCollection(java.util.ArrayList) 

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
        return (org.opennms.netmgt.config.vacuumd.Automations) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.Automations.class, reader);
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
