package org.codehaus.mojo.was6;

import java.util.Iterator;
import java.util.List;

/**
 * This is used to for when an application is being installed to map a role name
 * in an application to a container group / user.
 * 
 * @author Jim Sellers
 * @since 2009-08-06
 */
public class Role {

    /** Default constructor used by maven. */
    public Role() {
    }

    /**
     * Constructor used by the unit tests.
     * 
     * @param roleName
     * @param everyone
     * @param allAuth
     * @param users
     * @param groups
     */
    public Role(String roleName, boolean everyone, boolean allAuth, List users, List groups) {
        this.roleName = roleName;
        this.everyone = everyone;
        this.allAuthenticated = allAuth;
        this.users = users;
        this.groups = groups;
    }

    /**
     * The name of the role in the application.
     * 
     * @parameter
     * @required
     */
    private String roleName;

    /**
     * If we want to map this role to everyone.
     * 
     * @parameter default-value="false"
     */
    private boolean everyone;

    /**
     * If this role should be mapped to all authenticated users.
     * 
     * @parameter default-value="false"
     */
    private boolean allAuthenticated;

    /**
     * The list of user names that we want to map to this role.
     * 
     * @parameter
     */
    private List users;

    /**
     * The list of group names that we want to map to this role.
     * 
     * @parameter
     */
    private List groups;

    /**
     * In the form from <a href=
     * "http://www.ibm.com/developerworks/websphere/techjournal/0309_apte/apte.html"
     * >IBM techjournal</a>.
     * 
     * @return This will return a string in the form:
     *         <code>{role, role.everyone, role.all.auth.user, role.user, role.group}</code>
     *         to be used with the <code>MapRolesToUsers</code>.
     */
    public String getRoleMapping() {
        StringBuffer buff = new StringBuffer();

        buff.append(" {");

        // role name
        addQuote(buff);
        buff.append(roleName);
        addQuote(buff);

        addSpace(buff);
        addBoolean(buff, everyone);

        addSpace(buff);
        addBoolean(buff, allAuthenticated);

        addSpace(buff);
        addList(buff, users);

        addSpace(buff);
        addList(buff, groups);

        buff.append("} ");

        return buff.toString();
    }

    private void addQuote(StringBuffer buff) {
        buff.append("\\\"");
    }

    private void addSpace(StringBuffer buff) {
        buff.append(" ");
    }

    private void addBoolean(StringBuffer buff, boolean value) {
        if (value) {
            buff.append("Yes");
        } else {
            buff.append("No");
        }
    }

    private void addList(StringBuffer buff, List list) {
        addQuote(buff);
        if (null != list) {
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                buff.append(iterator.next());

                // if there's more, add a pipe as a separator
                if (iterator.hasNext()) {
                    buff.append("|");
                }
            }
        }
        addQuote(buff);
    }

}
