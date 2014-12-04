package javaccproject;

import java.util.ArrayList;
import javaccproject.codegen.ClassDesc;
import javaccproject.codegen.MethodDesc;
import javaccproject.tokens.FormalArgVarDeclToken;
import javaccproject.tokens.MemberToken;
import javaccproject.tokens.MethodToken;
import javaccproject.tokens.Token;

public class CodeGenVisitor implements Exp1Visitor
{
    String resultFile = "";
    @Override
    public Object visit(SimpleNode node, Object o){
        resultFile = generateCode(node);
        return null;
    }    
    //generates code for this node and all children nodes
    public String generateCode(SimpleNode node){     
        //generate code through depth first search
        //if it's a class
        if(node.value instanceof ClassDesc){            
            return (".class "+((ClassDesc)node.value).token.image) + "\n" + recursiveCodeGen(node);
        }else if(node.value instanceof MethodDesc){
            //public, private, or protected, default is nothing
            String accessModifier = ((MemberToken)((MethodDesc)node.value).token).myType.accessModifier.toString().toLowerCase()+" ";
            if(accessModifier.equals("default "))accessModifier="";
            String isStatic = ((MemberToken)((MethodDesc)node.value).token).myType.isStatic?"static ":"";
            
            //get name
            String name = ((MemberToken)((MethodDesc)node.value).token).image;
            
            //get arguments and abbreviate
            ArrayList<FormalArgVarDeclToken> args = ((MethodToken)((MethodDesc)node.value).token).formalArgs;
            String argsAbbr = "";
            for(int i=0;i<args.size();i++){
                //add [ for each array dimension
                for(int j=0;j<args.get(i).myType.arrDim;j++){
                    argsAbbr+="[";
                }
                if(args.get(i).myType.type == Token.ReturnType.Boolean){                     
                    argsAbbr+="Z";
                }else if(args.get(i).myType.type == Token.ReturnType.Character){
                    argsAbbr+="C";
                }else if(args.get(i).myType.type == Token.ReturnType.Integer){
                    argsAbbr+="I";
                }else if(args.get(i).myType.type == Token.ReturnType.Object){                    
                    //L folloewd by object class name
                    argsAbbr+="L";
                    //built in object types
                    if(args.get(i).myType.objectType.equals("String")){
                        argsAbbr+="java/lang/String;";
                    }
                }else if(args.get(i).myType.type == Token.ReturnType.Void){
                    argsAbbr+="V";
                }
            }
            String returnType = "";
            //add [ for each array dimension
            for (int i = 0; i < (((MethodDesc) node.value).token).myType.arrDim; i++) {
                returnType += "[";
            }
            if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Boolean) {
                returnType += "B";
            }else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Character) {
                returnType += "C";
            }else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Integer) {
                returnType += "I";
            }else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Object) {
                returnType += "L";
                //built in object types
                if ((((MethodDesc) node.value).token).myType.objectType.equals("String")) {
                    returnType += "java/lang/String;";
                }
            } else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Void) {
                returnType += "V";
            }
            return ".method "+accessModifier+isStatic+name+"("+
                    argsAbbr+")"+returnType+"\n"+recursiveCodeGen(node)+
                    "\n.end method\n";
        }else{
            return recursiveCodeGen(node);
        }
    }
    public String recursiveCodeGen(SimpleNode node){        
        if (node.children != null) {
            String code = "";
            for (int i = 0; i < node.children.length; ++i) {
                code+=generateCode((SimpleNode)node.children[i]);
            }
            return code;
        }else{
            return "";
        }
    }
    @Override
    public String toString(){
        return resultFile;
    }
}
