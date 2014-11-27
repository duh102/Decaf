package javaccproject.codegen;

import javaccproject.tokens.ClassToken;

public class ClassDesc
{
    public ClassToken token;
    
    public ClassDesc(ClassToken token)
    {
        this.token = token;
    }
    
    public String toString()
    {
        return token.toString();
    }
}
