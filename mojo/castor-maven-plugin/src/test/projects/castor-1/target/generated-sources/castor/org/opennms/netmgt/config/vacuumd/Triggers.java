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
 * A collection of triggers
 * 
 * @version $Revision$ $Date$
 */
public class Triggers implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * A query to the database with a resultset used for
     *  actions
     */
    private java.util.ArrayList _triggerList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Triggers() {
        super();
        _triggerList = new ArrayList();
    } //-- org.opennms.netmgt.config.vacuumd.Triggers()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addTrigger
     * 
     * 
     * 
     * @param vTrigger
     */
    public void addTrigger(org.opennms.netmgt.config.vacuumd.Trigger vTrigger)
        throws java.lang.IndexOutOfBoundsException
    {
        _triggerList.add(vTrigger);
    } //-- void addTrigger(org.opennms.netmgt.config.vacuumd.Trigger) 

    /**
     * Method addTrigger
     * 
     * 
     * 
     * @param index
     * @param vTrigger
     */
    public void addTrigger(int index, org.opennms.netmgt.config.vacuumd.Trigger vTrigger)
        throws java.lang.IndexOutOfBoundsException
    {
        _triggerList.add(index, vTrigger);
    } //-- void addTrigger(int, org.opennms.netmgt.config.vacuumd.Trigger) 

    /**
     * Method clearTrigger
     * 
     */
    public void clearTrigger()
    {
        _triggerList.clear();
    } //-- void clearTrigger() 

    /**
     * Method enumerateTrigger
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateTrigger()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_triggerList.iterator());
    } //-- java.util.Enumeration enumerateTrigger() 

    /**
     * Method getTrigger
     * 
     * 
     * 
     * @param index
     * @return Trigger
     */
    public org.opennms.netmgt.config.vacuumd.Trigger getTrigger(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _triggerList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.opennms.netmgt.config.vacuumd.Trigger) _triggerList.get(index);
    } //-- org.opennms.netmgt.config.vacuumd.Trigger getTrigger(int) 

    /**
     * Method getTrigger
     * 
     * 
     * 
     * @return Trigger
     */
    public org.opennms.netmgt.config.vacuumd.Trigger[] getTrigger()
    {
        int size = _triggerList.size();
        org.opennms.netmgt.config.vacuumd.Trigger[] mArray = new org.opennms.netmgt.config.vacuumd.Trigger[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.opennms.netmgt.config.vacuumd.Trigger) _triggerList.get(index);
        }
        return mArray;
    } //-- org.opennms.netmgt.config.vacuumd.Trigger[] getTrigger() 

    /**
     * Method getTriggerCollection
     * 
     * Returns a reference to 'trigger'. No type checking is
     * performed on any modications to the Collection.
     * 
     * @return ArrayList
     * @return returns a reference to the Collection.
     */
    public java.util.ArrayList getTriggerCollection()
    {
        return _triggerList;
    } //-- java.util.ArrayList getTriggerCollection() 

    /**
     * Method getTriggerCount
     * 
     * 
     * 
     * @return int
     */
    public int getTriggerCount()
    {
        return _triggerList.size();
    } //-- int getTriggerCount() 

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
     * Method removeTrigger
     * 
     * 
     * 
     * @param vTrigger
     * @return boolean
     */
    public boolean removeTrigger(org.opennms.netmgt.config.vacuumd.Trigger vTrigger)
    {
        boolean removed = _triggerList.remove(vTrigger);
        return removed;
    } //-- boolean removeTrigger(org.opennms.netmgt.config.vacuumd.Trigger) 

    /**
     * Method setTrigger
     * 
     * 
     * 
     * @param index
     * @param vTrigger
     */
    public void setTrigger(int index, org.opennms.netmgt.config.vacuumd.Trigger vTrigger)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _triggerList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _triggerList.set(index, vTrigger);
    } //-- void setTrigger(int, org.opennms.netmgt.config.vacuumd.Trigger) 

    /**
     * Method setTrigger
     * 
     * 
     * 
     * @param triggerArray
     */
    public void setTrigger(org.opennms.netmgt.config.vacuumd.Trigger[] triggerArray)
    {
        //-- copy array
        _triggerList.clear();
        for (int i = 0; i < triggerArray.length; i++) {
            _triggerList.add(triggerArray[i]);
        }
    } //-- void setTrigger(org.opennms.netmgt.config.vacuumd.Trigger) 

    /**
     * Method setTrigger
     * 
     * Sets the value of 'trigger' by copying the given ArrayList.
     * 
     * @param triggerCollection the Vector to copy.
     */
    public void setTrigger(java.util.ArrayList triggerCollection)
    {
        //-- copy collection
        _triggerList.clear();
        for (int i = 0; i < triggerCollection.size(); i++) {
            _triggerList.add((org.opennms.netmgt.config.vacuumd.Trigger)triggerCollection.get(i));
        }
    } //-- void setTrigger(java.util.ArrayList) 

    /**
     * Method setTriggerCollection
     * 
     * Sets the value of 'trigger' by setting it to the given
     * ArrayList. No type checking is performed.
     * 
     * @param triggerCollection the ArrayList to copy.
     */
    public void setTriggerCollection(java.util.ArrayList triggerCollection)
    {
        _triggerList = triggerCollection;
    } //-- void setTriggerCollection(java.util.ArrayList) 

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
        return (org.opennms.netmgt.config.vacuumd.Triggers) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.Triggers.class, reader);
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
