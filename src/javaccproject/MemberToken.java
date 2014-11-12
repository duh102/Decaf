package javaccproject;

public abstract class MemberToken extends Token
{
    /**
     * I don't honestly know why they bothered to make Tokens serializable but
     * oh well
     */
    private static final long serialVersionUID = -3952564775782197150L;

    public Token myToken;
    public Token parent;
    public boolean isStatic;
}
