package javaccproject;

import javaccproject.codegen.Access;
import javaccproject.codegen.ClassDesc;
import javaccproject.codegen.MethodDesc;
import javaccproject.tokens.Token;
import javaccproject.visitors.CodeGenVisitor;

public class SophisticatedNode extends SimpleNode
{

    public SophisticatedNode(Exp1 p, int i) {
        super(p, i);
    }
    
    public SophisticatedNode(int i) {
        super(i);
    }
    
    @Override
    public Object jjtAccept(Exp1Visitor visitor, Object data) {
        if(visitor instanceof CodeGenVisitor){
            visitor.visit(this, data);
        } else if (visitor instanceof javaccproject.visitors.CheckSTVisitor) {
            //if checking variables    
            visitor.visit(this, data);
            if(children != null){
                for(int i= 0; i < children.length; i++){
                    //check
                    if(this.jjtGetValue() == null || (!(this.jjtGetValue() instanceof Access)
                            && !(this.jjtGetValue() instanceof ClassDesc)
                            && !(this.jjtGetValue() instanceof MethodDesc)
                            && !(this.jjtGetValue() instanceof Token))){
                        children[i].jjtAccept(visitor, data);
                    }else{
                        SymbolTable s = null;
                        if(this.jjtGetValue() instanceof ClassDesc){
                            s = (SymbolTable)(((ClassDesc)this.jjtGetValue()).token).myContext;
                        }else if(this.jjtGetValue() instanceof MethodDesc){
                            s = (SymbolTable)(((MethodDesc)this.jjtGetValue()).token).myContext;
                        }else if(this.jjtGetValue() instanceof Token){                         
                            s = (SymbolTable)((Token)this.jjtGetValue()).myContext;
                        }else if(this.jjtGetValue() instanceof Access){         
                            s = (SymbolTable)(((Access)this.jjtGetValue()).token).myContext;                            
                        }
                        if(s != null){
                            leftToRightScope current = new leftToRightScope(s);     
                            children[i].jjtAccept(visitor, current);
                        }
                    }
                }
            }
            return data;
        } else {
            visitor.visit(this, data);
            String tabLevelNew = (String) data + " ";
            if (children != null) {
                for (int i = 0; i < children.length; ++i) {
                    children[i].jjtAccept(visitor, tabLevelNew);
                }
            }
        }
        return data;
    }
    
    @Override
    public String toString()
    {
        if(jjtGetValue() != null && !(jjtGetValue() instanceof javaccproject.codegen.Operator) 
                && !(jjtGetValue() instanceof javaccproject.codegen.Literal))
        {
            return String.format("%s:%s", Exp1TreeConstants.jjtNodeName[id], jjtGetValue().toString());
        }
        else return "";
        //else return String.format("%s:%s", Exp1TreeConstants.jjtNodeName[id], jjtGetValue());
    }
    
    public String toString(boolean valueOnly)
    {
       return valueOnly? (jjtGetValue() != null? jjtGetValue().toString():"") : toString();
    }
    
    public String toString(String prefix)
    {
       return String.format("%s%s", prefix, toString(false));
    }

}
