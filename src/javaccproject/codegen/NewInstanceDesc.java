package javaccproject.codegen;

import javaccproject.tokens.Token;

public class NewInstanceDesc
{
    public Token.ReturnType type;
    public String objectType;
    public ArrayAccessDimList sizeIfArray;
    public MethodInvocationArgList argsIfCtor;
}
