package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

public class Hello
{
    private final Logger log = LoggerFactory.getLogger( getClass() );

    public String hello( String name )
    {
        final String greetings = "Hello " + StringUtils.defaultIfEmpty( name, "world" ) + "!";
        log.info( "Greetings = {}", greetings );
        return greetings;
    }
}
