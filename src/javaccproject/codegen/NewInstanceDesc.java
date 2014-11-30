package javaccproject.codegen;

import javaccproject.tokens.Token;

public class NewInstanceDesc
{
    public Token.ReturnType type;
    public String objectType;
    public ArrayAccessDimList sizeIfArray;
    public MethodInvocationArgList argsIfCtor;
    
    public String toString()
    {
        StringBuilder str = new StringBuilder("new " + (type==Token.ReturnType.Object? objectType: type));
        str.append(argsIfCtor==null?"":argsIfCtor);
        str.append(sizeIfArray==null?"":sizeIfArray);
        return str.toString();
    }
}
