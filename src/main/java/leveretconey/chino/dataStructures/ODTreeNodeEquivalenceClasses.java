package leveretconey.chino.dataStructures;

import java.util.List;

import leveretconey.chino.util.Timer;

public class ODTreeNodeEquivalenceClasses {
    public EquivalenceClass left;
    public EquivalenceClass right;
    public static long mergeTime=0;
    public static long validateTime=0;

    public ODValidationResult validate(DataFrame data){
        Timer validateTimer=new Timer();

        ODValidationResult result=new ODValidationResult();
        result.status= ODTree.ODTreeNodeStatus.VALID;
        if(left.groups==null || right.groups==null)
            return result;
        //metadata
        int[] row2RightGroupIndex=new int[data.getRowCount()];
        for (int rightGroupIndex = 0; rightGroupIndex < right.groups.size(); rightGroupIndex++) {
            for (Integer row : right.groups.get(rightGroupIndex)) {
                row2RightGroupIndex[row]=rightGroupIndex;
            }
        }

        //get max and min rows in each group for further checking
        int[] minRowInLeftGroup=new int[left.groups.size()];
        int[] maxRowInLeftGroup=new int[left.groups.size()];
        for (int leftGroupIndex = 0; leftGroupIndex < left.groups.size(); leftGroupIndex++) {
            List<Integer> leftGroup=left.groups.get(leftGroupIndex);
            int max=leftGroup.get(0),min=max;
            for (int row : leftGroup) {
                if(row2RightGroupIndex[row]<row2RightGroupIndex[min])
                    min=row;
                if(row2RightGroupIndex[row]>row2RightGroupIndex[min])
                    max=row;
            }
            maxRowInLeftGroup[leftGroupIndex]=max;
            minRowInLeftGroup[leftGroupIndex]=min;
        }
        //get check result
        for (int leftGroupIndex = 0; leftGroupIndex < left.groups.size(); leftGroupIndex++) {
            //split
            if(result.status== ODTree.ODTreeNodeStatus.VALID &&
                    minRowInLeftGroup[leftGroupIndex]!=maxRowInLeftGroup[leftGroupIndex]){
                result.status= ODTree.ODTreeNodeStatus.SPLIT;
                result.violationRows.add(minRowInLeftGroup[leftGroupIndex]);
                result.violationRows.add(maxRowInLeftGroup[leftGroupIndex]);
            }
            //swap
            if(leftGroupIndex>0 &&
                    row2RightGroupIndex[maxRowInLeftGroup[leftGroupIndex-1]]
                  > row2RightGroupIndex[minRowInLeftGroup[leftGroupIndex  ]]){
                result.status= ODTree.ODTreeNodeStatus.SWAP;
                result.violationRows.add(minRowInLeftGroup[leftGroupIndex  ]);
                result.violationRows.add(maxRowInLeftGroup[leftGroupIndex-1]);
                break;
            }
        }

        validateTime+=validateTimer.getTimeUsed();
        return result;
    }

    public ODTreeNodeEquivalenceClasses() {
        left=new EquivalenceClass();
        right=new EquivalenceClass();
    }

    public ODTreeNodeEquivalenceClasses(EquivalenceClass left, EquivalenceClass right) {
        this.left = left;
        this.right = right;
    }

    public ODTreeNodeEquivalenceClasses deepClone(){
        return new ODTreeNodeEquivalenceClasses(left.deepClone(),right.deepClone());
    }

    public ODTreeNodeEquivalenceClasses mergeNode(ODTree.ODTreeNode node,DataFrame data){
        Timer mergeTimer =new Timer();

        int attribute=node.attribute;
        if(node.parent.status== ODTree.ODTreeNodeStatus.SPLIT)
            left.merge(data,attribute);
        else
            right.merge(data,attribute);
        mergeTime+=mergeTimer.getTimeUsed();
        return this;
    }
}
