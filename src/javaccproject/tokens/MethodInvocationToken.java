package javaccproject.tokens;

import java.util.ArrayList;

public class MethodInvocationToken extends AccessToken
{

    /**
     * 
     */
    private static final long serialVersionUID = -1780228241915244238L;
    
    public ArrayList<AccessToken> actualArgs = new ArrayList<AccessToken>();

    public MethodInvocationToken(Token toCopy) {
        super(toCopy);
    }
    
}
