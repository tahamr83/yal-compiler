package Parser;



import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import lexer.Lexer;
import lexer.Num;
import lexer.SyntaxException;
import lexer.Tag;
import lexer.Token;
import lexer.Word;
import Parser.tableObject;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexer.Rel;
import quadruple.quadruple;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Taha Zaidi
 */
public class Parser 
{
    
     Token lookAhead;
     Lexer lex;
     int tabs =0;
    int tempCounter=0;
    int address=0;
    int instruction_counter=0;
    int labelCounter =0;
    StringBuffer tac = new StringBuffer() ;
    Hashtable<String,tableObject> globalSymbolTable = new Hashtable<String,tableObject>();
    Hashtable<String,tableObject> currentSymbolTable;
    Hashtable<String,Hashtable<String,tableObject>> FunctionSymbolTables = new Hashtable<String,Hashtable<String,tableObject>>();
    quadruple quad=new quadruple();
    //A hashtable which has symbol tables of functions
     
    public Parser(Lexer l)
    {
        lex=l;
        currentSymbolTable = globalSymbolTable;
    }
    
    void match(int tag) throws IOException, SyntaxException 
    {
        if(lookAhead.tag == tag)
            nextToken();
        else 
            throw new SyntaxException();
       
    }
    
    
    public  quadruple parse() throws IOException, SyntaxException 
    {
         emit("goto main");
         quad.addInstruction(Tag.GOTO, 0, 0, 0);
         quad.incBcounter();
         quad.incMcounter();
         quad.incScounter();
         
         
         nextToken();
         DeclStmts();
         funcDecl();
         currentSymbolTable = globalSymbolTable;
         
        if(lookAhead.tag == Tag.MAIN)
        {
            match(Tag.MAIN);
            match((int)'(');
            match((int)')');
            match(Tag.BEGIN);
            globalSymbolTable.put("main",new tableObject("main",Tag.LABEL,instruction_counter));
            tac.append("main:");
            Statements();
            match(Tag.END);
        }
        else
        {
            System.out.println("Main function not found ");
            throw new SyntaxException();
        }
        emit("halt");
        quad.addInstruction(Tag.HALT, 0, 0, 0);
        quad.incScounter();
        quad.incMcounter();
        quad.incBcounter();
        
        System.out.println(tac);
        printTAC();
        
        quad.q[0][1]=globalSymbolTable.get("main").address;
        return quad;
    }
    
    
    
    public void Statements() throws IOException, SyntaxException
    {
       
        switch(lookAhead.tag)
        {
            case Tag.ID:
                String id_lexeme=((Word)lookAhead).lexeme;
                if(isFunction(id_lexeme))
                {
                    funcCall();
                }
                else
                {
                    assignStmt();
                }
                match((int)';');
                Statements();
                break;
                
            case Tag.INTEGER :
                DeclStmt();
                match((int)';');
                Statements();
                break;
            case Tag.CHARACTER :
                DeclStmt();
                match((int)';');
                Statements();
                break;
                
            case Tag.IF:
                ifStmt();

                Statements();
                break;
             case Tag.WHILE:
                 whileStmt();
                 Statements();
                break;
                 
             case Tag.INPUT:
                 input();
                 match((int)';');
                 Statements();
                 break;
                 
             case Tag.PRINT:
                 print();
                 match((int)';');
                 Statements();
                 break;
        }
    }
    
    
    public void print() throws IOException, SyntaxException
    {
        String id_lexeme="";
        match(Tag.PRINT);
        match((int)'(');
        if(lookAhead.tag == Tag.ID)
        {
           id_lexeme= ((Word)lookAhead).lexeme;
            if(!isDeclared(id_lexeme))
            {
                System.out.println("undeclared identifier "+id_lexeme+" on line "+lex.getLineNumber());
                throw new SyntaxException();
            }
            match(Tag.ID);
        }
        match((int)')');
        emit("print "+id_lexeme);
        if(!isInt(id_lexeme))
        quad.addInstruction(Tag.PRINT,getRelativeAddress(id_lexeme), 0, 0,quadruple.CHAR_TYPE);
        else
            quad.addInstruction(Tag.PRINT,getRelativeAddress(id_lexeme), 0, 0);
        quad.addMode(isNumeric(id_lexeme), true);
        quad.addScope(getScope(id_lexeme), true, true);
        quad.incBcounter();
    }
    
