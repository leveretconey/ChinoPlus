package leveretconey.chino.discoverer;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODByLeftRightAttributeList;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;

import java.util.Arrays;
import java.util.List;

public abstract class ODDiscoverer {
    public abstract ODTree discover(DataFrame data, ODTree reference);

    public static long minimalCheckTime=0;
    protected boolean isODCandidateMinimal(ODTreeNode node, List<ODCandidate> ods){
        Timer timer=new Timer();
        ODCandidate odCandidate=new ODCandidate(node);


        int countAttribute=node.children.length;
        boolean[] attributeExist=new boolean[countAttribute];
        for (Integer attribute : odCandidate.odByPath) {
            if(attributeExist[attribute])
                return false;
            attributeExist[attribute]=true;
        }

        for (ODCandidate od : ods) {
            if(node.parent.status== ODTree.ODTreeNodeStatus.VALID){
                if(canFindLocalOD(odCandidate.odByLeftRightAttributeList.right,od))
                    return false;
            }else {
                if(canFindLocalOD(odCandidate.odByLeftRightAttributeList.left,od))
                    return false;
            }
        }
        minimalCheckTime+=timer.getTimeUsed();
        return true;
    }

    private boolean canFindLocalOD(List<Integer> list,ODCandidate od){
        int indexLeft= listIndexOf(list,od.odByLeftRightAttributeList.left);
        if(indexLeft==-1)
            return false;
        int indexRight= listIndexOf(list,od.odByLeftRightAttributeList.right);
        if(indexRight==-1)
            return false;
        return indexLeft<indexRight
                || indexRight+od.odByLeftRightAttributeList.right.size()==indexLeft;
    }

    private int listIndexOf(List<Integer> text, List<Integer> pattern){
        int beginPosition=0;
        while (beginPosition<text.size()){
            if(text.get(beginPosition).equals(pattern.get(0)))
                break;
            beginPosition++;
        }
        if(beginPosition==text.size())
            return -1;
        if(beginPosition+pattern.size() <= text.size()){
            for (int i = 1; i < pattern.size(); i++) {
                if(!text.get(beginPosition+i).equals(pattern.get(i)))
                    return -1;
            }
        }else{
            return -1;
        }
        return beginPosition;
    }

}
