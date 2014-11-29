package javaccproject.codegen;

import java.util.ArrayList;

public class MethodInvocationArgList
{
    public ArrayList<Access> actualArgs = new ArrayList<Access>();
    
    public String toString()
    {
        StringBuilder str = new StringBuilder("(");
        if(actualArgs.size() > 0)
        {
            str.append(actualArgs.get(0));
            for(int i = 1; i < actualArgs.size(); i++)
            {
                str.append(","+actualArgs.get(i));
            }
        }
        str.append(")");
        return str.toString();
    }
}
