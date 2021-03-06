package javaccproject.tokens;

import javaccproject.DataType;

public class MemberToken extends Token
{
    /**
     * I don't honestly know why they bothered to make Tokens serializable but
     * oh well
     */
    private static final long serialVersionUID = -3952564775782197150L;

    public Token parent = null;
    public DataType myType = null;
    public boolean checkOnSecondPass;
    
    public MemberToken(Token toCopy, boolean checkOnSecondPass) {
        super(toCopy);
        this.checkOnSecondPass = checkOnSecondPass;
        if(toCopy instanceof MemberToken) {
            MemberToken toCopyFrom = (MemberToken)toCopy;
            this.parent = toCopyFrom.parent;
            this.myType = toCopyFrom.myType;
            this.checkOnSecondPass = checkOnSecondPass;
        }
    }
    
    public String symbolTableKey() {
        return String.format("var%s", image);
    }
    
    public static String symbolTableKey(String key){
        return String.format("var%s", key);
    }
}
