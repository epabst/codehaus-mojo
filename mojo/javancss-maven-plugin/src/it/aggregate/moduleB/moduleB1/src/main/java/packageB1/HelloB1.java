package packageB1;

public class HelloB1
{
    public String helloB1( String name )
    {
        final String myName = name == null ? "world" : name;
        return "Hello " + myName + "!";
    }
}
