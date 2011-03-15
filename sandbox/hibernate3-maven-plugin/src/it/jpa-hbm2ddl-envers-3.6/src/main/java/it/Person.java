package it;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import java.io.Serializable;

@Entity
@Audited
public class Person
    implements Serializable
{
// ------------------------------ FIELDS ------------------------------

    @Id
    private Long id;

    @Column
    private String name;

// --------------------- GETTER / SETTER METHODS ---------------------

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