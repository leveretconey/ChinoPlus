package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ODCandidate implements Comparable<ODCandidate>{
    public ODByLeftRightAttributeList odByLeftRightAttributeList;
    public List<Integer> odByPath;
    public ODTree.ODTreeNode odByODTreeNode;

    public ODCandidate(ODTree.ODTreeNode odByODTreeNode) {
        //this can be null if im debugging
        if(odByODTreeNode==null){
            return;
        }
        this.odByODTreeNode = odByODTreeNode;
        odByPath=new ArrayList<>();
        odByLeftRightAttributeList =new ODByLeftRightAttributeList();
        ODTree.ODTreeNode node=odByODTreeNode;
        while (!node.isRoot()){
            odByPath.add(node.attribute);
            if(node.parent.status== ODTree.ODTreeNodeStatus.VALID)
                odByLeftRightAttributeList.right.add(node.attribute);
            else if(node.parent.status== ODTree.ODTreeNodeStatus.SPLIT)
                odByLeftRightAttributeList.left.add(node.attribute);
            node=node.parent;
        }
        Collections.reverse(odByPath);
        Collections.reverse(odByLeftRightAttributeList.left);
        Collections.reverse(odByLeftRightAttributeList.right);
    }

    @Override
    public int compareTo(ODCandidate o) {
        return odByPath.size()-o.odByPath.size();

    }
    @Override
    public String toString(){
        return odByLeftRightAttributeList.toString();
    }
}
