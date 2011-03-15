/*
 * Created on 23/08/2004
 */
package org.apache.maven.plugin.hibernate.test;

/**
 * @author CameronBraid
 */
public class PersonEntity {

    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    private String name = null;
    public void setName(String name) {
        this.name = name;
    }
}
