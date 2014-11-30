package javaccproject.codegen;

import java.util.ArrayList;

import javaccproject.SimpleNode;

public class ArrayAccessDimList
{
    public ArrayList<SimpleNode> arrayDims;
    
    public String toString()
    {
        StringBuilder str = new StringBuilder("");
        if(arrayDims.size() > 0)
        {
            str.append("["+ arrayDims.get(0));
            for(int i = 0; i < arrayDims.size(); i++)
            {
                str.append("]["+arrayDims.get(i));
            }
            str.append("]");
        }
        return str.toString();
    }
}
