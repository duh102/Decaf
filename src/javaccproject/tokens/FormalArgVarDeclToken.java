package javaccproject.tokens;

import javaccproject.DataType;

public class FormalArgVarDeclToken extends MemberToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -5303000839549209486L;

    public FormalArgVarDeclToken(Token myToken, DataType type, boolean checkOnSecondPass) {
        super(myToken, checkOnSecondPass);
        this.myType = type;
    }
    
    public String toString() {
        return String.format("formal arg %s %s", myType, image);
    }

}
