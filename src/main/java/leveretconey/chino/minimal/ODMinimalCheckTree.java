package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.util.Timer;
import minimal.ODMinimalChecker;

public class ODMinimalCheckTree extends ODMinimalChecker {

    private int countAttribute;
    private ODMinimalCheckTreeNode root;
    public static long minimalCheckTime=0;

    @Override
    public void insert(ODCandidate candidate){
        Timer timer=new Timer();
        List<AttributeAndDirection> left=candidate.odByLeftRightAttributeList.left;
        List<AttributeAndDirection> right=candidate.odByLeftRightAttributeList.right;

        AttributeAndDirection leftLast=left.get(left.size()-1);
        AttributeAndDirection rightLast=right.get(right.size()-1);
        //first insert left->right
        if(leftLast.direction==AttributeAndDirection.DOWN){
            left=reverseDirection(left);
            right=reverseDirection(right);
        }
        ODMinimalCheckTreeNode node=findNode(left);
        if(node.rightList ==null){
            node.rightList =new ArrayList<>();
        }
        node.rightList.add(right);
        //next insert right -> left
        if(rightLast.direction!= leftLast.direction){
            if(rightLast.direction==AttributeAndDirection.DOWN) {
                left = reverseDirection(left);
                right = reverseDirection(right);
            }
            else {
                left=candidate.odByLeftRightAttributeList.left;
                right=candidate.odByLeftRightAttributeList.right;
            }
        }
        node=findNode(right);
        if(node.leftList==null){
            node.leftList=new ArrayList<>();
        }
        node.leftList.add(left);
        minimalCheckTime+=timer.getTimeUsed();
    }

    private ODMinimalCheckTreeNode findNode(List<AttributeAndDirection> list){
        ODMinimalCheckTreeNode node=root;
        for(int i=list.size()-1;i>=0;i--){
            AttributeAndDirection attributeAndDirection=list.get(i);
            int index=attributeAndDirection2childrenIndex(attributeAndDirection);
            if(node.children[index]==null){
                node=new ODMinimalCheckTreeNode(attributeAndDirection,node);
            }else {
                node=node.children[index];
            }
        }
        return node;
    }

    @Override
    protected boolean isListMinimal(List<AttributeAndDirection> list){


        if(list.get(list.size()-1).direction==AttributeAndDirection.DOWN){
            list=reverseDirection(list);
        }
        ODMinimalCheckTreeNode node=root;
        for(int i=list.size()-1;i>=0;i--){
            AttributeAndDirection attributeAndDirection=list.get(i);
            node=node.children[attributeAndDirection2childrenIndex(attributeAndDirection)];
            if(node==null)
                break;
            if(node.leftList!=null){
                for(List<AttributeAndDirection> pattern:node.leftList){
                    if(canFindBefore(list,pattern,i-pattern.size())){
                        return false;
                    }
                }
            }
            if (node.rightList != null) {
                for(List<AttributeAndDirection> pattern:node.rightList){
                    if(exactMatch(list,pattern,i-pattern.size())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ODMinimalCheckTree(int countAttribute) {
        this.countAttribute = countAttribute;
        root=new ODMinimalCheckTreeNode(new AttributeAndDirection(-1,AttributeAndDirection.UP),null);
    }
    private List<AttributeAndDirection> reverseDirection(List<AttributeAndDirection> list){
        List<AttributeAndDirection> result=new ArrayList<>();
        for (AttributeAndDirection attributeAndDirection : list) {
            result.add(new AttributeAndDirection(attributeAndDirection.attribute
                    ,-attributeAndDirection.direction));
        }
        return result;
    }

    private class ODMinimalCheckTreeNode{
        ODMinimalCheckTreeNode[] children;
        List<List<AttributeAndDirection>> leftList;
        List<List<AttributeAndDirection>> rightList;
        AttributeAndDirection attribute;

        private ODMinimalCheckTreeNode(AttributeAndDirection attribute
                ,ODMinimalCheckTreeNode parent) {
            this.attribute=attribute;
            if(parent!=null){
                int index=attributeAndDirection2childrenIndex(attribute);
                parent.children[index]=this;
            }
            children=new ODMinimalCheckTreeNode[2*countAttribute];
        }
    }

    private int attributeAndDirection2childrenIndex(AttributeAndDirection attributeAndDirection){
        return attributeAndDirection.attribute+
                (attributeAndDirection.direction==AttributeAndDirection.DOWN?countAttribute:0);
    }
}
