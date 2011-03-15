package mypackage;

/**
 *
 * @author Author 1
 * @author Author 2, <a href="mailto:foo@bar.com">Email Author</a>
 * @see Interface2
 * @see TopLevelClass, java.lang.Object
 */
public class FooClass
    extends TopLevelClass
    implements Interface1, Interface2
{
    private int privateInt;
    protected int protectedInt;
    public int publicInt;

    public int bleh;

    public FooClass( int big, long bang )
    {
    }

    protected FooClass( Integer hoola, Long hoop )
    {
    }

    public void setBleh( int bleh )
    {
        this.bleh = bleh;
    }

    public int getBleh()
    {
        return bleh;
    }

    public void publicMethod()
    {
    }

    void packageMethod()
    {
    }

    protected void protectedMethod()
    {
    }

    private void privateMethod()
    {
    }
}
