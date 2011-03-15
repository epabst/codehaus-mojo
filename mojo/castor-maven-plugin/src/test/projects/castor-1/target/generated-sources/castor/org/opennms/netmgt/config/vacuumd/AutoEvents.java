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
 * Class AutoEvents.
 * 
 * @version $Revision$ $Date$
 */
public class AutoEvents implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * actions modify the database based on results of a
     *  trigger
     */
    private java.util.ArrayList _autoEventList;


      //----------------/
     //- Constructors -/
    //----------------/

    public AutoEvents() {
        super();
        _autoEventList = new ArrayList();
    } //-- org.opennms.netmgt.config.vacuumd.AutoEvents()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAutoEvent
     * 
     * 
     * 
     * @param vAutoEvent
     */
    public void addAutoEvent(org.opennms.netmgt.config.vacuumd.AutoEvent vAutoEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _autoEventList.add(vAutoEvent);
    } //-- void addAutoEvent(org.opennms.netmgt.config.vacuumd.AutoEvent) 

    /**
     * Method addAutoEvent
     * 
     * 
     * 
     * @param index
     * @param vAutoEvent
     */
    public void addAutoEvent(int index, org.opennms.netmgt.config.vacuumd.AutoEvent vAutoEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        _autoEventList.add(index, vAutoEvent);
    } //-- void addAutoEvent(int, org.opennms.netmgt.config.vacuumd.AutoEvent) 

    /**
     * Method clearAutoEvent
     * 
     */
    public void clearAutoEvent()
    {
        _autoEventList.clear();
    } //-- void clearAutoEvent() 

    /**
     * Method enumerateAutoEvent
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateAutoEvent()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_autoEventList.iterator());
    } //-- java.util.Enumeration enumerateAutoEvent() 

    /**
     * Method getAutoEvent
     * 
     * 
     * 
     * @param index
     * @return AutoEvent
     */
    public org.opennms.netmgt.config.vacuumd.AutoEvent getAutoEvent(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _autoEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.opennms.netmgt.config.vacuumd.AutoEvent) _autoEventList.get(index);
    } //-- org.opennms.netmgt.config.vacuumd.AutoEvent getAutoEvent(int) 

    /**
     * Method getAutoEvent
     * 
     * 
     * 
     * @return AutoEvent
     */
    public org.opennms.netmgt.config.vacuumd.AutoEvent[] getAutoEvent()
    {
        int size = _autoEventList.size();
        org.opennms.netmgt.config.vacuumd.AutoEvent[] mArray = new org.opennms.netmgt.config.vacuumd.AutoEvent[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.opennms.netmgt.config.vacuumd.AutoEvent) _autoEventList.get(index);
        }
        return mArray;
    } //-- org.opennms.netmgt.config.vacuumd.AutoEvent[] getAutoEvent() 

    /**
     * Method getAutoEventCollection
     * 
     * Returns a reference to 'autoEvent'. No type checking is
     * performed on any modications to the Collection.
     * 
     * @return ArrayList
     * @return returns a reference to the Collection.
     */
    public java.util.ArrayList getAutoEventCollection()
    {
        return _autoEventList;
    } //-- java.util.ArrayList getAutoEventCollection() 

    /**
     * Method getAutoEventCount
     * 
     * 
     * 
     * @return int
     */
    public int getAutoEventCount()
    {
        return _autoEventList.size();
    } //-- int getAutoEventCount() 

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
     * Method removeAutoEvent
     * 
     * 
     * 
     * @param vAutoEvent
     * @return boolean
     */
    public boolean removeAutoEvent(org.opennms.netmgt.config.vacuumd.AutoEvent vAutoEvent)
    {
        boolean removed = _autoEventList.remove(vAutoEvent);
        return removed;
    } //-- boolean removeAutoEvent(org.opennms.netmgt.config.vacuumd.AutoEvent) 

    /**
     * Method setAutoEvent
     * 
     * 
     * 
     * @param index
     * @param vAutoEvent
     */
    public void setAutoEvent(int index, org.opennms.netmgt.config.vacuumd.AutoEvent vAutoEvent)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _autoEventList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _autoEventList.set(index, vAutoEvent);
    } //-- void setAutoEvent(int, org.opennms.netmgt.config.vacuumd.AutoEvent) 

    /**
     * Method setAutoEvent
     * 
     * 
     * 
     * @param autoEventArray
     */
    public void setAutoEvent(org.opennms.netmgt.config.vacuumd.AutoEvent[] autoEventArray)
    {
        //-- copy array
        _autoEventList.clear();
        for (int i = 0; i < autoEventArray.length; i++) {
            _autoEventList.add(autoEventArray[i]);
        }
    } //-- void setAutoEvent(org.opennms.netmgt.config.vacuumd.AutoEvent) 

    /**
     * Method setAutoEvent
     * 
     * Sets the value of 'autoEvent' by copying the given
     * ArrayList.
     * 
     * @param autoEventCollection the Vector to copy.
     */
    public void setAutoEvent(java.util.ArrayList autoEventCollection)
    {
        //-- copy collection
        _autoEventList.clear();
        for (int i = 0; i < autoEventCollection.size(); i++) {
            _autoEventList.add((org.opennms.netmgt.config.vacuumd.AutoEvent)autoEventCollection.get(i));
        }
    } //-- void setAutoEvent(java.util.ArrayList) 

    /**
     * Method setAutoEventCollection
     * 
     * Sets the value of 'autoEvent' by setting it to the given
     * ArrayList. No type checking is performed.
     * 
     * @param autoEventCollection the ArrayList to copy.
     */
    public void setAutoEventCollection(java.util.ArrayList autoEventCollection)
    {
        _autoEventList = autoEventCollection;
    } //-- void setAutoEventCollection(java.util.ArrayList) 

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
        return (org.opennms.netmgt.config.vacuumd.AutoEvents) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.AutoEvents.class, reader);
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
