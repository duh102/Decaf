package javaccproject.tokens;

import javaccproject.SymbolTable;
import javaccproject.tokens.Token;

public class ElseToken extends Token
{
    /**
     * 
     */
    private static final long serialVersionUID = 3217163247681236342L;

    public ElseToken(Token myToken) {
        super(myToken);
        myContext = new SymbolTable();
        myContext.tableOf = this;
    }
    
    public String toString()
    {
        return String.format("else %d", beginLine);
    }
    
    public String symbolTableKey() {
        return String.format("else%d", beginLine);
    }
    
    public static String symbolTableKey(int beginLine){
        return String.format("else%d", beginLine);
    }
}
