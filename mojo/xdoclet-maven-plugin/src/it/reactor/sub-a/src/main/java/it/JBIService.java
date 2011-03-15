package it;

/**
 * @jmx.mbean name="jboss.system:service=TestJBIContainer"
 *            extends="org.jboss.system.ServiceMBean"
 *
 */
public class JBIService {
    
    /**
     * @jmx.managed-attribute
     */
    public void setTransactionManager(String transactionManager) {
    }
    
    /**
     * @jmx.managed-attribute
     */
    public String getTransactionManager() {
        return null;
    }
    
    /**
     * @jmx.managed-operation
     */
    public void installArchive(String archive) {
    }
    
    /**
     * @jmx.managed-operation
     */
    public void uninstallArchive(String archive) {
    }
    
}
