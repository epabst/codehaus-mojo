package it;

import java.io.Serializable;

public class Pet
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