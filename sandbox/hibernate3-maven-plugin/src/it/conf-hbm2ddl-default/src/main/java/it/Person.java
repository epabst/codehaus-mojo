package it;

import java.io.Serializable;

public class Person
    implements Serializable
{
    private Long id;

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }
}