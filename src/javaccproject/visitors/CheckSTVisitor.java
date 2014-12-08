package javaccproject.visitors;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaccproject.codegen.*;
import javaccproject.tokens.*;
import javaccproject.*;

public class CheckSTVisitor implements Exp1Visitor
{    
    boolean failed = false;
    @Override
    public Object visit(SimpleNode node, Object ltrScope){
        //check if value is null, if it is, return
        if(node.jjtGetValue() == null || 
                node.jjtGetValue() instanceof javaccproject.codegen.Operator){
            return null;
        }else{
            //value is not null
            if(!(node.jjtGetValue() instanceof MemberToken) && !(node.jjtGetValue() instanceof Access)){
                if(node.jjtGetValue() instanceof ClassDesc){
                    ((leftToRightScope)ltrScope).currentlyScannedThisScope.add(((Token)(((ClassDesc)node.jjtGetValue()).token)).symbolTableKey());
                }
                return null;
            }else{
                if(!(node.jjtGetValue() instanceof Access) && !((MemberToken)node.jjtGetValue()).checkOnSecondPass){
                    ((leftToRightScope)ltrScope).currentlyScannedThisScope.add(((MemberToken)node.jjtGetValue()).symbolTableKey());
                    return null;
                }
            }
            //if symbol table or key is null throw error
            if(node.jjtGetValue() instanceof MemberToken){
                return null;
            }
            String testTokenKey = ((Token)((Access)node.jjtGetValue()).token).symbolTableKey();
            if((ltrScope == null) || (((Token)((Access)node.jjtGetValue()).token).symbolTableKey() == null)){                 
                try {
                    throw new ParseException(String.format(
                            "Error at %s: \"%s\" used before declaration", ((Token)((Access)node.jjtGetValue()).token).parseExcept(), ((MemberToken)node.jjtGetValue()).image()));
                } catch (ParseException ex) {
                    Logger.getLogger(CheckSTVisitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            //if symbol table doesn't contain value, check recursively all parents
            }else if(!(((leftToRightScope) ltrScope).currentlyScannedThisScope.contains(((Token)((Access)node.jjtGetValue()).token).symbolTableKey()))) {
                if (((leftToRightScope) ltrScope).equivalent.tableOf != null && ((leftToRightScope) ltrScope).equivalent.tableOf.containedIn != null) {
                    if (!checkRecursive((((Token)((Access)node.jjtGetValue()).token)).symbolTableKey(), ((leftToRightScope) ltrScope).equivalent.tableOf.containedIn)) {
                        //check as class too
                        if(!checkRecursive("class" + (((Token) ((Access) node.jjtGetValue()).token)).image(), ((leftToRightScope) ltrScope).equivalent.tableOf.containedIn)) {
                            //lastly check all classes for methods
                            if(!checkClasses("method" + (((Token) ((Access) node.jjtGetValue()).token)).image()+"()", ((leftToRightScope) ltrScope).equivalent.tableOf.containedIn)) {
                            //error
                                try {
                                    throw new ParseException(String.format(
                                            "Error at %s: \"%s\" used before declaration", (((Token) ((Access) node.jjtGetValue()).token)).parseExcept(), (((Token) ((Access) node.jjtGetValue()).token)).image()));
                                } catch (ParseException e) {
                                    // Catching Throwable is ugly but JavaCC throws Error objects!
                                    System.err.println("Syntax check failed: " + e.getMessage());
                                }
                            }
                        }
                    } else {
                        //is okay
                        System.out.println((((Token)((Access)node.jjtGetValue()).token)).image() + " is correct in scope");
                    }
                } else {
                    //error no upper scope 
                    try{
                        throw new ParseException(String.format(
                                "Error at %s: \"%s\" used before declaration", (((Token)((Access)node.jjtGetValue()).token)).parseExcept(), (((Token)((Access)node.jjtGetValue()).token)).image()));
                    } catch (ParseException e) {
                        // Catching Throwable is ugly but JavaCC throws Error objects!
                        System.err.println("Syntax check failed: " + e.getMessage());
                    }
                }
            }else{
                //correct test
                System.out.println((((Token)((Access)node.jjtGetValue()).token)).image() + " is correct in scope");
            }
        }
        return null;
    }
    public boolean checkRecursive(String s, SymbolTable symbolTable){
        if(symbolTable.getToken(s) != null){
            return true;
        }else{
            if(symbolTable.tableOf != null && symbolTable.tableOf.containedIn != null){
                return checkRecursive(s, symbolTable.tableOf.containedIn);
            }else{
                return false;
            }
        }
    }
    public boolean checkClasses(String method, SymbolTable symbolTable){
        //get root table
        SymbolTable root;
        if(symbolTable.tableOf == null || symbolTable.tableOf.containedIn == null){
            return false;
        }else{
            root = symbolTable.tableOf.containedIn;
        }
        Set<String> keys = root.table.keySet();
        Iterator<String> iter = keys.iterator();
        while(iter.hasNext()){
            //check each class
            String strKey = (String) iter.next();
            ClassToken classToken = (ClassToken) root.getToken(strKey);
            SymbolTable classTable = classToken.myContext;
            if(classTable.getToken(method) != null){
                return true;
            }
        }
        return false;
    }            
}