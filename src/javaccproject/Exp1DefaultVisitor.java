package javaccproject;

/* Generated By:JavaCC: Do not edit this line. Exp1DefaultVisitor.java Version 6.0_1 */
public class Exp1DefaultVisitor implements Exp1Visitor{
  public Object defaultVisit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(SimpleNode node, Object data){
    return defaultVisit(node, data);
  }
}
/* JavaCC - OriginalChecksum=aafe598471d2a0bd578172bc3505cd8d (do not edit this line) */
