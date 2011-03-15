package it;

/**
 * @ejb.bean
 *     name="bank/Account"
 *     type="CMP"
 *     jndi-name="ejb/bank/Account"
 *     local-jndi-name="ejb/bank/LocalAccount"
 *     primkey-field="id"
 *
 * @ejb.finder
 *     signature="java.util.Collection findAll()"
 *     unchecked="true"
 *
 * @ejb.transaction
 *     type="Required"
 *
 * @ejb.interface
 *     remote-class="test.interfaces.Account"
 *
 * @ejb.value-object
 *     match="*"
 */
public class SimpleBean {

}
