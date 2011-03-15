package hello;

import java.text.MessageFormat;

public class Hello
{
    private static final String PATTERN;
    
    static {
        PATTERN = "Hello {0}!";
    }
    
    public String hello( String name )
    {
        final String myName = name == null ? "world" : name;
        return MessageFormat.format(PATTERN, new Object[] { myName });
    }
}
