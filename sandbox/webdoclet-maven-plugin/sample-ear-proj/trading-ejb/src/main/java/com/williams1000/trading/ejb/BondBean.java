package com.williams1000.trading.ejb;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;


/**
 * Bond bean.
 * 
 * @ejb.bean view-type="local"
 *     name="Bond"
 *     display-name="Bond EB"
 *     description="represents a bond"
 *     type="CMP"
 *     cmp-version="2.x"
 *     local-jndi-name="ejb/trading/BondLocal"
 *     
 * @ejb.ejb-ref view-type="local"
 *     ejb-name="Coupon"
 *     ref-name="ejb/CouponLocal"
 *     
 * @ejb.finder
 *     signature="java.util.Collection findAll()"
 *     query="SELECT OBJECT (b) FROM Bond AS b"
 *     
 * @ejb.finder
 *     signature="java.util.Collection findByIssuer(java.lang.String issuer)"
 *     query="SELECT OBJECT (b) FROM Bond AS b WHERE b.issuer=?1"
 *     
 * @ejb.value-object
 *     name="Bond"
 *     match="*"
 */
public abstract class BondBean
    implements EntityBean
{
    /**
     * @ejb.interface-method view-type="local"
     * @ejb.persistence
     * @ejb.pk-field
     */
    public abstract String getId();

    /**
     */
    public abstract void setId( String id );

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.persistence
     */
    public abstract int getMaturity();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setMaturity( int maturity );

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.persistence
     */
    public abstract String getIssuer();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setIssuer( String issuer );

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.persistence
     */
    public abstract String getHolder();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setHolder( String holder );

    /**
     * @ejb.interface-method view-type="local"
     * 
     * @ejb.relation
     *     name="bond-coupons"
     *     role-name="bond-side"
     *     
     * @ejb.value-object
     *     compose="com.williams1000.trading.vo.CouponValue"
     *     compose-name="Coupon"
     *     members="com.williams1000.trading.ejb.CouponLocal"
     *     members-name="Coupon"
     *     relation="external"
     * @return
     */
    public abstract Collection getCoupons();

    /**
     * @ejb.interface-method view-type="local"
     * @param coupons
     */
    public abstract void setCoupons( Collection coupons );

    /**
     * @ejb.create-method
     */
    public BondPK ejbCreate( String id, int maturity, String issuer )
        throws CreateException
    {
        setId( id );
        setMaturity( maturity );
        setIssuer( issuer );
        return null;
    }
}
