package quadruple;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Taha Zaidi
 */
public class quadruple {
    
    public static final boolean NUMBER_MODE=true,ADDRESS_MODE=false,RESULT_BIND=true,op1Bind=false,localScope=true,globalScope=false,
           CHAR_TYPE=true,INT_TYPE=false ;
    
    
    public int [][] q ;
    public boolean [][] scopes;//true = local , false = global
    public boolean [][] numMode;//true=number mode,false addressing mode
    public boolean [] bindings;//bind with op3(result) == true , bind with op1 is false
    public boolean [] type;
    
    public int Icounter=0;
   public int Scounter=0;
    public int Mcounter=0;
    public int Bcounter=0;
    public quadruple()
    {
        q = new int [1000][4];
        scopes = new boolean [1000][3];
        numMode = new boolean [1000][2];
        bindings = new boolean [1000];
        type = new boolean [1000];
        q.clone();
    }
    
    public quadruple(int [][] q,boolean[][] scopes,boolean [][] numMode,boolean [] binding,boolean [] t,int IC,int SC,int MC,int BC )
    {
        this.q=q;
        this.scopes=scopes;
        this.numMode=numMode;
        this.bindings=binding;
        type=t;
        this.Icounter=IC;
        this.Scounter=SC;
        this.Bcounter=BC;
        this.Mcounter=MC;
        
    }
    
    public void addInstruction(int operation,int operand1,int operand2,int result)
    {
       q[Icounter][0]=operation;
       q[Icounter][1]=operand1;
       q[Icounter][2]=operand2;
       q[Icounter][3]=result;
       type[Icounter]=INT_TYPE;
       ++Icounter;
       
    }
    public void addInstruction(int operation,int operand1,int operand2,int result,boolean t)
    {
       q[Icounter][0]=operation;
       q[Icounter][1]=operand1;
       q[Icounter][2]=operand2;
       q[Icounter][3]=result;
       type[Icounter]=t;
       ++Icounter;
       
    }
    
    public void addScope(boolean op1,boolean op2,boolean result)
    {
        scopes[Scounter][0]=op1;
        scopes[Scounter][1]=op2;
        scopes[Scounter][2]=result;
        Scounter++;
    }
    
    public void addMode(boolean op1,boolean op2)
    {
        numMode[Mcounter][0]=op1;
        numMode[Mcounter][1]=op2;
        Mcounter++;

    }
    
    public void addBinding(boolean bind)
    {
        bindings[Bcounter]=bind;
        Bcounter++;
    }
    public void incMcounter()
    {
        Mcounter++;
    }
    public void incBcounter()
    {
        Bcounter++;
    }
    public void incScounter()
    {
        Scounter++;
    }
    
    
}
