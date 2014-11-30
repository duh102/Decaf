package javaccproject.visitors;

import javaccproject.Exp1Visitor;
import javaccproject.SimpleNode;

public class PrintingVisitor implements Exp1Visitor
{
    StringBuilder result = new StringBuilder();
    @Override
    public Object visit(SimpleNode node, Object data) {
        if(node.jjtGetValue() != null)
        {
            int parentCount = countParents(node);
            char[] tabLevel = new char[parentCount*3];
            for(int i = 0; i < tabLevel.length; i++)
            {
                tabLevel[i]=' ';
            }
            result.append(node.toString(new String(tabLevel))+"\n");
        }
        return null;
    }
    
    public String toString()
    {
        return result.toString();
    }
    
    public int countParents(SimpleNode node)
    {
        int count = 0;
        SimpleNode parent = (SimpleNode)node.jjtGetParent();
        while(parent != null)
        {
            if(parent.jjtGetValue() != null) count++;
            parent = (SimpleNode)parent.jjtGetParent();
        }
        return count;
    }

}
