package javaccproject;

public class VariableDeclToken extends FormalArgVarDeclToken
{
    /**
     * 
     */
    private static final long serialVersionUID = -9035653871099146097L;
    public Token assignment;//if this decl has some assignment, fill it in here
    
    public VariableDeclToken(Token myToken, Token.DataType type) {
        super(myToken, type);
    }
}
