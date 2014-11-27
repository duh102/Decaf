package javaccproject.tokens;

import java.util.ArrayList;

import javaccproject.SymbolTable;

public class MethodToken extends MemberToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -3068423710289097157L;
    
    public ArrayList<FormalArgVarDeclToken> formalArgs = new ArrayList<FormalArgVarDeclToken>();
    
    public MethodToken(Token myToken) {
        super(myToken);
        myContext = new SymbolTable();
        myContext.tableOf = this;
    }
    
    public String toString() {
        return String.format("method %s %s(%s)", myType, image, args());
    }
    
    public String symbolTableKey() {
        return String.format("method%s(%s)", image, args());
    }
    
    public static String symbolTableKey(String key, String args){
        return String.format("method%s(%s)", key, args);
    }
    
    public String args()
    {
        StringBuilder builder = new StringBuilder("");
        if(formalArgs.size() > 0)
            for(FormalArgVarDeclToken token : formalArgs)
            {
                if(builder.length() > 0) builder.append(",");
                builder.append(token.myType.toString(false));
            }
        return builder.toString();
    }
}
