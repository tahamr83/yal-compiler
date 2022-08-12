/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Parser;

/**
 *
 * @author Taha Zaidi
 */
public class tableObject {
    
    public String val;
    public int type;
    public int address;
    
    tableObject(String val,int type,int address)
    {
       this.val = val;
       this.type = type;
       this.address= address;
    }
}
