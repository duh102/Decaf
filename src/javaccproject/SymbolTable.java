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
            throw new ParseException(String.format("Error at %s: Duplicate symbol %s", t.parseExcept(), t));
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
        int longestKey = 0, extraWidth = 4;
        for(String key : table.keySet())
        {
            longestKey = Math.max(longestKey, table.get(key).toString().length());
        }
        char[] tableTopBottom = new char[longestKey+extraWidth];
        for(int i = 0; i < tableTopBottom.length; i++)
        {
            tableTopBottom[i] = '-';
        }
        String tableSeparator = new String(tableTopBottom);
        
        toReturn.append(String.format("%s%s\n", tabs, tableSeparator));
        for(String key : table.keySet())
        {
            if(table.get(key) instanceof ClassToken || table.get(key) instanceof MethodToken)
            {
                toReturn.append(String.format(String.format("%%s| %%%ds |\n%%s\n", longestKey+extraWidth-4), tabs, table.get(key).toString(), table.get(key).myContext));
            }
            else
            {
                toReturn.append(String.format(String.format("%%s| %%%ds |\n", longestKey+extraWidth-4), tabs, table.get(key).toString()));
            }
        }
        toReturn.append(String.format("%s%s", tabs, tableSeparator));
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
