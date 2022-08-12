/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author Taha Zaidi
 */
public class Rel extends Token{
	public final String lexeme;
	public Rel(String s){
		super(Tag.REL);
		lexeme = new String(s);
	}
        public String toString()
        {
            return "<Rel"+","+lexeme+">";
        }
}