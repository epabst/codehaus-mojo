package org.codehaus.mojo.buildinfo.util;

public class BuildInfoConstructionException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    public BuildInfoConstructionException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public BuildInfoConstructionException( String message )
    {
        super( message );
    }

}
