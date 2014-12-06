package javaccproject;

import java.util.ArrayList;

public class leftToRightScope{
    public ArrayList<String> currentlyScannedThisScope;
    public SymbolTable equivalent;
    
    public leftToRightScope(SymbolTable s){
        equivalent = s;
        currentlyScannedThisScope = new ArrayList<String>();
    }
}
