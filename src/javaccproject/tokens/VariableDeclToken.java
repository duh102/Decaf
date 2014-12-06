package javaccproject.tokens;

import javaccproject.DataType;

public class VariableDeclToken extends FormalArgVarDeclToken
{
    /**
     * 
     */
    private static final long serialVersionUID = -9035653871099146097L;
    public Token assignment;//if this decl has some assignment, fill it in here
    
    public VariableDeclToken(Token myToken, DataType type, boolean checkOnSecondPass) {
        super(myToken, type, checkOnSecondPass);
    }
    
    public String toString() {
        return String.format("variable %s %s", myType, image);
    }
}
