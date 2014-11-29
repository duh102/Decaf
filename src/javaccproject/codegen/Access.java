package javaccproject.codegen;

public class Access
{
    public ArrayAccessDimList arrayDims = null;
    public MethodInvocationArgList methodCall = null;
    String image;
    
    public Access(String image)
    {
        this.image = image;
    }
    
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
