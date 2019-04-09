package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.util.Util;

public class ODByLeftRightAttributeList {
    public List<Integer> left;
    public List<Integer> right;

    public ODByLeftRightAttributeList(List<Integer> left, List<Integer> right) {
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

    private static void outAttributeListNoBracket(List<Integer> list,StringBuilder sb){
        for (int i = 0; i < list.size(); i++) {
            if(i>0)
                sb.append(",");
            sb.append(list.get(i)+1);
        }
    }

    public ODByLeftRightAttributeList deepClone() {
        return new ODByLeftRightAttributeList(new ArrayList<Integer>(left)
                ,new ArrayList<>(right));
    }

    public void add(int attribute,boolean toLeft){
        if(toLeft)
            left.add(attribute);
        else
            right.add(attribute);
    }

    public void removeLast(int attribute,boolean fromLeft){
        if(fromLeft)
            Util.removeLastFromList(left);
        else {
            Util.removeLastFromList(right);
        }
    }
}
