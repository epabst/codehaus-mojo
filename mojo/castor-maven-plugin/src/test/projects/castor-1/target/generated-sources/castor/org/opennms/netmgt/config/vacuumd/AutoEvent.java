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
 * actions modify the database based on results of a
 *  trigger
 * 
 * @version $Revision$ $Date$
 */
public class AutoEvent implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _fields
     */
    private java.lang.String _fields;

    /**
     * Must be a UEI defined in
     *  event-conf.xml
     */
    private org.opennms.netmgt.config.vacuumd.Uei _uei;


      //----------------/
     //- Constructors -/
    //----------------/

    public AutoEvent() {
        super();
    } //-- org.opennms.netmgt.config.vacuumd.AutoEvent()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'fields'.
     * 
     * @return String
     * @return the value of field 'fields'.
     */
    public java.lang.String getFields()
    {
        return this._fields;
    } //-- java.lang.String getFields() 

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
     * Returns the value of field 'uei'. The field 'uei' has the
     * following description: Must be a UEI defined in
     *  event-conf.xml
     * 
     * @return Uei
     * @return the value of field 'uei'.
     */
    public org.opennms.netmgt.config.vacuumd.Uei getUei()
    {
        return this._uei;
    } //-- org.opennms.netmgt.config.vacuumd.Uei getUei() 

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
     * Sets the value of field 'fields'.
     * 
     * @param fields the value of field 'fields'.
     */
    public void setFields(java.lang.String fields)
    {
        this._fields = fields;
    } //-- void setFields(java.lang.String) 

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
     * Sets the value of field 'uei'. The field 'uei' has the
     * following description: Must be a UEI defined in
     *  event-conf.xml
     * 
     * @param uei the value of field 'uei'.
     */
    public void setUei(org.opennms.netmgt.config.vacuumd.Uei uei)
    {
        this._uei = uei;
    } //-- void setUei(org.opennms.netmgt.config.vacuumd.Uei) 

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
        return (org.opennms.netmgt.config.vacuumd.AutoEvent) Unmarshaller.unmarshal(org.opennms.netmgt.config.vacuumd.AutoEvent.class, reader);
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
