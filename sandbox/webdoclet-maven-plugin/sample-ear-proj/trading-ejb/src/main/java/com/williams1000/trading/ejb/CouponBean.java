package com.williams1000.trading.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

import com.williams1000.trading.vo.CouponValue;

/**
 * @ejb.bean view-type = "local"
 *     name = "Coupon"
 *     display-name = "Coupon EB"
 *     type = "CMP"
 *     cmp-version = "2.x"
 *     local-jndi-name = "ejb/trading/CouponLocal"
 *     
 * @ejb.ejb-ref view-type="local"
 *     ejb-name="Bond"
 *     ref-name="ejb/BondLocal"
 *     
 * @ejb.value-object
 *     name="Coupon"
 *     match="*"
 */
public abstract class CouponBean
    implements EntityBean
{
    /**
     * @ejb.interface-method view-type = "local"
     * @ejb.persistence
     * @ejb.pk-field
     * @return
     * 
     * note that ejb.pk-field must be combined with ejb.persistence field
     * note that marking many fields as ejb.pk-field results in a composite key
     */
    public abstract String getId();

    /**
     * note don't expose setId on the remote interface
     */
    public abstract void setId( String id );

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation
     *     name="bond-coupons"
     *     role-name="coupon-side"
     *     cascade-delete="yes"
     *     
     * @jboss.relation
     *     related-pk-field = "id"
     *     fk-column = "bond_fk"
     *     
     * @return
     */
    public abstract BondLocal getBond();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setBond( BondLocal bond );

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.persistence
     * @return
     */
    public abstract float getRate();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setRate( float rate );

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract CouponValue getCouponValue();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setCouponValue( CouponValue value );

    /**
     * @ejb.create-method
     */
    public CouponPK ejbCreate( String id, float rate )
        throws CreateException
    {
        setId( id );
        setRate( rate );
        return null;
    }

    /**
     * @ejb.create-method
     */
    public CouponPK ejbCreate( CouponValue value )
        throws CreateException
    {
        //        setId( id );
        //        setRate( rate );
        return null;
    }
}
