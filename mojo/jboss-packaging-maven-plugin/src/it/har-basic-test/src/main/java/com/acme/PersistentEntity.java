package com.acme;

import java.io.Serializable;

/**
 * Sample entity class, used to demonstrate har packaging.
 * 
 * @author batkinson
 * 
 */
public class PersistentEntity
    implements Serializable
{

    private Long id;

    private String name;

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

}
