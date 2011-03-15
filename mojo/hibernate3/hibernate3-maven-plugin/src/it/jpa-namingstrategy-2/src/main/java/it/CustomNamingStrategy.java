package it;

import org.hibernate.cfg.EJB3NamingStrategy;

public class CustomNamingStrategy
    extends EJB3NamingStrategy
{
    @Override
    public String classToTableName( String className )
    {
        return "tbl_" + super.classToTableName( className );
    }
}
