package it;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class PersonDao
    extends HibernateDaoSupport
{
    public List findPersons()
    {
        return getHibernateTemplate().loadAll( Person.class );
    }
}