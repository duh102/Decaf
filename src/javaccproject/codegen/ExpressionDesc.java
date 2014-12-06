package javaccproject.codegen;

import javaccproject.SimpleNode;
import javaccproject.tokens.MemberToken;

public class ExpressionDesc {
    public SimpleNode expressionNode;
    
    public ExpressionDesc(SimpleNode n){
        expressionNode = n;
    }
    
    public String toString(){
        return "expression";
    }
}
