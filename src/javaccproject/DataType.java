package javaccproject;

public class DataType
{
    public Token.AccessModifier accessModifier = Token.AccessModifier.Default;
    public Token.ReturnType type;
    public String objectType; // filled in if the returntype/vartype is object
    public Integer arrDim = 0; // >0 if array
    public Boolean isStatic = false;
    
    // some things are assumed by default, the type itself is not
}
