package javaccproject.tokens;

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
        return String.format("constructor %s %s(%s)", myType.accessModifier, image, args());
    }
    
    public String symbolTableKey() {
        return String.format("constructor%s%s", image, args());
    }
    
    public static String symbolTableKey(String key, String args){
        return String.format("constructor%s%s", key, args);
    }
}
