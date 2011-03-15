package it;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Entity
public class Person
    implements Serializable
{
// ------------------------------ FIELDS ------------------------------

    @Id
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> resources;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public List<String> getResources()
    {
        return resources;
    }

    public void setResources( List<String> resources )
    {
        this.resources = resources;
    }
}