    public void input() throws IOException, SyntaxException
    {
        String id_lexeme="";
        match(Tag.INPUT);
        match((int)'(');
        if(lookAhead.tag == Tag.ID)
        {
           id_lexeme= ((Word)lookAhead).lexeme;
            if(!isDeclared(id_lexeme))
            {
                System.out.println("undeclared identifier "+id_lexeme+" on line "+lex.getLineNumber());
                throw new SyntaxException();
            }
            match(Tag.ID);
        }
        
        match((int)')');
        emit("input "+id_lexeme);
        if(!isInt(id_lexeme))
        quad.addInstruction(Tag.INPUT,getRelativeAddress(id_lexeme), 0, 0,quadruple.CHAR_TYPE);
        else
            quad.addInstruction(Tag.INPUT,getRelativeAddress(id_lexeme), 0, 0);
        quad.addMode(isNumeric(id_lexeme), true);
        quad.addScope(getScope(id_lexeme), true, true);
        quad.incBcounter();
    }
    
    public void funcCall() throws IOException, SyntaxException
    {
        String id_lexeme=((Word)lookAhead  ).lexeme;
        match(Tag.ID);
        match((int)'(');
        match((int)')');
        emit("CALL "+id_lexeme);
        quad.addInstruction(Tag.CALL,getRelativeAddress(id_lexeme), 0, 0);
        quad.addMode(true, true);
        quad.addScope(true, true, true);
        quad.incBcounter();
        
    }
    
    public void ifStmt() throws IOException, SyntaxException
    {
        String id1_lexeme="",id2_lexeme="",rel,trueLabel,falseLabel;
        trueLabel=newLabel();
        falseLabel=newLabel();
        match(Tag.IF);
        match((int)'(');
        if(lookAhead.tag == Tag.ID)
        {
            id1_lexeme = ((Word)lookAhead).lexeme;
            match(Tag.ID);
        }
        else
        {
            throw new SyntaxException();
        }
         if(lookAhead.tag == Tag.REL)
        {
            rel = ((Rel)lookAhead).lexeme;
            match(Tag.REL);
        }
         else
        {
            throw new SyntaxException();
        }
         if(lookAhead.tag == Tag.ID)
        {
            id2_lexeme = ((Word)lookAhead).lexeme;
            match(Tag.ID);
        }
        match((int)')');
        globalSymbolTable.put(trueLabel,new tableObject(trueLabel,Tag.LABEL,instruction_counter+2));
        emit("if "+id1_lexeme+rel+id2_lexeme+" goto "+trueLabel);
        //***************************************************** QUADRUPLE
        quad.addInstruction(Tag.getRelTag(rel), getRelativeAddress(id1_lexeme), getRelativeAddress(id2_lexeme), getRelativeAddress(trueLabel));
        quad.addScope(getScope(id1_lexeme), getScope(id2_lexeme), quadruple.globalScope);
        quad.addMode(isNumeric(id1_lexeme), isNumeric(id2_lexeme));
        quad.incBcounter();
        //*****************************************************
        emit("goto "+falseLabel);
        int falseIndex=quad.Icounter;
        quad.addInstruction(Tag.GOTO,0,0,0);
        quad.incBcounter();
        quad.incMcounter();
        quad.incScounter();
        
        //*****************************************************
        tac.append(trueLabel+':');
        
        match(Tag.BEGIN);
        Statements();
        match(Tag.END);
        tac.append(falseLabel+':');
        globalSymbolTable.put(falseLabel,new tableObject(trueLabel,Tag.LABEL,instruction_counter));
        quad.q[falseIndex][1]=instruction_counter;
        optionalElse();

    }
    
    
    public void whileStmt() throws IOException, SyntaxException
    {
        String id1_lexeme="",id2_lexeme="",rel,trueLabel,falseLabel,whileLabel;
        trueLabel=newLabel();
        falseLabel=newLabel();
        whileLabel=newLabel();
        match(Tag.WHILE);
         match((int)'(');
        if(lookAhead.tag == Tag.ID)
        {
            id1_lexeme = ((Word)lookAhead).lexeme;
            match(Tag.ID);
        }
        else
        {
            throw new SyntaxException();
        }
         if(lookAhead.tag == Tag.REL)
        {
            rel = ((Rel)lookAhead).lexeme;
            match(Tag.REL);
        }
         else
        {
            throw new SyntaxException();
        }
         if(lookAhead.tag == Tag.ID)
        {
            id2_lexeme = ((Word)lookAhead).lexeme;
            match(Tag.ID);
        }
        match((int)')');
        globalSymbolTable.put(trueLabel,new tableObject(trueLabel,Tag.LABEL,instruction_counter+2));
        tac.append(whileLabel+':');
        globalSymbolTable.put(whileLabel,new tableObject(trueLabel,Tag.LABEL,instruction_counter));
        emit("if "+id1_lexeme+rel+id2_lexeme+" goto "+trueLabel);
        //***************************************************** QUADRUPLE
        quad.addInstruction(Tag.getRelTag(rel), getRelativeAddress(id1_lexeme), getRelativeAddress(id2_lexeme), getRelativeAddress(trueLabel));
        quad.addScope(getScope(id1_lexeme), getScope(id2_lexeme), quadruple.globalScope);
        quad.addMode(isNumeric(id1_lexeme), isNumeric(id2_lexeme));
        quad.incBcounter();
        //*****************************************************
         emit("goto "+falseLabel);
         int falseIndex=quad.Icounter;
         quad.addInstruction(Tag.GOTO,0,0,0);
        quad.incBcounter();
        quad.incMcounter();
        quad.incScounter();
        //*****************************
        tac.append(trueLabel+':');
        
        match(Tag.BEGIN);
        Statements();
        match(Tag.END);
        emit("goto "+whileLabel);
        quad.addInstruction(Tag.GOTO,getRelativeAddress(whileLabel),0,0);
        quad.incBcounter();
        quad.incMcounter();
        quad.incScounter();
        //***********************************************************
        tac.append(falseLabel+':');
        globalSymbolTable.put(falseLabel,new tableObject(falseLabel,Tag.LABEL,instruction_counter));
        quad.q[falseIndex][1]=instruction_counter;
    }
    
