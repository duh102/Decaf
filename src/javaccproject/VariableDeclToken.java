package javaccproject;

public class VariableDeclToken extends MemberToken
{
    /**
     * 
     */
    private static final long serialVersionUID = -9035653871099146097L;
    public Token assignment;//if this decl has some assignment, fill it in here
    
    public VariableDeclToken(Token myToken, Token.DataType type) {
        this.myToken = myToken;
        this.dataType = type;
    }
}
