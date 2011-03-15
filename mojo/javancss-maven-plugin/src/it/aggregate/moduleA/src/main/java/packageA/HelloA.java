package packageA;

public class HelloA
{
    public String helloA( String name )
    {
        final String myName = name == null ? "world" : name;
        return "Hello " + myName + "!";
    }
}
