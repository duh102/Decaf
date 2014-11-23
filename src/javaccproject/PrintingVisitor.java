package javaccproject;

public class PrintingVisitor implements Exp1Visitor
{
    StringBuilder result = new StringBuilder();
    @Override
    public Object visit(SimpleNode node, Object tabLevel) {
        result.append(node.toString((String)tabLevel));
        return null;
    }
    
    public String toString()
    {
        return result.toString();
    }

}
