/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package VirtualMachine;

import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.Stack;
import lexer.Tag;
import quadruple.quadruple;

/**
 *
 * @author Taha Zaidi
 */
public class VirtualMachine {
    quadruple quad;
    ByteBuffer ds = ByteBuffer.allocate(1000);
    int programCounter=0;
    
   public VirtualMachine(quadruple q)
    {
        quad=q;
    }
    
   public void Execute()
    {
        int result_address, operand1, operand2,tempInput;
        char ch;
        Scanner reader = new Scanner(System.in);
        Stack callStack = new Stack();
        Stack stack = new Stack();
        
        while(quad.q[programCounter][0] != Tag.HALT)
        {
            switch(quad.q[programCounter][0])
            {
                case Tag.GOTO:
                    programCounter=quad.q[programCounter][1];
                    break;
                    
                case Tag.ADD:
                    result_address=quad.q[programCounter][3];
                    operand1=quad.q[programCounter][1];
                    operand2=quad.q[programCounter][2];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    if(quad.numMode[programCounter][1] != quadruple.NUMBER_MODE)
                    {
                        operand2=ds.getInt(operand2);
                    }
                    ds.putInt( result_address,operand1+operand2);
                    programCounter++;
                    break;
                    
                    case Tag.DIV:
                    result_address=quad.q[programCounter][3];
                    operand1=quad.q[programCounter][1];
                    operand2=quad.q[programCounter][2];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    if(quad.numMode[programCounter][1] != quadruple.NUMBER_MODE)
                    {
                        operand2=ds.getInt(operand2);
                    }
                    ds.putInt( result_address,operand1+operand2);
                    programCounter++;
                    break;
                        
                    case Tag.MUL:
                    result_address=quad.q[programCounter][3];
                    operand1=quad.q[programCounter][1];
                    operand2=quad.q[programCounter][2];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    if(quad.numMode[programCounter][1] != quadruple.NUMBER_MODE)
                    {
                        operand2=ds.getInt(operand2);
                    }
                    ds.putInt( result_address,operand1*operand2);
                    programCounter++;
                    break;
                            
                    case Tag.SUB:
                    result_address=quad.q[programCounter][3];
                    operand1=quad.q[programCounter][1];
                    operand2=quad.q[programCounter][2];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    if(quad.numMode[programCounter][1] != quadruple.NUMBER_MODE)
                    {
                        operand2=ds.getInt(operand2);
                    }
                    ds.putInt( result_address,operand1-operand2);
                    programCounter++;
                    break;
                    
                case Tag.AsOp:
                    result_address=quad.q[programCounter][3];
                    if(quad.type[programCounter] == quadruple.CHAR_TYPE)
                    {
                        ch=(char)quad.q[programCounter][1];
                        ds.putChar(result_address, ch);
                    }
                    else
                    {
                    operand1=quad.q[programCounter][1];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    ds.putInt( result_address,operand1);
                    }
                    programCounter++;
                    
                    break;
                    
                case Tag.PRINT:
                    operand1=quad.q[programCounter][1];
                    if(quad.type[programCounter] == quadruple.CHAR_TYPE)
                    {
                        ch=ds.getChar(operand1);
                        System.out.println(ch);
                    }
                    else
                    {
                        if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                        {
                             operand1=ds.getInt(operand1);
                        }
                        System.out.println(operand1);
                    }
                    programCounter++;
                    break;
                
                case Tag.INPUT:
                    result_address=quad.q[programCounter][3];
                    tempInput=reader.nextInt();
                    ds.putInt( result_address,tempInput);
                    programCounter++;
                    break;    
                    
                case Tag.CALL:
                    stack.push(programCounter+1);
                    ByteBuffer tempDS = ByteBuffer.allocate(1000);
                    ds=tempDS;
                    callStack.push(tempDS);
                    programCounter=quad.q[programCounter][1];
                    break;
                    
                case Tag.RET:
                     programCounter=(int)stack.pop();
                     ds=(ByteBuffer)callStack.pop();
                    break;
                    
                case Tag.GT:
                    result_address=quad.q[programCounter][3];
                    operand1=quad.q[programCounter][1];
                    operand2=quad.q[programCounter][2];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    if(quad.numMode[programCounter][1] != quadruple.NUMBER_MODE)
                    {
                        operand2=ds.getInt(operand2);
                    }
                    if(operand1>operand2)
                    {
                        programCounter=result_address;
                        
                    }
                    else
                    {
                        programCounter++;
                    }
                    break;
                    
                    
                    case Tag.LT:
                    result_address=quad.q[programCounter][3];
                    operand1=quad.q[programCounter][1];
                    operand2=quad.q[programCounter][2];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    if(quad.numMode[programCounter][1] != quadruple.NUMBER_MODE)
                    {
                        operand2=ds.getInt(operand2);
                    }
                    if(operand1<operand2)
                    {
                        programCounter=result_address;
                        
                    }
                    else
                    {
                        programCounter++;
                    }
                    break;
                        
                    case Tag.GTE:
                    result_address=quad.q[programCounter][3];
                    operand1=quad.q[programCounter][1];
                    operand2=quad.q[programCounter][2];
                    if(quad.numMode[programCounter][0] != quadruple.NUMBER_MODE)
                    {
                        operand1=ds.getInt(operand1);
                    }
                    if(quad.numMode[programCounter][1] != quadruple.NUMBER_MODE)
                    {
                        operand2=ds.getInt(operand2);
                    }
                    if(operand1>=operand2)
                    {
                        programCounter=result_address;
                        
                    }
                    else
                    {
                        programCounter++;
                    }
                    break;
                        
                default:
            }
        }
        
    }
   
   
}