    public void optionalElse() throws IOException, SyntaxException
    {
         if(lookAhead.tag == Tag.ELSE)
         {
             match(Tag.ELSE);
             match(Tag.BEGIN);
             Statements();
             match(Tag.END);
         }
        else;
    }
    
    public void DeclStmts() throws IOException, SyntaxException//DeclStmts --> DeclStmt;DeclStmt | ^
    {
        if(lookAhead.tag == Tag.INTEGER || lookAhead.tag == Tag.CHARACTER)
        {
         DeclStmt();
         match((int)';');
         DeclStmts();
        }
        else;
    }
    
    public void funcDecl() throws IOException, SyntaxException
    {
        int Paddress=address;//saving previous address
        address=0;
        if(lookAhead.tag == Tag.FUNCTION)
        {
            match(Tag.FUNCTION);
            if(lookAhead.tag == Tag.ID)
            {
                String funcName=((Word)lookAhead).lexeme;
                if(FunctionSymbolTables.get(funcName)== null)//No symbol table for this function
                {
                    FunctionSymbolTables.put(funcName, new Hashtable<String,tableObject>());
                    currentSymbolTable=FunctionSymbolTables.get(funcName);
                   
                }
                else
                {
                    System.out.println("Error function redefinition");
                }
                match(Tag.ID);
                match((int)'(');
                match((int)')');
                globalSymbolTable.put(funcName, new tableObject(funcName,Tag.FUNCTION,instruction_counter));
                match(Tag.BEGIN);
                tac.append(funcName+":");
                Statements();
                address=Paddress;//Restoring previous address
                match(Tag.END);
                emit("ret");
                quad.addInstruction(Tag.RET, 0, 0, 0);
                quad.incBcounter();
                quad.incMcounter();;
                quad.incScounter();;
                
                funcDecl();
            }
            
        }
    }
    
