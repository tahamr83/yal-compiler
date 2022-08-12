/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author Taha Zaidi
 */
public class Num extends Token {
	public final float value;
        
	public Num(float v){
		super(Tag.NUM);
		value = v;
         
	}
        public String toString()
        {
            return "<Number"+","+value+">";
        }
        public float getValue()
        {
            return value;
        }
        
}