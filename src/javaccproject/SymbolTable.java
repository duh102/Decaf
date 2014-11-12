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
        }
    }

    public Token getToken(String t) {
        return table.get(t);
    }
}
