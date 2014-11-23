package javaccproject;

public class SophisticatedNode extends SimpleNode
{

    public SophisticatedNode(Exp1 p, int i) {
        super(p, i);
    }
    
    public SophisticatedNode(int i) {
        super(i);
    }
    
    public Object jjtAccept(Exp1Visitor visitor, Object data) {
        visitor.visit(this, data);
        String tabLevelNew = jjtGetValue() != null? (String)data + " ": (String)data;
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                children[i].jjtAccept(visitor, tabLevelNew);
            }
        }
        return data;
    }
    
    public String toString()
    {
        if(jjtGetValue() != null)
        {
            return String.format("%s:%s\n", Exp1TreeConstants.jjtNodeName[id], jjtGetValue());
        }
        else return "";
    }

}
