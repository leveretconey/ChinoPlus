package leveretconey.chino.dataStructures;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.crypto.Data;

import leveretconey.chino.sampler.DataAndIndex;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;
import sun.reflect.generics.tree.Tree;

public class ODTreeNodeEquivalenceClasses {
    public EquivalenceClass left;
    public EquivalenceClass right;
    public static long mergeTime=0;
    public static long validateTime=0;
    public static long cloneTime=0;

    int[] getRow2RightGroupIndex(){
        int[] row2RightGroupIndex=new int[left.indexes.length];
        int rightGroupIndex=-1;
        int beginPointer=0;
        for (int i = 0; i < right.indexes.length; i++) {
            if(i==right.begins.get(beginPointer)){
                rightGroupIndex++;
                beginPointer++;
            }
            row2RightGroupIndex[right.indexes[i]]=rightGroupIndex;
        }
        return row2RightGroupIndex;
    }


    public ODValidationResult validate(DataFrame data){
        Timer validateTimer=new Timer();

        ODValidationResult result=new ODValidationResult();
        result.status= ODTree.ODTreeNodeStatus.VALID;
        if(!left.initialized() || !right.initialized())
            return result;

        int[] row2RightGroupIndex=getRow2RightGroupIndex();

        int max=0,min=0,maxvalue=0,minvalue=0;
        int maxLast=0,maxLastValue=0;
        for(int beginPointer=0;beginPointer<left.begins.size()-1;beginPointer++){
            int groupBegin=left.begins.get(beginPointer);
            int groupEnd=left.begins.get(beginPointer+1);
            max=min=left.indexes[groupBegin];
            maxvalue=minvalue=row2RightGroupIndex[max];
            for(int i=groupBegin+1;i<groupEnd;i++){
                int index=left.indexes[i];
                int value=row2RightGroupIndex[index];
                if(value<minvalue){
                    min=index;
                    minvalue=value;
                }
                if(maxvalue<value){
                    max=index;
                    maxvalue=value;
                }
            }

            if(result.status== ODTree.ODTreeNodeStatus.VALID && max!=min){
                result.status= ODTree.ODTreeNodeStatus.SPLIT;
                result.violationRows.add(min);
                result.violationRows.add(max);
            }
            if(beginPointer>=1 && maxLastValue>minvalue){
                result.status= ODTree.ODTreeNodeStatus.SWAP;
                result.violationRows.clear();
                result.violationRows.add(min);
                result.violationRows.add(maxLast);
                break;
            }
            maxLast=max;
            maxLastValue=maxvalue;
        }

        validateTime+=validateTimer.getTimeUsed();
        return result;
    }

    public ODValidationResult validateForFullViolation(DataFrame data){
        ODValidationResult result=new ODValidationResult(ODTree.ODTreeNodeStatus.VALID);
        Set<Integer> resultSet=new HashSet<>();
        int[] row2RightGroupIndex=getRow2RightGroupIndex();
        boolean resultSplit=false;
        for(int beginPointer=0;beginPointer<left.begins.size()-1;beginPointer++) {
            int groupBegin = left.begins.get(beginPointer);
            int groupEnd = left.begins.get(beginPointer + 1);
            int value =row2RightGroupIndex[left.indexes[groupBegin]];
            boolean split=false;
            for(int i=groupBegin+1;i<groupEnd;i++){
                if(row2RightGroupIndex[left.indexes[i]]!=value){
                    split=true;
                    resultSplit=true;
                    break;
                }
            }

            if(split){
                for(int i=groupBegin;i<groupEnd;i++){
                    resultSet.add(left.indexes[i]);
                }
            }
        }

        boolean resultSwap=false;
        TreeSet<Integer> previousValues=new TreeSet<>();
        for(int beginPointer=0;beginPointer<left.begins.size()-1;beginPointer++) {
            int groupBegin = left.begins.get(beginPointer);
            int groupEnd = left.begins.get(beginPointer + 1);
            for(int i=groupBegin;i<groupEnd;i++){
                if(previousValues.ceiling(1+row2RightGroupIndex[left.indexes[i]])!=null){
                    resultSet.add(left.indexes[i]);
                    resultSwap=true;
                }
            }
            for(int i=groupBegin;i<groupEnd;i++){
                previousValues.add(row2RightGroupIndex[left.indexes[i]]);
            }
        }

        TreeSet<Integer> backwardValues=new TreeSet<>();
        for(int beginPointer=left.begins.size()-2;beginPointer>=0;beginPointer--) {
            int groupBegin = left.begins.get(beginPointer);
            int groupEnd = left.begins.get(beginPointer + 1);
            for(int i=groupBegin;i<groupEnd;i++){
                if(backwardValues.floor(row2RightGroupIndex[left.indexes[i]]-1)!=null){
                    resultSet.add(left.indexes[i]);
                    resultSwap=true;
                }
            }
            for(int i=groupBegin;i<groupEnd;i++){
                backwardValues.add(row2RightGroupIndex[left.indexes[i]]);
            }
        }

        if(resultSwap){
            result.status= ODTree.ODTreeNodeStatus.SWAP;
        }else if(resultSplit){
            result.status= ODTree.ODTreeNodeStatus.SPLIT;
        }
        result.violationRows.addAll(resultSet);
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
        Timer timer=new Timer();
        ODTreeNodeEquivalenceClasses result = new ODTreeNodeEquivalenceClasses(left.deepClone(), right.deepClone());
        cloneTime+=timer.getTimeUsed();
        return result;
    }

    @Override
    public String toString() {
        return "ODTreeNodeEquivalenceClasses{" +
                "leftList=" + left +
                ", rightList=" + right +
                '}';
    }

    public ODTreeNodeEquivalenceClasses mergeNode(ODTree.ODTreeNode node, DataFrame data){
        Timer mergeTimer =new Timer();

        AttributeAndDirection attribute=node.attribute;
        if(node.parent.status== ODTree.ODTreeNodeStatus.SPLIT)
            left.merge(data,attribute);
        else
            right.merge(data,attribute);
        mergeTime+=mergeTimer.getTimeUsed();
        return this;
    }

}
