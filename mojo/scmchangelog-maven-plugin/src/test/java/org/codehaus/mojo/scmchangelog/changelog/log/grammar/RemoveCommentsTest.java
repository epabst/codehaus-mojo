/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codehaus.mojo.scmchangelog.changelog.log.grammar;

import junit.framework.TestCase;
import org.codehaus.mojo.scmchangelog.changelog.log.Message;
import org.codehaus.mojo.scmchangelog.changelog.log.ScmGrammar;

/**
 *
 * @author ehsavoie
 */
public class RemoveCommentsTest extends TestCase {
    private ScmGrammar grammar = new AbstractScmGrammar()
    {
        public Message extractMessage( String content )
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        public boolean hasMessage( String content )
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        public String getIssueSeparator()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    };

    public void testRemoveSimpleComment()
    {
        String content = "Hello World /* Bonjour le Monde */ my friend";
        String result = grammar.removeComments( content );
        assertEquals( "Hello World  my friend", result );
      
        content = "Hello World /**/ my friend";
        result = grammar.removeComments( content );
        assertEquals( "Hello World  my friend", result );

    }

     public void testRemoveMultistarsComment()
    {
        String content = "Hello World /*** Bonjour le Monde ***/ my friend";
        String result = grammar.removeComments( content );
        assertEquals( "Hello World  my friend", result );
    }


    public void testRemoveMultipleComment()
    {
        String content = "Hello World /* Bonjour le Monde */ my friend /*mon ami*/";
        String result = grammar.removeComments( content );
        assertEquals( "Hello World  my friend ", result );
    }

    public void testRemoveNoComment()
    {
        String content = "Hello World my friend";
        String result = grammar.removeComments( content );
        assertEquals( "Hello World my friend", result );
      
        content = "Hello World my friend*/";
        result = grammar.removeComments( content );
        assertEquals( "Hello World my friend*/", result );

        content = "/*Hello World my friend";
        result = grammar.removeComments( content );
        assertEquals( "/*Hello World my friend", result );
    }

    public void testRemoveIncludedComments()
    {
        String content = "Hello World /* Bonjour le Monde /*mon ami*/   */my friend";
        String result = grammar.removeComments( content );
        assertEquals( "Hello World    */my friend", result );
    }
}
