package javaccproject.tokens;

import javaccproject.SimpleNode;
import javaccproject.SymbolTable;

public class MethodToken extends MemberToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -3068423710289097157L;
    
    public SimpleNode codeInside;
    
    public MethodToken(Token myToken) {
        super(myToken);
        myContext = new SymbolTable();
        myContext.tableOf = this;
    }
    
    public String toString() {
        return String.format("method %s %s", myType, image);
    }
}
