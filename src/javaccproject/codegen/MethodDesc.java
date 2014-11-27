package javaccproject.codegen;

import javaccproject.tokens.MethodToken;

public class MethodDesc
{
    public MethodToken token;
    
    public MethodDesc(MethodToken token)
    {
        this.token = token;
    }
    
    public String toString()
    {
        return token.toString();
    }
}
