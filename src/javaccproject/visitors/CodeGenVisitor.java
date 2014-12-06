package javaccproject.visitors;

import javaccproject.Exp1Visitor;
import javaccproject.SimpleNode;
import javaccproject.codegen.ExpressionDesc;

import java.util.ArrayList;
import java.util.Stack;

import javaccproject.codegen.Access;
import javaccproject.codegen.BlockDesc;
import javaccproject.codegen.BreakDesc;
import javaccproject.codegen.ClassDesc;
import javaccproject.codegen.ContinueDesc;
import javaccproject.codegen.IfDesc;
import javaccproject.codegen.Literal;
import javaccproject.codegen.MethodDesc;
import javaccproject.codegen.MethodInvocationArgList;
import javaccproject.codegen.Operator;
import javaccproject.codegen.Operator.OperatorType;
import javaccproject.codegen.Primary;
import javaccproject.codegen.PrimaryListDesc;
import javaccproject.codegen.ReturnDesc;
import javaccproject.codegen.WhileDesc;
import javaccproject.tokens.FormalArgVarDeclToken;
import javaccproject.tokens.MemberToken;
import javaccproject.tokens.MethodToken;
import javaccproject.tokens.Token;
import javaccproject.tokens.VariableDeclToken;

public class CodeGenVisitor implements Exp1Visitor
{
    String resultFile = "";
    int labelCount = 0;
    static Stack<String> currentTopLabel = new Stack<String>();
    static Stack<String> currentExitLabel = new Stack<String>();
    static ArrayList<String> variablesToLocalNumber = new ArrayList<String>();
    static boolean insideMethod = false;
    static String initializationMethodBegin = "";
    static String initializationMethodMiddle = "";
    static String initializationMethodEnd = "aload_0 \n" +
                                            "invokenonvirtual java/lang/Object/<init>()V \n" +
                                            "return \n" +
                                            ".end method \n";
    static boolean firstMethodDone = false;
    static String allFields = "";
    @Override
    public Object visit(SimpleNode node, Object o){
        resultFile = generateCode(node);
        return null;
    }    
    //generates code for this node and all children nodes
    public String generateCode(SimpleNode node){     
        //generate code through depth first search
        //if it's a class
        if(node.jjtGetValue() instanceof ClassDesc){
            //does it have a super class?
            //default is object
            String superString = ".super java/lang/Object";
            if(((ClassDesc)node.jjtGetValue()).token !=null && 
                    ((ClassDesc)node.jjtGetValue()).token.superclass != null &&
                    !((ClassDesc)node.jjtGetValue()).token.superclass.isEmpty()){
                //if has superclass
                superString = ".super "+((ClassDesc)node.jjtGetValue()).token.superclass;
            }            
            String rtrn= recursiveCodeGen(node);
            initializationMethodBegin = ".method <init>()V \n" +
                                              ".limit stack 32\n" +
                                              ".limit locals "+((ClassDesc)node.jjtGetValue()).token.myContext.table.size()+"\n";
            return (".class "+((ClassDesc)node.jjtGetValue()).token.image) + "\n" 
                    + superString + "\n"
                    + allFields + initializationMethodBegin + initializationMethodMiddle
                    + initializationMethodEnd + rtrn;
        }else if(node.jjtGetValue() instanceof MethodDesc){
            //public, private, or protected, default is nothing
            String accessModifier = ((MemberToken)((MethodDesc)node.jjtGetValue()).token).myType.accessModifier.toString().toLowerCase()+" ";
            if(accessModifier.equals("default "))accessModifier="";
            String isStatic = ((MemberToken)((MethodDesc)node.jjtGetValue()).token).myType.isStatic?"static ":"";
            
            //get name
            String name = ((MemberToken)((MethodDesc)node.jjtGetValue()).token).image;
            
            //get arguments and abbreviate
            ArrayList<FormalArgVarDeclToken> args = ((MethodToken)((MethodDesc)node.jjtGetValue()).token).formalArgs;
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
            String returnDefault = "";
            //i = int, l = long, f = float, d = double, a = pointer to object
            //add [ for each array dimension
            for (int i = 0; i < (((MethodDesc) node.jjtGetValue()).token).myType.arrDim; i++) {
                returnType += "[";
                returnDefault = "areturn";
            }
            if (((MethodToken) ((MethodDesc) node.jjtGetValue()).token).myType.type == Token.ReturnType.Boolean) {
                returnType += "B";
            }else if (((MethodToken) ((MethodDesc) node.jjtGetValue()).token).myType.type == Token.ReturnType.Character) {
                returnType += "C";
            }else if (((MethodToken) ((MethodDesc) node.jjtGetValue()).token).myType.type == Token.ReturnType.Integer) {
                returnType += "I";
                returnDefault = "ireturn";
            }else if (((MethodToken) ((MethodDesc) node.jjtGetValue()).token).myType.type == Token.ReturnType.Object) {
                returnType += "L";
                returnDefault = "areturn";
                //built in object types
                if ((((MethodDesc) node.jjtGetValue()).token).myType.objectType.equals("String")) {
                    returnType += "java/lang/String;";
                }
            } else if (((MethodToken) ((MethodDesc) node.jjtGetValue()).token).myType.type == Token.ReturnType.Void) {
                returnType += "V";
                returnDefault = "return";
            }
            //default always has 1 variable (this)
            //how many variables are in local scope? increase for each variable
            int variables = 1+(((MethodDesc) node.jjtGetValue()).token).myContext.table.size();
            //exact stack value requires intense analysis, using fixed value of 32.
            int stackValue = 32;
            //return generated code
            insideMethod = true;            
            String rtrn=".method "+accessModifier+isStatic+name+"("+
                    argsAbbr+")"+returnType+"\n"+".limit locals "+variables+
                    "\n"+ ".limit stack "+stackValue+"\n"+recursiveCodeGen(node)+
                    returnDefault+"\n"+
                    ".end method\n";
            insideMethod = false;
            return rtrn;
        }else if(node.jjtGetValue() instanceof BlockDesc){
            //ignore block statements, recursively check each inner statement            
            return recursiveCodeGen(node);
        }else if(node.jjtGetValue() instanceof IfDesc){
            //must have children[0] the condition.
            //must also have children[1] statements executed if true
            String labelExit = "label_"+labelCount++;
            if(((IfDesc)node.jjtGetValue()).ifElse == null){
                return generateCode((SimpleNode) node.jjtGetChild(0))
                        + "ifeq " + labelExit + "\n"
                        + generateCode((SimpleNode) node.jjtGetChild(1)) +
                        labelExit + ":\n";
            } else {
                //must have children[2] statements executed if false
                String labelElse = "label_" + labelCount++;
                return generateCode((SimpleNode) node.jjtGetChild(0))
                        + "ifeq " + labelElse + "\n"
                        + generateCode((SimpleNode) node.jjtGetChild(1))
                        + "goto " + labelExit + "\n"
                        + labelElse + ":\n" +
                        generateCode((SimpleNode)node.jjtGetChild(2)) +
                        labelExit + ":\n";
            }
        } else if(node.jjtGetValue() instanceof VariableDeclToken){
            //check if field
            if(!insideMethod){
                //is a field
                String accessModifier = ((VariableDeclToken)node.jjtGetValue()).myType.accessModifier.toString().toLowerCase()+" ";
            if(accessModifier.equals("default "))accessModifier="";
            String isStatic = ((VariableDeclToken)node.jjtGetValue()).myType.isStatic?"static ":"";
            
            //get name
            String name = ((VariableDeclToken)node.jjtGetValue()).image+" ";
            
            String returnType = "";
            //add [ for each array dimension
            for (int i = 0; i < ((VariableDeclToken)node.jjtGetValue()).myType.arrDim; i++) {
                returnType += "[";
            }
            if (((VariableDeclToken)node.jjtGetValue()).myType.type == Token.ReturnType.Boolean) {
                returnType += "B";
            }else if (((VariableDeclToken)node.jjtGetValue()).myType.type == Token.ReturnType.Character) {
                returnType += "C";
            }else if (((VariableDeclToken)node.jjtGetValue()).myType.type == Token.ReturnType.Integer) {
                returnType += "I";
            }else if (((VariableDeclToken)node.jjtGetValue()).myType.type == Token.ReturnType.Object) {
                returnType += "L";
                //built in object types
                if (((VariableDeclToken)node.jjtGetValue()).myType.objectType.equals("String")) {
                    returnType += "java/lang/String;";
                }
            } else if (((VariableDeclToken)node.jjtGetValue()).myType.type == Token.ReturnType.Void) {
                returnType += "V";
            }
            String assignment = "";
            //doies it have an assignment?
            if (((VariableDeclToken)node.jjtGetValue()).assignment != null){
                //yes, there's an assignment
                //calculate value to push into variable
                initializationMethodMiddle+= expressionHandler((SimpleNode)node.jjtGetChild(1), "",1);
            }
            //return generated code
            allFields+= ".field "+accessModifier+isStatic+name+returnType+"\n";
            return "";
            }
            //not a field, is a local variable initialization;
            return ";initiate variable token\n";
        } else if(node.jjtGetValue() instanceof ExpressionDesc){
            //if it's a method call
            System.out.println("");
            //return expressionHandler(node,"");
            return ";push value of expression on stack\n";
        } else if(node.jjtGetValue() instanceof WhileDesc){            
            //must have children[0] the condition.
            //must also have children[1] statements executed while true
            String labelTop = "label_" + labelCount++;
            String labelExit = "label_" + labelCount++;
            currentTopLabel.push(labelTop);
            currentExitLabel.push(labelExit);
            return labelTop + ":\n" + 
                    generateCode((SimpleNode)node.jjtGetChild(0)) +
                    "ifeq " + labelExit + "\n" +
                    generateCode((SimpleNode)node.jjtGetChild(1)) +
                    "goto "+labelTop+"\n"+
                    labelExit + ":\n";
        } else if(node.jjtGetValue() instanceof BreakDesc){
            return !currentExitLabel.isEmpty() ? "goto "+currentExitLabel.pop() + "\n" : "";
        } else if(node.jjtGetValue() instanceof ContinueDesc){
            return !currentTopLabel.isEmpty() ? "goto "+currentTopLabel.pop() + "\n" : "";
        }else {
            return recursiveCodeGen(node);
        }
    }
    public String recursiveCodeGen(SimpleNode node){        
        if (node.jjtGetNumChildren() > 0) {
            String code = "";
            for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
                code+=generateCode((SimpleNode)node.jjtGetChild(i));
            }
            return code;
        }else{
            return "";
        }
    }
    public String expressionHandler(SimpleNode node, String tabs, int numTab){
        numTab++;
        tabs+=" ";
        String returnStr = "";
        System.out.println(tabs+numTab+"--"+node.jjtGetValue());
        //check children first, depth first
        if(node.jjtGetValue() instanceof Operator){
            System.out.println("");
        }
        if(node.jjtGetNumChildren() > 0){
            String addAtEnd = "";
            for(int i=0;i<node.jjtGetNumChildren();i++){
                if(((SimpleNode)node.jjtGetChild(i)).jjtGetValue() instanceof Operator){
                    addAtEnd+=expressionHandler((SimpleNode)node.jjtGetChild(i),tabs,numTab);
                    continue;
                }
                returnStr+= expressionHandler((SimpleNode)node.jjtGetChild(i),tabs,numTab);
            }
            returnStr+=addAtEnd;
        }
        //if a variable is mentioned that is not being declare (e.g. not "int x")
        if(node.jjtGetValue() instanceof Access){
            //a variable
            return returnStr+"load "+((Access)node.jjtGetValue()).image+"\n";
        }else if(node.jjtGetValue() instanceof Literal){
            //a literal
            if(((Literal)node.jjtGetValue()).type.type == Token.ReturnType.Integer){
                //if it's an int
                //push onto stack
                return returnStr+"ldc "+((Literal)node.jjtGetValue()).image+"\n";
            }else{
                return returnStr+"";
            }
        }else if(node.jjtGetValue() instanceof Operator){
            //first do children            
            if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                //assumed integer
                return returnStr+"iadd\n";
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Minus){                
                //assumed integer
                return returnStr+"isub\n";
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Star){
                //assumed integer
                return returnStr+"imul\n";                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Slash){
                //assumed integer
                return returnStr+"idiv\n";
            }/*else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }else if(((Operator)node.jjtGetValue()).type == OperatorType.Plus){
                
            }*/else{
                return returnStr+"";
            }
        }
        return returnStr+"";
        //if contains binaryop 7, 6, 5, 4, or 3. it is a boolean expression
        //otherwise it returns a value
    }    
    @Override
    public String toString(){
        return resultFile;
    }
}