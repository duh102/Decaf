/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaccproject;

import java.util.HashMap;

/**
 *
 * @author Lenovo
 */
public class SymbolTable
{
    public Token tableOf = null;
    HashMap<String, Token> table = new HashMap<String, Token>();

    void setToken(Token t) throws ParseException {
        if (table.containsKey(t.image)) {
            throw new ParseException("Duplicate symbol");
        } else {
            table.put(t.image, t);
            t.containedIn = this;
        }
    }

    public Token getToken(String t) {
        return table.get(t);
    }
    
    public String toString()
    {
        int level = level();
        char[] tab = new char[level];
        for(int i = 0; i < level; i++)
        {
            tab[i] = '\t';
        }
        String tabs = new String(tab);
        StringBuilder toReturn = new StringBuilder();
        for(String key : table.keySet())
        {
            if(table.get(key) instanceof ClassToken || table.get(key) instanceof MethodToken)
            {
                toReturn.append(String.format("%s%s\n%s\n", tabs, key, table.get(key).myContext));
            }
            else
            {
                toReturn.append(String.format("%s%s variable\n", tabs, key));
            }
        }
        return toReturn.toString();
    }
    
    public int level()
    {
        int level = 0;
        Token temp = tableOf;
        while(temp != null) {
            level++;
            temp = temp.containedIn.tableOf;
        }
        return level;
    }
}
