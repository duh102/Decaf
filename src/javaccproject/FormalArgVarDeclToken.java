package javaccproject;

public class FormalArgVarDeclToken extends MemberToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -5303000839549209486L;

    public FormalArgVarDeclToken(Token myToken, DataType type) {
        super(myToken);
        this.myType = type;
    }
    
    public String toString() {
        return String.format("formal arg %s %s", myType, image);
    }

}
