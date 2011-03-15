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
 * A collection of actions
 * 
 * @version $Revision$ $Date$
 */
public class Actions implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * actions modify the database based on results of a
     *  trigger
     */
    private java.util.ArrayList _actionList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Actions() {
        super();
        _actionList = new ArrayList();
    } //-- org.opennms.netmgt.config.vacuumd.Actions()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAction
     * 
     * 
     * 
     * @param vAction
     */
    public void addAction(org.opennms.netmgt.config.vacuumd.Action vAction)
        throws java.lang.IndexOutOfBoundsException
    {
        _actionList.add(vAction);
    } //-- void addAction(org.opennms.netmgt.config.vacuumd.Action) 

    /**
     * Method addAction
     * 
     * 
     * 
     * @param index
     * @param vAction
     */
    public void addAction(int index, org.opennms.netmgt.config.vacuumd.Action vAction)
        throws java.lang.IndexOutOfBoundsException
    {
        _actionList.add(index, vAction);
    } //-- void addAction(int, org.opennms.netmgt.config.vacuumd.Action) 

    /**
     * Method clearAction
     * 
     */
    public void clearAction()
    {
        _actionList.clear();
    } //-- void clearAction() 

    /**
     * Method enumerateAction
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateAction()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_actionList.iterator());
    } //-- java.util.Enumeration enumerateAction() 

    /**
     * Method getAction
     * 
     * 
     * 
     * @param index
     * @return Action
     */
    public org.opennms.netmgt.config.vacuumd.Action getAction(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _actionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.opennms.netmgt.config.vacuumd.Action) _actionList.get(index);
    } //-- org.opennms.netmgt.config.vacuumd.Action getAction(int) 

    /**
     * Method getAction
     * 
     * 
     * 
     * @return Action
     */
    public org.opennms.netmgt.config.vacuumd.Action[] getAction()
    {
        int size = _actionList.size();
        org.opennms.netmgt.config.vacuumd.Action[] mArray = new org.opennms.netmgt.config.vacuumd.Action[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.opennms.netmgt.config.vacuumd.Action) _actionList.get(index);
        }
        return mArray;
    } //-- org.opennms.netmgt.config.vacuumd.Action[] getAction() 

    /**
     * Method getActionCollection
     * 
     * Returns a reference to 'action'. No type checking is
     * performed on any modications to the Collection.
     * 
     * @return ArrayList
     * @return returns a reference to the Collection.
     */
    public java.util.ArrayList getActionCollection()
    {
        return _actionList;
    } //-- java.util.ArrayList getActionCollection() 

    /**
     * Method getActionCount
     * 
     * 
     * 
     * @return int
     */
    public int getActionCount()
    {
        return _actionList.size();
    } //-- int getActionCount() 

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
     * Method removeAction
     * 
     * 
     * 
     * @param vAction
     * @return boolean
     */
    public boolean removeAction(org.opennms.netmgt.config.vacuumd.Action vAction)
    {
        boolean removed = _actionList.remove(vAction);
        return removed;
    } //-- boolean removeAction(org.opennms.netmgt.config.vacuumd.Action) 

    /**
     * Method setAction
     * 
     * 
     * 
     * @param index
     * @param vAction
     */
    public void setAction(int index, org.opennms.netmgt.config.vacuumd.Action vAction)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _actionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _actionList.set(index, vAction);
    } //-- void setAction(int, org.opennms.netmgt.config.vacuumd.Action) 

    /**
     * Method setAction
     * 
     * 
     * 
     * @param actionArray
     */
    public void setAction(org.opennms.netmgt.config.vacuumd.Action[] actionArray)
    {
        //-- copy array
        _actionList.clear();
        for (int i = 0; i < actionArray.length; i++) {
            _actionList.add(actionArray[i]);
        }
    } //-- void setAction(org.opennms.netmgt.config.vacuumd.Action) 

    /**
     * Method setAction
     * 
     * Sets the value of 'action' by copying the given ArrayList.
     * 
     * @param actionCollection the Vector to copy.
     */
    public void setAction(java.util.ArrayList actionCollection)
    {
        //-- copy collection
        _actionList.clear();
        for (int i = 0; i < actionCollection.size(); i++) {
            _actionList.add((org.opennms.netmgt.config.vacuumd.Action)actionCollection.get(i));
        }
    } //-- void setAction(java.util.ArrayList) 

    /**
     * Method setActionCollection
     * 
     * Sets the value of 'action' by setting it to the given
     * ArrayList. No type checking is performed.
     * 
     * @param actionCollection the ArrayList to copy.
     */
    public void setActionCollection(java.util.ArrayList actionCollection)
    {
        _actionList = actionCollection;
    } //-- void setActionCollection(java.util.ArrayList) 

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
        return (org.opennms.netmgt.config.vacuumd.Actions) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.Actions.class, reader);
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
