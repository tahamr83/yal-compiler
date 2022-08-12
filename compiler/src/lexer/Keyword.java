/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author Taha Zaidi
 */
public class Keyword extends Word{
    
    Keyword(int t , String s)
    {
        super(t,s);
    }
    
    @Override
        public String toString()
        {
            return "<"+lexeme+", NULL>";
        }
    
}
