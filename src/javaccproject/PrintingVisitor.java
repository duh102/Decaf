package javaccproject;

public class PrintingVisitor implements Exp1Visitor
{
    StringBuilder result = new StringBuilder();
    @Override
    public Object visit(SimpleNode node, Object data) {
        char[] tabLevel = new char[countParents(node)];
        for(int i = 0; i < tabLevel.length; i++)
        {
            tabLevel[i]=' ';
        }
        result.append(node.toString(new String(tabLevel)));
        return null;
    }
    
    public String toString()
    {
        return result.toString();
    }
    
    public int countParents(SimpleNode node)
    {
        int count = 0;
        SimpleNode parent = (SimpleNode)node.parent;
        while(parent != null)
        {
            if(parent.jjtGetValue() != null) count++;
            parent = (SimpleNode)parent.parent;
        }
        return count;
    }

}
