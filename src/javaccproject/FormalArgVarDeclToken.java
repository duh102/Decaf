package javaccproject;

public class FormalArgVarDeclToken extends MemberToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -5303000839549209486L;

    public FormalArgVarDeclToken(Token myToken, DataType type) {
        this.myToken = myToken;
        this.dataType = type;
    }

}
