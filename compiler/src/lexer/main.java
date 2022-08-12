/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import Parser.Parser;
import VirtualMachine.VirtualMachine;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.Exception;
import quadruple.quadruple;
/**
 *
 * @author Taha Zaidi
 */
public class main {
    public static void main(String[] args) throws FileNotFoundException, IOException, SyntaxException {
        FileInputStream FIS = new FileInputStream(new File("source.txt"));
        //FileOutputStream FOS = new FileOutputStream(new File("lex.txt"));
        PrintWriter writer = new PrintWriter("C:/Users/Taha Zaidi/Documents/NetBeansProjects/Lexer/lex.txt", "UTF-8");
        Lexer l = new Lexer (FIS);
        
        
        
        
        Parser p = new Parser (l);
        quadruple q=p.parse(),qNew= new quadruple();
        qNew.q=q.q.clone();
        qNew.bindings=q.bindings.clone();
        qNew.numMode=q.numMode.clone();
        qNew.q=q.q.clone();
        qNew.Icounter=q.Icounter;
        qNew.Scounter=q.Scounter;
        qNew.Mcounter=q.Mcounter;
        
        
        VirtualMachine VM= new VirtualMachine(new quadruple(q.q.clone(),q.scopes.clone(),q.numMode.clone(),q.bindings.clone(),q.type.clone(),q.Icounter,q.Scounter,q.Mcounter,q.Bcounter));
        VM.Execute();
        writer.close();
        
    }
}