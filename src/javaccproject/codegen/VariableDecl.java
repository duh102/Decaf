package javaccproject.codegen;

import javaccproject.tokens.VariableDeclToken;


public class VariableDecl
{
    public VariableDeclToken token;
    
    public VariableDecl(VariableDeclToken token)
    {
        this.token = token;
    }
    
    public String toString()
    {
        return token.toString();
    }
}
