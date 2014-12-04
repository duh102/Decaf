package javaccproject;

import javaccproject.visitors.PrintingVisitor;

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
    
    public void depthFirstCheck(){
        Exp1Visitor visitor = new CheckSTVisitor();
        leftToRightScope current = new leftToRightScope(table);   
        ((SophisticatedNode)topNode).jjtAccept(visitor, current);        
    }
    
    public String generateCode(){
        Exp1Visitor visitor = new CodeGenVisitor();
        ((SophisticatedNode)topNode).jjtAccept(visitor, ""); 
        return visitor.toString();
    }
    
    public String getNode(Node toPrint, String tabs)
    {
        StringBuilder toReturn = new StringBuilder(String.format("%s%s", tabs, toPrint));

        String newTabs = tabs+(toPrint instanceof SimpleNode && ((SimpleNode)toPrint).jjtGetValue()!= null? " ": "");
        if(toPrint.jjtGetNumChildren() > 0)
        {
            for(int i = 0; i < toPrint.jjtGetNumChildren(); i++)
            {
                toReturn.append(String.format("\n%s", getNode(toPrint.jjtGetChild(i),newTabs)));
            }
        }
        return toReturn.toString();
                
    }
}
