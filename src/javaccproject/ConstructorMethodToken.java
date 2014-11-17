package javaccproject;

public class ConstructorMethodToken extends MethodToken
{
    /**
     * 
     */
    private static final long serialVersionUID = -5673087674427976975L;
    public ConstructorMethodToken(Token myToken)
    {
        super(myToken);
    }
    public String toString() {
        return String.format("constructor %s %s", myType.accessModifier, image);
    }
}
