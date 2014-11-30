package javaccproject.codegen;

public class IfDesc
{
    public ElseDesc ifElse;
    public String toString()
    {
        StringBuilder str = new StringBuilder("if");
        if(ifElse != null)
            str.append("else");
        return str.toString();
    }
}
