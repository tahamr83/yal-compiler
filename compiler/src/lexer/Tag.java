/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author Taha Zaidi
 */
public class Tag {
	public final static int
		NUM = 256,
		ID = 257,
		INTEGER = 258,
		CHARACTER = 259,
		REL = 260,
                BEGIN=270,
                WHILE=271
                ,END=272,
                IF=273,
                FUNCTION=274,
                RETURN=275,
                RETURNS=276,
                AsOp=277,
                AsCmp=279,
                VOID = 280,
                INPUT=281,
                PRINT=282,
                LABEL=283,
                ELSE=284,
                MAIN=285,
                HALT=286,
                RET=287,
                CALL=288,
                GT=289,
                LT=290,
                GTE=291,
                LTE=292,
                EQ=293,
                MUL=294,
                ADD=295,
                SUB=296,
                DIV=297,
                GOTO=298;
        
       public static int getRelTag(String rel)
       {
           if(rel.equals("=<"))
               return LTE;
           else if (rel.equals("=>"))
               return GTE;
           else if (rel.equals("<"))
               return LT;
           else if (rel.equals(">"))
               return GT;
           else 
               return EQ;
       }
        
}