    public void DeclStmt() throws IOException, SyntaxException
    {
        String id;
        switch(lookAhead.tag)
        {
            case Tag.INTEGER:
                match(Tag.INTEGER);
                id=((Word)lookAhead).lexeme;          
                
                if(lookAhead.tag == Tag.ID)
                {
                    
                    allocate(id,4,Tag.INTEGER);
                    match(Tag.ID);
                    
                    if(lookAhead.tag == (int)'[')
                    {
                        match((int)'[');
                        if(lookAhead.tag == Tag.NUM)
                        {
                            int size=(int)((Num)lookAhead).value;
                            address=address+(4*(size-1));
                            match(Tag.NUM);
                            match((int)']');
                        }
                        else
                        {
                            System.out.println("Error . Only constant values for array size");
                            throw new SyntaxException();
                            
                        }
                    }
                    
                }
                break;
            case Tag.CHARACTER:
                match(Tag.CHARACTER);
                id=((Word)lookAhead).lexeme;
                if(lookAhead.tag == Tag.ID)
                {
                    
                    allocate(id,2,Tag.CHARACTER);
                    match(Tag.ID);
                }
               
        }
       
    }
    
     @SuppressWarnings("empty-statement")
    public void assignStmt() throws IOException, SyntaxException
    {
        String id_lexeme="",E_addr="";
        char ch;
         if(lookAhead.tag == Tag.ID)
         { 
               id_lexeme = ((Word)lookAhead).lexeme;
               if(!isDeclared(id_lexeme))
               {
                   if(!isDeclared(id_lexeme,globalSymbolTable))
                   {
                        System.out.println("undeclared identifier '"+id_lexeme+"'");
                        throw new SyntaxException();
                   }
               }
               match(Tag.ID);
               id_lexeme=OptSubs(id_lexeme,false);
               match(Tag.AsOp);
               if(lookAhead.tag == (int)'\'')//if character literal
               {
                   match((int)'\'');
                   if( (((Word)lookAhead).lexeme.toCharArray()).length >1 )
                   {
                       System.out.println("Character literal not specified properly");
                       throw new SyntaxException();
                       
                   }
                   ch = (((Word)lookAhead).lexeme.toCharArray())[0];
                   match(Tag.ID);
                   emit(id_lexeme+'='+'\''+ch+'\'');
                   //************************ADDING TO QUADRUPLE
                   
                   quad.addInstruction(Tag.AsOp, (int)ch, tabs, getRelativeAddress(id_lexeme),quadruple.CHAR_TYPE);
                   quad.incBcounter();
                   quad.incMcounter();
                   quad.incScounter();
                   //*************************
                   match((int)'\'');
                }
               else
               {
               E_addr=E();
               emit(id_lexeme+'='+E_addr);
               //******************************************************
               //Adding assignment operation in the quadruple
                 addAssign2Quad(id_lexeme, E_addr);
               //******************************************************
               }
         }
         else
             return ;
    }
    
    public String OptSubs(String id_lex,boolean flag) throws IOException, SyntaxException
    {
        String E_addr="";
        if(lookAhead.tag == (int)'[')
        {
            match((int)'[');
            E_addr=E();
            match((int)']');
            if(flag == false)
            return id_lex+'['+E_addr+']';
            else 
            {
                String t;
                t=newTemp();
                emit(t+'='+id_lex+'['+E_addr+']');
                addAssign2Quad(t, id_lex+'['+E_addr+']');
                return t;
            }
            
        }
        else
            ;
        return id_lex;
        
    }
   
    public String E() throws IOException, SyntaxException
    {
        String E_addr="",E_dash_i;
        
        E_dash_i=T();//calculating inhertited attribute for for E_dash
        
        E_addr=E_dash(E_dash_i);//passing the inherited attribute from parent E
        
        return E_addr;
    }
    
    public String T() throws IOException, SyntaxException
    {
        String T_addr="",T_dash_i;
        
        T_dash_i=F();//calculating inhertited attribute for for T_dash
        
        T_addr=T_dash(T_dash_i);//passing the inherited attribute from parent T
        
        return T_addr;
    }
    
