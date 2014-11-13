package javaccproject;

public class MethodToken extends MemberToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -3068423710289097157L;
    
    public SimpleNode codeInside;
    
    public MethodToken(Token myToken) {
        this.myToken = myToken;
        myContext = new SymbolTable();
    }
}
