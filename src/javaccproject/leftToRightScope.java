package javaccproject;

import java.util.ArrayList;
import java.util.Iterator;
import javaccproject.tokens.FormalArgVarDeclToken;
import javaccproject.tokens.Token;

public class leftToRightScope{
    public ArrayList<String> currentlyScannedThisScope;
    public SymbolTable equivalent;
    
    public leftToRightScope(SymbolTable s){
        equivalent = s;
        currentlyScannedThisScope = new ArrayList<String>();
        //add formal arguments to arraylist
        Iterator<String> iter = s.table.keySet().iterator();
        while(iter.hasNext()){
            String key = (String) iter.next();
            Token t = s.getToken(key);
            if(t instanceof FormalArgVarDeclToken){
                currentlyScannedThisScope.add(t.symbolTableKey());
            }
        }
    }
}