    public String F() throws IOException, SyntaxException
    {
        String addr,id_lexeme="",E_addr;
        switch(lookAhead.tag)
        {
            case Tag.ID:
                id_lexeme=((Word)lookAhead).lexeme;
                
                if(!isDeclared(id_lexeme))
               {
                   if(!isDeclared(id_lexeme,globalSymbolTable))
                   {
                        System.out.println("undeclared identifier '"+id_lexeme+"'");
                        throw new SyntaxException();
                   }
               }
                
                match(Tag.ID);
                id_lexeme=OptSubs(id_lexeme,true);
                addr=id_lexeme;
                break;
                
            case Tag.NUM:
               id_lexeme= String.valueOf((int)((Num)lookAhead).value);
                match(Tag.NUM);
                addr=id_lexeme;
                break;
            
            case (int)'(':
                 match((int)')');
                E_addr=E();
                match((int)')');
                addr=E_addr;
                break;
            default:
                addr="";
        }
        
        return addr;
    }
    
    public String T_dash(String T_dash_i) throws IOException, SyntaxException
    {
        String T_dash_addr,F_addr,T1_dash_i;
        if(lookAhead.tag == (int)'*')
                
        {    match((int)'*');
                F_addr=F();
                T1_dash_i= newTemp();
                emit(T1_dash_i+'='+T_dash_i+'*'+F_addr);
                addArith2quad(Tag.MUL,T1_dash_i,T_dash_i,F_addr);
                
                T_dash_addr=T_dash(T1_dash_i);//the T1 call
                
               
        }
        
         else if (lookAhead.tag == (int)'/')
        {
            match((int)'/');
                F_addr=F();
                T1_dash_i= newTemp();
                emit(T1_dash_i+'='+T_dash_i+'/'+F_addr);
                addArith2quad(Tag.DIV,T1_dash_i,T_dash_i,F_addr);
                T_dash_addr=T_dash(T1_dash_i);//the T1 call
        }
        
        else 
        {
            T_dash_addr=T_dash_i;
        }
         return T_dash_addr;
    }
    
    public String E_dash(String E_dash_i) throws IOException, SyntaxException
    {
        String E_dash_addr,T_addr,E1_dash_i;
        if(lookAhead.tag == (int)'+')
                
        {    match((int)'+');
                T_addr=T();
                E1_dash_i= newTemp();
                emit(E1_dash_i+'='+E_dash_i+'+'+T_addr);
                addArith2quad(Tag.ADD,E1_dash_i,E_dash_i,T_addr);
                E_dash_addr=E_dash(E1_dash_i);//the E1 call
                
               
        }
        
        else if (lookAhead.tag == (int)'-')
        {
            match((int)'-');
                T_addr=T();
                E1_dash_i= newTemp();
                emit(E1_dash_i+'='+E_dash_i+'-'+T_addr);
                addArith2quad(Tag.SUB,E1_dash_i,E_dash_i,T_addr);
                E_dash_addr=E_dash(E1_dash_i);//the E1 call
        }
        else
        {
            E_dash_addr=E_dash_i;
        }
         return E_dash_addr;
    }
    private Token  nextToken() throws IOException, SyntaxException
    {
        
        return lookAhead=(Token)lex.scan();
    }
    
    public void printTabs()
    {
        for(int i=0;i<tabs;++i)
        System.out.println("\t");
    }
    
    public void emit (String code) throws IOException 
    {
       /* PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter("interCode.txt", true));
            writer.println(code);
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }*/
        
        tac.append(instruction_counter+") "+code+';'+'\n');
        instruction_counter++;
    }
    
    public String newTemp()
    {
        String temp="t"+ (tempCounter++);
        currentSymbolTable.put(temp, new tableObject(temp,Tag.INTEGER,address));
        
        return temp;
    }
    
    public String newLabel()
    {
        String label="L"+ (labelCounter++);
        
        
        return label;
    }
    
    
    public boolean isDeclared(String id)
    {
         tableObject o= (tableObject)currentSymbolTable.get(id);//
          if(o == null)//if identifier not in symbol table
          {
              return false;
          }
          return true;
    }
    public boolean isDeclared(String id,Hashtable symbolTable)
    {
         tableObject o= (tableObject)symbolTable.get(id);//
          if(o == null)//if identifier not in symbol table
          {
              return false;
          }
          return true;
    }
    
    public boolean allocate(String id,int size,int type)
    {
     
     
                    if(!isDeclared(id))//if identifier not in symbol table
                    {
                        currentSymbolTable.put(id, new tableObject(id,type,address));
                        address= address+size;
                        return true;
                    }
                    else 
                    {
                        System.out.println("redeclaration of identifier '" + id +"'");
                        return false;
                    }
    }
    
