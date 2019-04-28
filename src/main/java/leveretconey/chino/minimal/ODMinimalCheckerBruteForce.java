package leveretconey.chino.minimal;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.dataStructures.AttributeAndDirection;
import leveretconey.chino.dataStructures.ODByLeftRightAttributeList;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.util.Util;

public class ODMinimalCheckerBruteForce extends ODMinimalChecker{
    private List<ODCandidate> ods;

    public ODMinimalCheckerBruteForce() {
        ods=new ArrayList<>();
    }

    @Override
    public void insert(ODCandidate candidate) {
        ods.add(candidate);
    }

    @Override
    protected boolean isListMinimal(List<AttributeAndDirection> list) {
        for (ODCandidate od : ods) {
            List<AttributeAndDirection> left=od.odByLeftRightAttributeList.left;
            List<AttributeAndDirection> right=od.odByLeftRightAttributeList.right;
            int leftIndex=getIndex(list,left),rightIndex=getIndex(list,right);
            if(leftIndex!=-1 && rightIndex != -1 &&
                    (leftIndex<rightIndex ||rightIndex+right.size()==leftIndex)){
                return false;
            }
            list=reverseDirection(list);
            leftIndex=getIndex(list,left);
            rightIndex=getIndex(list,right);
            if(leftIndex!=-1 && rightIndex != -1 &&
                    (leftIndex<rightIndex ||rightIndex+right.size()==leftIndex)){
                return false;
            }
        }
        return true;
    }

    private int getIndex(List<AttributeAndDirection> context,List<AttributeAndDirection> pattern){
        if(context.size()<pattern.size())
            return -1;
        int end=context.size()-pattern.size();
        for (int i = 0; i <= end; i++) {
            if(exactMatch(context,pattern,i))
                return i;
        }
        return -1;
    }
}
