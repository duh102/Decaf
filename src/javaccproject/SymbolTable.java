/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaccproject;
import java.util.HashMap;

/**
 *
 * @author Lenovo
 */
public class SymbolTable { 
    SymbolTokenType type;       
    HashMap<Token, SymbolTable> table = new HashMap<Token, SymbolTable>();
        
    SymbolTable(){
        super();
    }
    SymbolTable(SymbolTokenType type){
        this.type=type;
    }

    void setToken(Token t, SymbolTokenType type) throws ParseException{
        if(table.containsKey(t)){
            throw new ParseException("Duplicate symbol");
        }else{
            table.put(t, new SymbolTable(type));
        }
    }
    public SymbolTokenType getTokenType(){
        return type;
    }
    public SymbolTable getSymbolTable(Token t){
        return table.get(t);        
    }
}
