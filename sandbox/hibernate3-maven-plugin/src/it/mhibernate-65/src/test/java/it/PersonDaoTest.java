package it;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class PersonDaoTest
    extends AbstractDependencyInjectionSpringContextTests
{
// ------------------------------ FIELDS ------------------------------

    // this instance will be (automatically) dependency injected
    private PersonDao personDao;

// -------------------------- OTHER METHODS --------------------------

    // specifies the Spring configuration to load for this test fixture
    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:/applicationContext.xml"};
    }

    // a setter method to enable DI of the 'personDao' instance variable
    public void setTitleDao( PersonDao personDao )
    {
        this.personDao = personDao;
    }

    public void testFindPersons()
        throws Exception
    {
        int count = personDao.findPersons().size();
        assertEquals( 2, count );
    }
}
