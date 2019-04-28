package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.util.Util;

public final class AttributeAndDirection {
    public static final int UP=1;
    public static final int DOWN=-1;
    public final int attribute;
    public final int direction;
    private static int maxAttribute=-1;
    private static List<AttributeAndDirection> ups=new ArrayList<>();
    private static List<AttributeAndDirection> downs=new ArrayList<>();

    public static AttributeAndDirection getInstance(int attribute,int direction){
        while (maxAttribute<attribute){
            maxAttribute++;
            ups.add(new AttributeAndDirection(maxAttribute,UP));
            downs.add(new AttributeAndDirection(maxAttribute,DOWN));
        }
        if(direction==UP){
            return ups.get(attribute);
        }else {
            return downs.get(attribute);
        }
    }

    private AttributeAndDirection(int attribute, int direction) {
        this.attribute = attribute;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return attribute+1+(direction==UP?"↑":"↓");
    }

    public static List<AttributeAndDirection> parseString(String ss){
        List<AttributeAndDirection> result=new ArrayList<>();
        for(String s:ss.split(",")){
            result.add(AttributeAndDirection.getInstance(Integer.parseInt(s.substring(0,s.length()-1))-1,
                    '↑'==s.charAt(s.length()-1)?AttributeAndDirection.UP:AttributeAndDirection.DOWN));
        }
        return result;
    }
}
