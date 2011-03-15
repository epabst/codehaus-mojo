package hello;

import java.util.Vector;

public class Hello
{
    @Deprecated
    public static final String SYS_PROPERTY_LOADBALANCER = "s";

    private Map<String, Enum<?>[]> enumerations = new HashMap<String, Enum<?>[]>(0);

    public String hello( String name )
    {
        @SuppressWarnings("unused")
        int notUsedVariable = 2;

        Enumeration enum = new Vector().elements();

        final String myName = name == null ? "world" : name;
        return "Hello " + myName + "!";
    }
}
