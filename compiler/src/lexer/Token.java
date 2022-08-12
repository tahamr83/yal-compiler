/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author Taha Zaidi
 */
public class Token{
	public final int tag;
	public Token(int t){
		tag = t;
                
	}
    @Override
        public String toString()
        {
            
            if(tag < 255)
            {
            char ch=(char)tag;
            return Character.toString(ch);
            }
            return "<"+tag+",NULL>";
        }
}