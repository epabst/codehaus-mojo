package org.codehaus.mojo.was6.it;

import org.springframework.ejb.support.AbstractStatelessSessionBean;
import javax.ejb.CreateException;

public class TestBean extends AbstractStatelessSessionBean {

    @Override
    protected void onEjbCreate() throws CreateException {}
}