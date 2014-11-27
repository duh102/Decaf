package javaccproject.tokens;

import java.util.ArrayList;

import javaccproject.SimpleNode;

public class ArrayAccessToken extends AccessToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -3517460761989183815L;
    public ArrayList<SimpleNode> arrayDims;
    
    public ArrayAccessToken(Token toCopy)
    {
        super(toCopy);
    }
}
