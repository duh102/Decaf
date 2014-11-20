package javaccproject.tokens;

import javaccproject.SymbolTable;

public class WhileToken extends IfToken
{
    /**
     * 
     */
    private static final long serialVersionUID = 2875908319389526641L;

    public WhileToken(Token myToken) {
        super(myToken);
        myContext = new SymbolTable();
        myContext.tableOf = this;
    }
    
    public String toString()
    {
        return String.format("while %d", beginLine);
    }
    
    public String symbolTableKey() {
        return String.format("while%d", beginLine);
    }
    
    public static String symbolTableKey(int beginLine){
        return String.format("while%d", beginLine);
    }
}
