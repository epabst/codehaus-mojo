package org.codehaus.mojo.was6;

import java.util.ArrayList;

import junit.framework.TestCase;

public class RoleTest extends TestCase {

    private String roleName = "myRole";

    public void testGetRoleMapping_Everyone() {
        Role role = new Role(roleName, true, false, null, null);
        String expected = " {\\\"" + roleName + "\\\" Yes No \\\"\\\" \\\"\\\"} ";

        assertEquals(expected, role.getRoleMapping());
    }

    public void testGetRoleMapping_AllAuth() {
        Role role = new Role(roleName, false, true, null, null);
        String expected = " {\\\"" + roleName + "\\\" No Yes \\\"\\\" \\\"\\\"} ";

        assertEquals(expected, role.getRoleMapping());
    }

    public void testGetRoleMapping_SingleGroup() {
        String groupName = "myGroup";
        ArrayList groups = new ArrayList();
        groups.add(groupName);

        Role role = new Role(roleName, false, false, null, groups);
        String expected = " {\\\"" + roleName + "\\\" No No \\\"\\\" \\\"" + groupName + "\\\"} ";

        assertEquals(expected, role.getRoleMapping());
    }

    public void testGetRoleMapping_MultipleGroups() {
        String groupName1 = "myGroup1";
        String groupName2 = "myGroup2";
        ArrayList groups = new ArrayList();
        groups.add(groupName1);
        groups.add(groupName2);

        assertEquals("should be right number of groups", 2, groups.size());

        Role role = new Role(roleName, false, false, null, groups);
        String expected = " {\\\"" + roleName + "\\\" No No \\\"\\\" \\\"" + groupName1 + "|" + groupName2 + "\\\"} ";

        assertEquals(expected, role.getRoleMapping());
    }

    public void testGetRoleMapping_SingleUser() {
        String userName = "myUserName";
        ArrayList users = new ArrayList();
        users.add(userName);

        Role role = new Role(roleName, false, false, users, null);
        String expected = " {\\\"" + roleName + "\\\" No No \\\"" + userName + "\\\" \\\"\\\"} ";

        assertEquals(expected, role.getRoleMapping());
    }

    public void testGetRoleMapping_MultipleUsers() {
        String userName1 = "myUser1";
        String userName2 = "myUser2";
        ArrayList users = new ArrayList();
        users.add(userName1);
        users.add(userName2);

        assertEquals("should be right number of users", 2, users.size());

        Role role = new Role(roleName, false, false, users, null);
        String expected = " {\\\"" + roleName + "\\\" No No \\\"" + userName1 + "|" + userName2 + "\\\" \\\"\\\"} ";

        assertEquals(expected, role.getRoleMapping());
    }

}
