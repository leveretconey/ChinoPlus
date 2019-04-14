package minimal;

import java.util.List;

import leveretconey.chino.dataStructures.AttributeAndDirection;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.util.Timer;

public abstract class ODMinimalChecker {
    public static long minimalCheckTime=0;
    public abstract void insert(ODCandidate candidate);
    public boolean isCandidateMinimal(ODCandidate candidate){
        Timer timer=new Timer();
        boolean result;
        List<AttributeAndDirection> expandSide,otherSide;
        if(candidate.odByODTreeNode.parent.status== ODTree.ODTreeNodeStatus.VALID){
            expandSide=candidate.odByLeftRightAttributeList.right;
            otherSide=candidate.odByLeftRightAttributeList.left;
        }else {
            expandSide=candidate.odByLeftRightAttributeList.left;
            otherSide=candidate.odByLeftRightAttributeList.right;
        }
        int expandAttribute=expandSide.get(expandSide.size()-1).attribute;
        for(AttributeAndDirection x:otherSide){
            if(x.attribute==expandAttribute)
                return false;
        }
        for (int i = 0; i < expandSide.size()-1; i++) {
            if(expandAttribute==expandSide.get(i).attribute)
                return false;
        }
        result= isListMinimal(expandSide);
        minimalCheckTime+=timer.getTimeUsed();
        return result;
    }
    protected abstract boolean isListMinimal(List<AttributeAndDirection> list);

    protected boolean canFindBefore(List<AttributeAndDirection> context,
                                  List<AttributeAndDirection> pattern,int beginPosition){
        int targetAttribute=pattern.get(0).attribute;
        int targetDirection=pattern.get(0).direction;
        for(int i=beginPosition;i>=0;i--){
            if(context.get(i).attribute==targetAttribute){
                if(context.get(i).direction!=targetDirection)
                    return false;
                for(int j=1;j<pattern.size();j++){
                    AttributeAndDirection x1=context.get(i+j);
                    AttributeAndDirection x2=pattern.get(j);
                    if(x1.direction!=x2.direction || x1.attribute!=x2.attribute)
                        return false;
                }
                return true;
            }
        }
        return false;
    }
    protected boolean exactMatch(List<AttributeAndDirection> context,
                               List<AttributeAndDirection> pattern,int beginPosition){
        if(beginPosition<0)
            return false;
        for (int i = 0; i < pattern.size(); i++) {
            AttributeAndDirection x1=context.get(beginPosition+i);
            AttributeAndDirection x2=pattern.get(i);
            if(x1.direction!=x2.direction || x1.attribute!=x2.attribute)
                return false;
        }
        return true;
    }

}
