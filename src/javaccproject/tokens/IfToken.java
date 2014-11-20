package javaccproject.tokens;

import javaccproject.SimpleNode;
import javaccproject.SymbolTable;

public class IfToken extends Token
{

    /**
     * 
     */
    private static final long serialVersionUID = 7204484457773720753L;
    public SimpleNode booleanExp;
    
    public IfToken(Token myToken) {
        super(myToken);
        myContext = new SymbolTable();
        myContext.tableOf = this;
    }
    
    public String toString()
    {
        return String.format("if %d", beginLine);
    }
    
    public String symbolTableKey() {
        return String.format("if%d", beginLine);
    }
    
    public static String symbolTableKey(int beginLine){
        return String.format("if%d", beginLine);
    }
}
