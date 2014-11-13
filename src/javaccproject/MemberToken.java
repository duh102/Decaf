package javaccproject;

public class MemberToken extends Token
{
    /**
     * I don't honestly know why they bothered to make Tokens serializable but
     * oh well
     */
    private static final long serialVersionUID = -3952564775782197150L;

    public Token myToken;
    public Token parent;
    public boolean isStatic;
    public Token.AccessModifier accessModifier;
    public Token.DataType dataType;
    public String dataTypeIfObject;//if the type of this member is an object, dataType will be Object and fill this in with the identifier
    public Integer dimCountIfArray = 0;//if this member is an array variable or returns an array variable, this count will be >0
}
