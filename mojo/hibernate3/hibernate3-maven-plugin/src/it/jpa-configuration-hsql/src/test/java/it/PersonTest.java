package it;

import junit.framework.TestCase;
import org.hibernate.ejb.Ejb3Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

public class PersonTest
    extends TestCase
{
// ------------------------------ FIELDS ------------------------------

    protected Ejb3Configuration configuration;

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void setUp()
        throws Exception
    {
        if ( configuration == null )
        {
            configuration = new Ejb3Configuration();
            configuration.configure( "ejb3test", new HashMap() );
        }
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
        configuration = null;
    }

    public void testPersist1()
    {
        EntityManagerFactory emf = configuration.buildEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        Person person = new Person();
        person.setId( (long) 1 );

        try
        {
            em.getTransaction().begin();
            em.persist( person );
            em.getTransaction().commit();
        }
        catch ( IllegalStateException e )
        {
            fail( e.getMessage() );
        }
        finally
        {
            if ( em.getTransaction() != null && em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
            em.close();
            emf.close();
        }
    }

    public void testPersist2()
    {
        EntityManagerFactory emf = configuration.buildEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        Person person = new Person();
        person.setId( (long) 2 );

        try
        {
            em.getTransaction().begin();
            em.persist( person );
            em.getTransaction().commit();
        }
        catch ( IllegalStateException e )
        {
            fail( e.getMessage() );
        }
        finally
        {
            if ( em.getTransaction() != null && em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
            em.close();
            emf.close();
        }
    }

    public void testPersist3()
    {
        EntityManagerFactory emf = configuration.buildEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        Person person = new Person();
        person.setId( (long) 3 );

        try
        {
            em.getTransaction().begin();
            em.persist( person );
            em.getTransaction().commit();
        }
        catch ( IllegalStateException e )
        {
            fail( e.getMessage() );
        }
        finally
        {
            if ( em.getTransaction() != null && em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
            em.close();
            emf.close();
        }
    }
}
