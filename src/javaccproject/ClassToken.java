package javaccproject;

public class ClassToken extends Token
{

    /**
     * I don't honestly know why they bothered to make Tokens serializable but
     * oh well
     */
    private static final long serialVersionUID = -8780524111994565692L;

    public String superclass;

    public ClassToken(Token myToken) {
        super(myToken);
        myContext = new SymbolTable();
        myContext.tableOf = this;
    }
    
    public String toString()
    {
        return String.format("class %s", image);
    }
}
