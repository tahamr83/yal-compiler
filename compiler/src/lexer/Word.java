/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author Taha Zaidi
 */
public class Word extends Token{
	public final String lexeme;
	public Word(int t, String s){
		super(t);
		lexeme = new String(s);
	}
        public String toString()
        {
            return "<Identifier"+","+lexeme+">";
        }
}
