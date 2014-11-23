package javaccproject.codegen;

public class Operator
{
    public enum OperatorType
    {
        Equals, GreaterThan, LessThan, Not, EqualsEquals, GreaterThanEqual, LessThanEqual,
        NotEqual, AndAnd, Slash, Backslash, DoubleVerticalBar, Modulo, Plus, Minus, Star;
    }
    public String image;
    public OperatorType type;
    
    public String toString()
    {
        return image;
    }
    
    public Operator(String image, OperatorType type)
    {
        this.image = image;
        this.type = type;
    }
}
