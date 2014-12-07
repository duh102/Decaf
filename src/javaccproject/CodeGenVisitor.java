package javaccproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import javaccproject.codegen.Access;
import javaccproject.codegen.BlockDesc;
import javaccproject.codegen.BreakDesc;
import javaccproject.codegen.ClassDesc;
import javaccproject.codegen.ContinueDesc;
import javaccproject.codegen.ExpressionDesc;
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
    static Stack<String> currentTopLabel = new Stack<>();
    static Stack<String> currentExitLabel = new Stack<>();
    static ArrayList<String> currentLocalVariables = new ArrayList<>();
    static boolean insideMethod = false;
    static String initializationMethodBegin = "";
    static String initializationMethodMiddle = "";
    static String initializationMethodEnd = "aload_0 \n" +
                                            "invokenonvirtual java/lang/Object/<init>()V \n" +
                                            "return \n" +
                                            ".end method \n";
    static String allFields = "";
    static boolean skipNextExp = false;
    static SymbolTable rootST;
    static SymbolTable thisSymbolTable;
    @Override
    public Object visit(SimpleNode node, Object o){
        resultFile = generateCode(node);
        return null;
    }    
    //generates code for this node and all children nodes
    public String generateCode(SimpleNode node){ 
        if(node.visitedForCodeGen){
            return "";
        }else{
            node.visitedForCodeGen = true;
        }
        //generate code through depth first search
        //if it's a class
        if(node.value instanceof ClassDesc){
            rootST = ((ClassDesc)node.value).token.containedIn;
            thisSymbolTable = ((ClassDesc)node.value).token.myContext;
            //does it have a super class?
            //default is object
            String superString = ".super java/lang/Object";
            if(((ClassDesc)node.value).token !=null && 
                    ((ClassDesc)node.value).token.superclass != null &&
                    !((ClassDesc)node.value).token.superclass.isEmpty()){
                //if has superclass
                superString = ".super "+((ClassDesc)node.value).token.superclass;
            }            
            String rtrn= recursiveCodeGen(node);
            initializationMethodBegin = ".method <init>()V \n" +
                                              ".limit stack 32\n" +
                                              ".limit locals "+((ClassDesc)node.value).token.myContext.table.size()+"\n";
            return (".class "+((ClassDesc)node.value).token.image) + "\n" 
                    + superString + "\n"
                    + allFields + initializationMethodBegin + initializationMethodMiddle
                    + initializationMethodEnd + rtrn;
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
            String returnDefault = "";
            //i = int, l = long, f = float, d = double, a = pointer to object
            //add [ for each array dimension
            for (int i = 0; i < (((MethodDesc) node.value).token).myType.arrDim; i++) {
                returnType += "[";
                returnDefault = "areturn";
            }
            if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Boolean) {
                returnType += "B";
            }else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Character) {
                returnType += "C";
            }else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Integer) {
                returnType += "I";
                returnDefault = "ireturn";
            }else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Object) {
                returnType += "L";
                returnDefault = "areturn";
                //built in object types
                if ((((MethodDesc) node.value).token).myType.objectType.equals("String")) {
                    returnType += "java/lang/String;";
                }
            } else if (((MethodToken) ((MethodDesc) node.value).token).myType.type == Token.ReturnType.Void) {
                returnType += "V";
                returnDefault = "return";
            }
            //default always has 1 variable (this)
            //how many variables are in local scope? increase for each variable
            int variables = 1+(((MethodDesc) node.value).token).myContext.table.size();
            
            Set<String> keys = ((MethodDesc) node.value).token.myContext.table.keySet();
            Iterator iterSet = keys.iterator();
            while (iterSet.hasNext()) {
                String strKey = (String) iterSet.next();
                String value = ((MethodDesc)node.value).token.myContext.table.get(strKey).image;
                if(!currentLocalVariables.contains(value)){
                    currentLocalVariables.add(value);                    
                }
            }
            //for some reason, adds them in backwards
            Collections.reverse(currentLocalVariables);
            
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
            currentLocalVariables = new ArrayList<>();
            return rtrn;
        }else if(node.value instanceof BlockDesc){
            //ignore block statements, recursively check each inner statement            
            return recursiveCodeGen(node);
        }else if(node.value instanceof IfDesc){
            //must have children[0] the condition.
            //must also have children[1] statements executed if true
            String labelExit = "label_"+labelCount++;
            if(((IfDesc)node.value).ifElse == null){
                return generateCode((SimpleNode) node.children[0])
                        + "ifeq " + labelExit + "\n"
                        + generateCode((SimpleNode) node.children[1]) +
                        labelExit + ":\n";
            } else {
                //must have children[2] statements executed if false
                String labelElse = "label_" + labelCount++;
                return generateCode((SimpleNode) node.children[0])
                        + "ifeq " + labelElse + "\n"
                        + generateCode((SimpleNode) node.children[1])
                        + "goto " + labelExit + "\n"
                        + labelElse + ":\n" +
                        generateCode((SimpleNode)node.children[2]) +
                        labelExit + ":\n";
            }
        } else if(node.value instanceof VariableDeclToken){
            //check if field
            if(!insideMethod){
                //is a field
                String accessModifier = ((VariableDeclToken)node.value).myType.accessModifier.toString().toLowerCase()+" ";
            if(accessModifier.equals("default "))accessModifier="";
            String isStatic = ((VariableDeclToken)node.value).myType.isStatic?"static ":"";
            
            //get name
            String name = ((VariableDeclToken)node.value).image+" ";
            
            String returnType = "";
            //add [ for each array dimension
            for (int i = 0; i < ((VariableDeclToken)node.value).myType.arrDim; i++) {
                returnType += "[";
            }
            if (((VariableDeclToken)node.value).myType.type == Token.ReturnType.Boolean) {
                returnType += "B";
            }else if (((VariableDeclToken)node.value).myType.type == Token.ReturnType.Character) {
                returnType += "C";
            }else if (((VariableDeclToken)node.value).myType.type == Token.ReturnType.Integer) {
                returnType += "I";
            }else if (((VariableDeclToken)node.value).myType.type == Token.ReturnType.Object ||
                            ((VariableDeclToken)node.value).myType.type == Token.ReturnType.String) {
                returnType += "L";
                //built in object types
                if (((VariableDeclToken)node.value).myType.objectType.equals("String")) {
                    returnType += "java/lang/String;";
                }
            } else if (((VariableDeclToken)node.value).myType.type == Token.ReturnType.Void) {
                returnType += "V";
            }
            String assignment = "";
            //doies it have an assignment?
            if (((VariableDeclToken)node.value).assignment != null){
                //yes, there's an assignment
                //calculate value to push into variable
                initializationMethodMiddle+= expressionHandler((SimpleNode)node.children[1], "",1);
            }
            //return generated code
            allFields+= ".field "+accessModifier+isStatic+name+returnType+"\n";
            return "";
            }
            //not a field, is a local variable initialization;
            if(((SimpleNode)((SimpleNode)node).parent).children.length > 1 &&
                    ((SimpleNode)((SimpleNode)((SimpleNode)node).parent).children[1]).value instanceof ExpressionDesc){
                String ending;
                if(currentLocalVariables.contains(((VariableDeclToken)node.value).image)){
                    int varNum = -1;
                    for(int i=0;i<currentLocalVariables.size();i++){
                        if(currentLocalVariables.get(i).equals(((VariableDeclToken)node.value).image)){
                            varNum = i;
                            break;
                        }
                    }
                    //used to have underscore "istore_"
                    ending = "istore "+varNum+"\n";
                }else{
                    //not a local variable
                    ending = "istore "+((VariableDeclToken)node.value).image+"\n";
                }
                skipNextExp=true;
                return expressionHandler(((SimpleNode)((SimpleNode)((SimpleNode)node).parent).children[1]), "",1)+
                        ending;
            }
            //no code needs to be executed for declaration of variables with no assignment
            return "";
        } else if(node.value instanceof ExpressionDesc){
            if(!skipNextExp){
                skipNextExp = false;
                String s = expressionHandler((SimpleNode)node,"",1);
                return s;
            }else{
                skipNextExp=false;
                return "";
            }
            //return ";push value of expression on stack\n";
        } else if(node.value instanceof WhileDesc){            
            //must have children[0] the condition.
            //must also have children[1] statements executed while true
            String labelTop = "label_" + labelCount++;
            String labelExit = "label_" + labelCount++;
            currentTopLabel.push(labelTop);
            currentExitLabel.push(labelExit);
            return labelTop + ":\n" + 
                    generateCode((SimpleNode)node.children[0]) +
                    "ifeq " + labelExit + "\n" +
                    generateCode((SimpleNode)node.children[1]) +
                    "goto "+labelTop+"\n"+
                    labelExit + ":\n";
        } else if(node.value instanceof BreakDesc){
            return !currentExitLabel.isEmpty() ? "goto "+currentExitLabel.pop() + "\n" : "";
        } else if(node.value instanceof ContinueDesc){
            return !currentTopLabel.isEmpty() ? "goto "+currentTopLabel.pop() + "\n" : "";
        }else {
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
    public String expressionHandler(SimpleNode node, String tabs, int numTab){
        numTab++;
        tabs+=" ";
        String returnStr = "";
        System.out.println(tabs+numTab+"--"+node.value);
        //check children first, depth first
        if(node.value instanceof Operator){
            System.out.println("");
        }
        if(node.children != null){
            String addAtEnd = "";
            for(int i=0;i<node.children.length;i++){                
                if(((SimpleNode)node.children[i]).visitedForCodeGen){
                    return "";
                }else{
                    ((SimpleNode)node.children[i]).visitedForCodeGen = true;
                }
                if(((SimpleNode)node.children[i]).value instanceof Operator){
                    addAtEnd+=expressionHandler((SimpleNode)node.children[i],tabs,numTab);
                    continue;
                }
                returnStr+= expressionHandler((SimpleNode)node.children[i],tabs,numTab);
            }
            returnStr+=addAtEnd;
        }
        //if a variable is mentioned that is not being declare (e.g. not "int x")
        if(node.value instanceof Access){
            //work around for boolean literals
            if(((Access)node.value).image.equals("true")){
                return "iconst_1\n";
            }else if(((Access)node.value).image.equals("false")){                
                return "iconst_0\n";
            }
            //end work around for boolean literals            
            
            //if it's a method call
            //search symbol table for method calls
            
            //check first if class
            String key = "class"+((Access)node.value).token.image;
            Token t = rootST.getToken(key);
            Token tempMeth;
            String argumentsCode;
            if(t!=null){
                //check for methods in that class
                SimpleNode one = (SimpleNode)((SimpleNode)node.parent).parent;
                SimpleNode two = (SimpleNode)((SimpleNode)one.children[1]).children[0];
                String key1 = "method"+((Access)two.value).image+"()";
                tempMeth = t.myContext.getToken(key1);
                argumentsCode = recursiveCodeGen(two);
            } else {
                //might be a method call for this class                
                String key2 = "method"+((Access)node.value).token.image+"()";
                tempMeth = thisSymbolTable.getToken(key2);
                SimpleNode one = (SimpleNode)((SimpleNode)node.parent).parent;
                SimpleNode two = (SimpleNode)((SimpleNode)one.children[0]).children[0];
                String key1 = "method"+((Access)two.value).image+"()";
                argumentsCode = recursiveCodeGen(two);
            }
            if(tempMeth != null){
                MethodToken method = (MethodToken)tempMeth;
                //push given arguments onto a new stack
                //note: arguments need to be reversed on stack
                String name = method.image;

                //get arguments and abbreviate
                ArrayList<FormalArgVarDeclToken> args = method.formalArgs;
                String argsAbbr = "";
                for (int i = 0; i < args.size(); i++) {
                    //add [ for each array dimension
                    for (int j = 0; j < args.get(i).myType.arrDim; j++) {
                        argsAbbr += "[";
                    }
                    if (args.get(i).myType.type == Token.ReturnType.Boolean) {
                        argsAbbr += "Z";
                    } else if (args.get(i).myType.type == Token.ReturnType.Character) {
                        argsAbbr += "C";
                    } else if (args.get(i).myType.type == Token.ReturnType.Integer) {
                        argsAbbr += "I";
                    } else if (args.get(i).myType.type == Token.ReturnType.Object ||
                            args.get(i).myType.type == Token.ReturnType.String) {
                        //L folloewd by object class name
                        argsAbbr += "L";
                        //built in object types
                        if (args.get(i).myType.type == Token.ReturnType.String) {
                            argsAbbr += "java/lang/String;";
                        }
                    } else if (args.get(i).myType.type == Token.ReturnType.Void) {
                        argsAbbr += "V";
                    }
                }
                String returnType = "";
            //i = int, l = long, f = float, d = double, a = pointer to object
                //add [ for each array dimension
                for (int i = 0; i < method.myType.arrDim; i++) {
                    returnType += "[";
                }
                if (method.myType.type == Token.ReturnType.Boolean) {
                    returnType += "B";
                } else if (method.myType.type == Token.ReturnType.Character) {
                    returnType += "C";
                } else if (method.myType.type == Token.ReturnType.Integer) {
                    returnType += "I";
                } else if (method.myType.type == Token.ReturnType.Object) {
                    returnType += "L";
                    //built in object types
                    if (method.myType.objectType.equals("String")) {
                        returnType += "java/lang/String;";
                    }
                } else if (method.myType.type == Token.ReturnType.Void) {
                    returnType += "V";
                }

                String methodFull = name + "("
                        + argsAbbr + ")" + returnType; 
                String plusClass = t==null? thisSymbolTable.tableOf.image+"/"+methodFull:t.image + "/" + methodFull;
                
                //is the method static?
                if(method.myType.isStatic){
                    return argumentsCode + "invokestatic "+plusClass+"\n";
                }else{
                    return argumentsCode + "invokevirtual "+plusClass+"\n";
                }
            }else{
            //a variable
            //assume integer
            System.out.println("");
            if(currentLocalVariables.contains(((Access)node.value).image)){
                int variable = currentLocalVariables.indexOf(((Access)node.value).image);
                return returnStr+"iload "+variable+"\n";
            }else{
                return returnStr+"iload "+((Access)node.value).image+"\n";
            }
            }
        }else if(node.value instanceof Literal){
            //a literal
            if(((Literal)node.value).type.type == Token.ReturnType.Integer ||
                    ((Literal)node.value).type.type == Token.ReturnType.String){
                //if it's an int
                //push onto stack
                return returnStr+"ldc "+((Literal)node.value).image+"\n";
            }else{
                return returnStr;
            }
        }else if(node.value instanceof Operator){
            //first do children            
            if(((Operator)node.value).type == OperatorType.Plus){
                //assumed integer
                return returnStr+"iadd\n";
            }else if(((Operator)node.value).type == OperatorType.Minus){                
                //assumed integer
                return returnStr+"isub\n";
            }else if(((Operator)node.value).type == OperatorType.Star){
                //assumed integer
                return returnStr+"imul\n";                
            }else if(((Operator)node.value).type == OperatorType.Slash){
                //assumed integer
                return returnStr+"idiv\n";
            }else if(((Operator)node.value).type == OperatorType.GreaterThan){
                //assumed integer                
                String ifNotGreater = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "if_cmpge " + ifNotGreater + "\n"+
                        "iconst_1\n"+
                        "goto "+exitLabel+"\n"+
                        ifNotGreater+":\n"+
                        "iconst_0\n"+
                        exitLabel+":\n";
            }else if(((Operator)node.value).type == OperatorType.EqualsEquals){
                //assumed integer                
                String ifNotE = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "if_cmpne " + ifNotE + "\n"+
                        "iconst_1\n"+
                        "goto "+exitLabel+"\n"+
                        ifNotE+":\n"+
                        "iconst_0\n"+
                        exitLabel+":\n";                    
            }else if(((Operator)node.value).type == OperatorType.GreaterThanEqual){
                //assumed integer                
                String ifNotGreaterE = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "if_cmpgt " + ifNotGreaterE + "\n"+
                        "iconst_1\n"+
                        "goto "+exitLabel+"\n"+
                        ifNotGreaterE+":\n"+
                        "iconst_0\n"+
                        exitLabel+":\n";                
            }else if(((Operator)node.value).type == OperatorType.LessThan){
                //assumed integer                
                String ifNotLess = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "if_cmple " + ifNotLess + "\n"+
                        "iconst_1\n"+
                        "goto "+exitLabel+"\n"+
                        ifNotLess+":\n"+
                        "iconst_0\n"+
                        exitLabel+":\n";                           
            }else if(((Operator)node.value).type == OperatorType.LessThanEqual){
                //assumed integer                
                String ifNotLessE = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "if_cmplt " + ifNotLessE + "\n"+
                        "iconst_1\n"+
                        "goto "+exitLabel+"\n"+
                        ifNotLessE+":\n"+
                        "iconst_0\n"+
                        exitLabel+":\n";                        
            }else if(((Operator)node.value).type == OperatorType.NotEqual){
                //assumed integer                
                String ifNotNotE = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "if_cmpeq " + ifNotNotE + "\n"+
                        "iconst_1\n"+
                        "goto "+exitLabel+"\n"+
                        ifNotNotE+":\n"+
                        "iconst_0\n"+
                        exitLabel+":\n";                 
            }else if(((Operator)node.value).type == OperatorType.AndAnd){     
                String ifNot = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "ifeq " + ifNot + "\n"+
                        "ifeq " + ifNot + "\n" +
                        "iconst_1\n"+
                        "goto "+exitLabel+"\n"+
                        ifNot+":\n"+
                        "iconst_0\n"+
                        exitLabel+":\n";                   
            }else if(((Operator)node.value).type == OperatorType.Modulo){
                //assumed integer
                return returnStr+"irem\n";                
            }else if(((Operator)node.value).type == OperatorType.DoubleVerticalBar){  
                String ifTrue = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "ifne " + ifTrue + "\n"+
                        "ifne " + ifTrue + "\n" +
                        "iconst_0\n"+
                        "goto "+exitLabel+"\n"+
                        ifTrue+":\n"+
                        "iconst_1\n"+
                        exitLabel+":\n";                  
            }else if(((Operator)node.value).type == OperatorType.Not){
                //assumed integer                
                String ifFalse = "label_"+labelCount++;
                String exitLabel = "label_"+labelCount++;
                return "ifeq " + ifFalse + "\n"+
                        "iconst_0\n"+
                        "goto "+exitLabel+"\n"+
                        ifFalse+":\n"+
                        "iconst_1\n"+
                        exitLabel+":\n";
            }else if(((Operator)node.value).type == OperatorType.Equals){
                //whatevers on the left is assigned to value on right
                //right is in top spot on stack
                SimpleNode one = (SimpleNode)((SimpleNode)((SimpleNode)node.parent).parent).children[0];
                SimpleNode two = ((SimpleNode)((SimpleNode)one.children[0]).children[0]);
                SimpleNode three = ((SimpleNode)((SimpleNode)two.children[0]).children[0]);
                SimpleNode four = ((SimpleNode)((SimpleNode)three.children[0]).children[0]);
                SimpleNode five = (SimpleNode)((SimpleNode)((SimpleNode)(SimpleNode)four.children[0]).children[0]).children[0];
                String varToStore = ((Access)five.value).image;
                //is varToStore a local variable? if so, use it as local
                if(currentLocalVariables.contains(varToStore)){
                    int varNum = -1;
                    forLoop:
                    for(int i=0;i<currentLocalVariables.size();i++){
                        if(currentLocalVariables.get(i).equals(varToStore)){
                            varNum = i;
                            break forLoop;
                        }
                    }
                    //used to have underscore "istore_"
                    return "istore "+varNum+"\n";
                }else{
                    //not a local variable
                    return "istore "+varToStore+"\n";
                }
            }else{
                return returnStr+"";
            }
        }
        return returnStr+"";
    }
    @Override
    public String toString(){
        return resultFile;
    }
}
