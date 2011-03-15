package org.codehaus.mojo.jlint;

public class JlintMessageItem
{
    private String type;

    private String category;

    private String uniqueMessagePattern;

    private String priority;

    public JlintMessageItem( String[] message )
    {
        setCategory( message[1] );
        setType( message[2] );
        setUniqueMessagePattern( message[4] );
        setPriority( message[0] );
    }

    public JlintMessageItem( JlintMessageItem item )
    {
        setCategory( item.getCategory() );
        setType( item.getType() );
        setUniqueMessagePattern( item.getUniqueMessagePattern() );
        setPriority( item.getPriority() );
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    public void setUniqueMessagePattern( String uniqueMessagePattern )
    {
        this.uniqueMessagePattern = uniqueMessagePattern;
    }

    public void setPriority( String priority )
    {
        this.priority = priority;
    }

    public String getType()
    {
        return type;
    }

    public String getCategory()
    {
        return category;
    }

    public String getUniqueMessagePattern()
    {
        return uniqueMessagePattern;
    }

    public String getPriority()
    {
        return priority;
    }

    public String toString()
    {
        return "JlintMessageItem: Priority [" + getPriority() + "] Category [" + getCategory() + "] Type [" + getType()
            + "].";
    }
}
