package com.williams1000.trading.ejb;

import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;


/**
 * Login bean.
 *
 * @ejb.bean view-type="remote"
 *     name="TradeEntry"
 *     display-name="TradeEntry SB"
 *     description="Handles login"
 *     type="Stateful"
 *     jndi-name="ejb/trading/TradeEntry"
 */
public abstract class TradeEntryBean implements SessionBean {
	private SessionContext sessionContext;

	/**
	 * @ejb.ejb-ref view-type="local"
 	 *     ejb-name="Bond"
 	 *     ref-name="ejb/BondLocal"
 	 *     
 	 * note that we return the Home interface for entity beans because we sometimes need its methods eg createXXX and findXXX
 	 * note that for stateless session beans we return the component interface because we rarely need home (ie return BondLocal)
 	 * note that declaring ejb-ref at class level just adds ejb-ref to dd but nothing else
	 */
	protected abstract BondLocalHome getBondLocalHome();
    
    /**
     * @ejb.ejb-ref view-type="local"
     *     ejb-name="Coupon"
     *     ref-name="ejb/CouponLocal"
     * @return
     */
    protected abstract CouponLocalHome getCouponLocalHome();
	
	/**
	 * @ejb.env-entry
	 *     name="counter"
	 *     value="34"
	 * @return
	 */
	protected abstract int getCounter();

	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}
	
	/**
	 * Login to the service.
	 * @param username
	 * @param password
	 * 
	 * @ejb.interface-method 
	 */
	public void login(String username, String password) {
	}
	
	/**
	 * @ejb.interface-method
	 * @param id TODO
	 * @throws CreateException
	 */
	public void createBond(String id) throws CreateException {
		getBondLocalHome().create(id, 10, "New York Municiples");
	}
    
    /**
     * @ejb.interface-method
     * @param id
     * @throws CreateException
     */
    public void createCoupon(String id) throws CreateException {
        getCouponLocalHome().create(id, 44f);
    }
    
    /**
     * @ejb.interface-method
     * @throws FinderException
     * @throws RemoveException 
     * @throws EJBException 
     */
    public void removeAllBonds() throws FinderException, EJBException, RemoveException {
        Iterator allBonds = getBondLocalHome().findAll().iterator();
        while ( allBonds.hasNext() )
        {
            BondLocal bond = (BondLocal) allBonds.next();
            bond.remove();
        }
    }
	
	/**
	 * @ejb.interface-method
	 */
	public int getBookmarkCount() {
		return 1;
	}

	public void ejbCreate() {
		ejbActivate();
	}
	
	public void ejbRemove() {
		ejbPassivate();
	}

	public void ejbActivate() {
	}

	public void ejbPassivate() {
	}

}
