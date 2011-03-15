package packageB2;

public class HelloB2
{
    public String helloB2( String name )
    {
        final String myName = name == null ? "world" : name;
        return "Hello " + myName + "!";
    }
}
