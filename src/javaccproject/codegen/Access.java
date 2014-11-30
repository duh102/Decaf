package javaccproject.codegen;

public class Access
{
    public ArrayAccessDimList arrayDims = null;
    public MethodInvocationArgList methodCall = null;
    public String image;//if image is not null, access is accessing of some variable/class
    //if it is null, check the following enum
    //special case: this, image is set to "this"
    public AccessType type = AccessType.Variable;
    
    public enum AccessType {Variable, Literal, Expression};
    
    
    public String toString()
    {
        StringBuilder str = new StringBuilder(image);
        if(methodCall != null)
            str.append(methodCall.toString());
        if(arrayDims != null)
            str.append(arrayDims.toString());
        return str.toString();
    }
}
