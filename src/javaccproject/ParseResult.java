package javaccproject;

public class ParseResult
{
    public Node topNode;
    public SymbolTable table;
    
    public ParseResult(SymbolTable table, Node node)
    {
        this.topNode = node;
        this.table = table;
    }
    
    public String toString()
    {
        return String.format("%s\n\n\n%s", table, getAST());
    }
    
    public String getAST()
    {
        Exp1Visitor visit = new PrintingVisitor();
        ((SophisticatedNode)topNode).jjtAccept(visit, "");
        return visit.toString();
    }
    
    public String getNode(Node toPrint, String tabs)
    {
        StringBuilder toReturn = toPrint instanceof SimpleNode?
                new StringBuilder(String.format("%s%s:%s", tabs, toPrint, ((SimpleNode)toPrint).jjtGetValue()))
                : new StringBuilder(String.format("%s%s", tabs, toPrint));
        if(toPrint.jjtGetNumChildren() > 0)
        {
            String newTabs = tabs+' ';
            for(int i = 0; i < toPrint.jjtGetNumChildren(); i++)
            {
                toReturn.append(String.format("\n%s", getNode(toPrint.jjtGetChild(i),newTabs)));
            }
        }
        return toReturn.toString();
                
    }
}
