package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.util.Util;

public class ODByLeftRightAttributeList {
    public List<AttributeAndDirection> left;
    public List<AttributeAndDirection> right;

    public ODByLeftRightAttributeList(List<AttributeAndDirection> left, List<AttributeAndDirection> right) {
        this.left = left;
        this.right = right;
    }

    public ODByLeftRightAttributeList() {
        left=new ArrayList<>();
        right=new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        outAttributeListNoBracket(left,sb);
        sb.append(ODTree.OUTPUT_DELIMETER);
        outAttributeListNoBracket(right,sb);
        return sb.toString();
    }

    private static void outAttributeListNoBracket(List<AttributeAndDirection> list,StringBuilder sb){
        for (int i = 0; i < list.size(); i++) {
            if(i>0)
                sb.append(",");
            sb.append(list.get(i));
        }
    }

    public ODByLeftRightAttributeList deepClone() {
        return new ODByLeftRightAttributeList(new ArrayList<>(left)
                ,new ArrayList<>(right));
    }

    public void add(AttributeAndDirection attribute,boolean toLeft){
        if(toLeft)
            left.add(attribute);
        else
            right.add(attribute);
    }
}
