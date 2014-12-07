package javaccproject.codegen;

import javaccproject.SimpleNode;

public class ExpressionDesc {
    public SimpleNode expressionNode;
    
    public ExpressionDesc(SimpleNode n){
        expressionNode = n;
    }
    
    public String toString(){
        return "expression";
    }
}