    boolean isFunction(String identifier)
    {
        
            Hashtable<String,tableObject> ht = FunctionSymbolTables.get(identifier);
            if(ht != null)
                return true;
        
        return false;
    }
    
    int getRelativeAddress(String id)
    {
        if(isNumeric(id))
            return Integer.parseInt(id);
        if(currentSymbolTable.get(id) != null)//The call is from a function
        {
             return currentSymbolTable.get(id).address;
        }
        return globalSymbolTable.get(id).address;
    }
    
    boolean getScope(String id)
    {
        if(isNumeric(id))
            return quadruple.localScope;
        if(currentSymbolTable != globalSymbolTable)//The call is from a function
        {
            if(currentSymbolTable.get(id) != null)//if found in function's symbol table
                return true;//then local
            return false;//else global
        }
        return false;//the call is not from within a function . as the identifier is valid . so it must be global
    }
    
    
    public boolean isNumeric(String str)
{
    for (char c : str.toCharArray())
    {
        if (!Character.isDigit(c)) return false;
    }
    return true;
}
    
     void addAssign2Quad(String id_lexeme,String E_addr)
    {
        boolean bind;
        if(id_lexeme.indexOf('[') == -1 && E_addr.indexOf('[') == -1)//no array
        {
            bind=true;
            quad.addInstruction(Tag.AsOp, getRelativeAddress(E_addr), 0, getRelativeAddress(id_lexeme));
            quad.addScope(getScope(E_addr), quadruple.localScope, getScope(id_lexeme));
            quad.addMode(isNumeric(E_addr), quadruple.NUMBER_MODE);
            quad.addBinding(bind);
        }
        
        else if (id_lexeme.indexOf('[') != -1 && E_addr.indexOf('[') == -1)//bind with result i.e id.lexeme
        {
            bind=true;
            String op2 = id_lexeme;
            id_lexeme=id_lexeme.substring(0, id_lexeme.indexOf('['));
            op2 = op2.substring(op2.indexOf("[") + 1);
            op2 = op2.substring(0, op2.indexOf("]"));
            
            quad.addInstruction(Tag.AsOp, getRelativeAddress(E_addr), getRelativeAddress(op2), getRelativeAddress(id_lexeme));
            quad.addScope(getScope(E_addr), getScope(op2), getScope(id_lexeme));
            quad.addMode(isNumeric(E_addr), isNumeric(op2));
            quad.addBinding(quadruple.RESULT_BIND);
        }
        
        else
        {
            String op2 = E_addr;
            op2 = op2.substring(op2.indexOf("[") + 1);
            op2 = op2.substring(0, op2.indexOf("]"));
            E_addr=E_addr.substring(0, E_addr.indexOf('['));
            
            quad.addInstruction(Tag.AsOp, getRelativeAddress(E_addr), getRelativeAddress(op2), getRelativeAddress(id_lexeme));
            quad.addScope(getScope(E_addr), getScope(op2), getScope(id_lexeme));
            quad.addMode(isNumeric(E_addr), quadruple.ADDRESS_MODE);
            quad.addBinding(quadruple.op1Bind);
        }
    }
     
     
     
     void addArith2quad(int op,String result,String op1,String op2)
     {
         quad.addInstruction(op, getRelativeAddress(op1), getRelativeAddress(op2), getRelativeAddress(result));
            quad.addScope(getScope(op1), getScope(op2), getScope(result));
            quad.addMode(isNumeric(op1), isNumeric(op2));
            quad.addBinding(false);
     }
     
     public boolean isInt(String id)
     {
         if(currentSymbolTable != globalSymbolTable)//The call is from a function
        {
            if(currentSymbolTable.get(id).type == Tag.INTEGER)//if found in function's symbol table
                return true;//then local
            return false;//else global
        }
        return false;//the call is not from within a function . as the identifier is valid . so it must be global
     }
     
     void printTAC()
     {
         PrintWriter writer = null;
        try {
            writer = new PrintWriter("interCode.txt");
            writer.println(tac);
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
             Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
         } finally {
            writer.close();
     }
     }
}
