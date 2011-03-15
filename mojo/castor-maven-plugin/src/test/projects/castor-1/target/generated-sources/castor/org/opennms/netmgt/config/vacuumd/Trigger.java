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
 * A query to the database with a resultset used for
 *  actions
 * 
 * @version $Revision$ $Date$
 */
public class Trigger implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * only run the action if the row count evalutes with
     *  the operator (defaults to > 0)
     */
    private java.lang.String _operator;

    /**
     * Field _rowCount
     */
    private int _rowCount;

    /**
     * keeps track of state for field: _rowCount
     */
    private boolean _has_rowCount;

    /**
     * Just a generic string used for SQL
     *  statements
     */
    private org.opennms.netmgt.config.vacuumd.Statement _statement;


      //----------------/
     //- Constructors -/
    //----------------/

    public Trigger() {
        super();
    } //-- org.opennms.netmgt.config.vacuumd.Trigger()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteRowCount
     * 
     */
    public void deleteRowCount()
    {
        this._has_rowCount= false;
    } //-- void deleteRowCount() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Returns the value of field 'operator'. The field 'operator'
     * has the following description: only run the action if the
     * row count evalutes with
     *  the operator (defaults to > 0)
     * 
     * @return String
     * @return the value of field 'operator'.
     */
    public java.lang.String getOperator()
    {
        return this._operator;
    } //-- java.lang.String getOperator() 

    /**
     * Returns the value of field 'rowCount'.
     * 
     * @return int
     * @return the value of field 'rowCount'.
     */
    public int getRowCount()
    {
        return this._rowCount;
    } //-- int getRowCount() 

    /**
     * Returns the value of field 'statement'. The field
     * 'statement' has the following description: Just a generic
     * string used for SQL
     *  statements
     * 
     * @return Statement
     * @return the value of field 'statement'.
     */
    public org.opennms.netmgt.config.vacuumd.Statement getStatement()
    {
        return this._statement;
    } //-- org.opennms.netmgt.config.vacuumd.Statement getStatement() 

    /**
     * Method hasRowCount
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasRowCount()
    {
        return this._has_rowCount;
    } //-- boolean hasRowCount() 

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
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Sets the value of field 'operator'. The field 'operator' has
     * the following description: only run the action if the row
     * count evalutes with
     *  the operator (defaults to > 0)
     * 
     * @param operator the value of field 'operator'.
     */
    public void setOperator(java.lang.String operator)
    {
        this._operator = operator;
    } //-- void setOperator(java.lang.String) 

    /**
     * Sets the value of field 'rowCount'.
     * 
     * @param rowCount the value of field 'rowCount'.
     */
    public void setRowCount(int rowCount)
    {
        this._rowCount = rowCount;
        this._has_rowCount = true;
    } //-- void setRowCount(int) 

    /**
     * Sets the value of field 'statement'. The field 'statement'
     * has the following description: Just a generic string used
     * for SQL
     *  statements
     * 
     * @param statement the value of field 'statement'.
     */
    public void setStatement(org.opennms.netmgt.config.vacuumd.Statement statement)
    {
        this._statement = statement;
    } //-- void setStatement(org.opennms.netmgt.config.vacuumd.Statement) 

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
        return (org.opennms.netmgt.config.vacuumd.Trigger) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.Trigger.class, reader);
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
