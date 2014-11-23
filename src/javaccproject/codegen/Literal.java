package javaccproject.codegen;

import javaccproject.DataType;

public class Literal
{
    public String image;
    public DataType type;
    
    public Literal(String image, DataType type )
    {
        this.image = image;
        this.type = type;//special case: ReturnType of void == null literal
    }
    
    public String toString()
    {
        return String.format("(%s) %s", type.toString(false), image);
    }
}
