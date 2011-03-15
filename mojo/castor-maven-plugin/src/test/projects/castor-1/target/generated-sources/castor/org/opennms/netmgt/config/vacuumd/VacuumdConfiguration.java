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
 * Top-level element for the vacuumd-configuration.xml
 *  configuration file.
 * 
 * @version $Revision$ $Date$
 */
public class VacuumdConfiguration implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * how often to vacuum the database in
     *  seconds
     */
    private int _period;

    /**
     * keeps track of state for field: _period
     */
    private boolean _has_period;

    /**
     * This represents the SQL that is performed every
     *  <period> seconds
     */
    private java.util.ArrayList _statementList;

    /**
     * Field _automations
     */
    private org.opennms.netmgt.config.vacuumd.Automations _automations;

    /**
     * A collection of triggers
     */
    private org.opennms.netmgt.config.vacuumd.Triggers _triggers;

    /**
     * A collection of actions
     */
    private org.opennms.netmgt.config.vacuumd.Actions _actions;

    /**
     * Field _autoEvents
     */
    private org.opennms.netmgt.config.vacuumd.AutoEvents _autoEvents;


      //----------------/
     //- Constructors -/
    //----------------/

    public VacuumdConfiguration() {
        super();
        _statementList = new ArrayList();
    } //-- org.opennms.netmgt.config.vacuumd.VacuumdConfiguration()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addStatement
     * 
     * 
     * 
     * @param vStatement
     */
    public void addStatement(org.opennms.netmgt.config.vacuumd.Statement vStatement)
        throws java.lang.IndexOutOfBoundsException
    {
        _statementList.add(vStatement);
    } //-- void addStatement(org.opennms.netmgt.config.vacuumd.Statement) 

    /**
     * Method addStatement
     * 
     * 
     * 
     * @param index
     * @param vStatement
     */
    public void addStatement(int index, org.opennms.netmgt.config.vacuumd.Statement vStatement)
        throws java.lang.IndexOutOfBoundsException
    {
        _statementList.add(index, vStatement);
    } //-- void addStatement(int, org.opennms.netmgt.config.vacuumd.Statement) 

    /**
     * Method clearStatement
     * 
     */
    public void clearStatement()
    {
        _statementList.clear();
    } //-- void clearStatement() 

    /**
     * Method deletePeriod
     * 
     */
    public void deletePeriod()
    {
        this._has_period= false;
    } //-- void deletePeriod() 

    /**
     * Method enumerateStatement
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateStatement()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_statementList.iterator());
    } //-- java.util.Enumeration enumerateStatement() 

    /**
     * Returns the value of field 'actions'. The field 'actions'
     * has the following description: A collection of actions
     * 
     * @return Actions
     * @return the value of field 'actions'.
     */
    public org.opennms.netmgt.config.vacuumd.Actions getActions()
    {
        return this._actions;
    } //-- org.opennms.netmgt.config.vacuumd.Actions getActions() 

    /**
     * Returns the value of field 'autoEvents'.
     * 
     * @return AutoEvents
     * @return the value of field 'autoEvents'.
     */
    public org.opennms.netmgt.config.vacuumd.AutoEvents getAutoEvents()
    {
        return this._autoEvents;
    } //-- org.opennms.netmgt.config.vacuumd.AutoEvents getAutoEvents() 

    /**
     * Returns the value of field 'automations'.
     * 
     * @return Automations
     * @return the value of field 'automations'.
     */
    public org.opennms.netmgt.config.vacuumd.Automations getAutomations()
    {
        return this._automations;
    } //-- org.opennms.netmgt.config.vacuumd.Automations getAutomations() 

    /**
     * Returns the value of field 'period'. The field 'period' has
     * the following description: how often to vacuum the database
     * in
     *  seconds
     * 
     * @return int
     * @return the value of field 'period'.
     */
    public int getPeriod()
    {
        return this._period;
    } //-- int getPeriod() 

    /**
     * Method getStatement
     * 
     * 
     * 
     * @param index
     * @return Statement
     */
    public org.opennms.netmgt.config.vacuumd.Statement getStatement(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _statementList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.opennms.netmgt.config.vacuumd.Statement) _statementList.get(index);
    } //-- org.opennms.netmgt.config.vacuumd.Statement getStatement(int) 

    /**
     * Method getStatement
     * 
     * 
     * 
     * @return Statement
     */
    public org.opennms.netmgt.config.vacuumd.Statement[] getStatement()
    {
        int size = _statementList.size();
        org.opennms.netmgt.config.vacuumd.Statement[] mArray = new org.opennms.netmgt.config.vacuumd.Statement[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.opennms.netmgt.config.vacuumd.Statement) _statementList.get(index);
        }
        return mArray;
    } //-- org.opennms.netmgt.config.vacuumd.Statement[] getStatement() 

    /**
     * Method getStatementCollection
     * 
     * Returns a reference to 'statement'. No type checking is
     * performed on any modications to the Collection.
     * 
     * @return ArrayList
     * @return returns a reference to the Collection.
     */
    public java.util.ArrayList getStatementCollection()
    {
        return _statementList;
    } //-- java.util.ArrayList getStatementCollection() 

    /**
     * Method getStatementCount
     * 
     * 
     * 
     * @return int
     */
    public int getStatementCount()
    {
        return _statementList.size();
    } //-- int getStatementCount() 

    /**
     * Returns the value of field 'triggers'. The field 'triggers'
     * has the following description: A collection of triggers
     * 
     * @return Triggers
     * @return the value of field 'triggers'.
     */
    public org.opennms.netmgt.config.vacuumd.Triggers getTriggers()
    {
        return this._triggers;
    } //-- org.opennms.netmgt.config.vacuumd.Triggers getTriggers() 

    /**
     * Method hasPeriod
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasPeriod()
    {
        return this._has_period;
    } //-- boolean hasPeriod() 

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
     * Method removeStatement
     * 
     * 
     * 
     * @param vStatement
     * @return boolean
     */
    public boolean removeStatement(org.opennms.netmgt.config.vacuumd.Statement vStatement)
    {
        boolean removed = _statementList.remove(vStatement);
        return removed;
    } //-- boolean removeStatement(org.opennms.netmgt.config.vacuumd.Statement) 

    /**
     * Sets the value of field 'actions'. The field 'actions' has
     * the following description: A collection of actions
     * 
     * @param actions the value of field 'actions'.
     */
    public void setActions(org.opennms.netmgt.config.vacuumd.Actions actions)
    {
        this._actions = actions;
    } //-- void setActions(org.opennms.netmgt.config.vacuumd.Actions) 

    /**
     * Sets the value of field 'autoEvents'.
     * 
     * @param autoEvents the value of field 'autoEvents'.
     */
    public void setAutoEvents(org.opennms.netmgt.config.vacuumd.AutoEvents autoEvents)
    {
        this._autoEvents = autoEvents;
    } //-- void setAutoEvents(org.opennms.netmgt.config.vacuumd.AutoEvents) 

    /**
     * Sets the value of field 'automations'.
     * 
     * @param automations the value of field 'automations'.
     */
    public void setAutomations(org.opennms.netmgt.config.vacuumd.Automations automations)
    {
        this._automations = automations;
    } //-- void setAutomations(org.opennms.netmgt.config.vacuumd.Automations) 

    /**
     * Sets the value of field 'period'. The field 'period' has the
     * following description: how often to vacuum the database in
     *  seconds
     * 
     * @param period the value of field 'period'.
     */
    public void setPeriod(int period)
    {
        this._period = period;
        this._has_period = true;
    } //-- void setPeriod(int) 

    /**
     * Method setStatement
     * 
     * 
     * 
     * @param index
     * @param vStatement
     */
    public void setStatement(int index, org.opennms.netmgt.config.vacuumd.Statement vStatement)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _statementList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _statementList.set(index, vStatement);
    } //-- void setStatement(int, org.opennms.netmgt.config.vacuumd.Statement) 

    /**
     * Method setStatement
     * 
     * 
     * 
     * @param statementArray
     */
    public void setStatement(org.opennms.netmgt.config.vacuumd.Statement[] statementArray)
    {
        //-- copy array
        _statementList.clear();
        for (int i = 0; i < statementArray.length; i++) {
            _statementList.add(statementArray[i]);
        }
    } //-- void setStatement(org.opennms.netmgt.config.vacuumd.Statement) 

    /**
     * Method setStatement
     * 
     * Sets the value of 'statement' by copying the given
     * ArrayList.
     * 
     * @param statementCollection the Vector to copy.
     */
    public void setStatement(java.util.ArrayList statementCollection)
    {
        //-- copy collection
        _statementList.clear();
        for (int i = 0; i < statementCollection.size(); i++) {
            _statementList.add((org.opennms.netmgt.config.vacuumd.Statement)statementCollection.get(i));
        }
    } //-- void setStatement(java.util.ArrayList) 

    /**
     * Method setStatementCollection
     * 
     * Sets the value of 'statement' by setting it to the given
     * ArrayList. No type checking is performed.
     * 
     * @param statementCollection the ArrayList to copy.
     */
    public void setStatementCollection(java.util.ArrayList statementCollection)
    {
        _statementList = statementCollection;
    } //-- void setStatementCollection(java.util.ArrayList) 

    /**
     * Sets the value of field 'triggers'. The field 'triggers' has
     * the following description: A collection of triggers
     * 
     * @param triggers the value of field 'triggers'.
     */
    public void setTriggers(org.opennms.netmgt.config.vacuumd.Triggers triggers)
    {
        this._triggers = triggers;
    } //-- void setTriggers(org.opennms.netmgt.config.vacuumd.Triggers) 

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
        return (org.opennms.netmgt.config.vacuumd.VacuumdConfiguration) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.VacuumdConfiguration.class, reader);
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
