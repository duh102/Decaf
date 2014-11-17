package javaccproject;

import javaccproject.tokens.Token;

public class DataType
{
    public Token.AccessModifier accessModifier = Token.AccessModifier.Default;
    public Token.ReturnType type = null;
    public String objectType = null; // filled in if the returntype/vartype is object
    public Integer arrDim = 0; // >0 if array
    public Boolean isStatic = false;
    
    // some things are assumed by default, the type itself is not
    
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%s%s %s", accessModifier, isStatic?" static": "", type==Token.ReturnType.Object?objectType:type));
        for(int i = 0; i < arrDim; i++) {
            str.append("[]");
        }
        return str.toString();
    }
    
    public DataType() {
        super();
    }
    
    public DataType(DataType toCopy){
        this.accessModifier = toCopy.accessModifier;
        this.type = toCopy.type;
        this.objectType = toCopy.objectType;
        this.arrDim = toCopy.arrDim;
        this.isStatic = toCopy.isStatic;
    }
}
