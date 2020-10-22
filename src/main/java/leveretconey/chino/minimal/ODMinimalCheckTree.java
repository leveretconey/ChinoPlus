package leveretconey.chino.minimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import leveretconey.chino.dataStructures.AttributeAndDirection;
import leveretconey.chino.dataStructures.ODCandidate;


public class ODMinimalCheckTree extends ODMinimalChecker {


    private int countAttribute;
    private ODMinimalCheckTreeNode root;


    @Override
    public void insert(ODCandidate candidate){
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

        HashMap<Integer,Integer> attribute2Index=new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            attribute2Index.put(list.get(i).attribute,i);
        }
        if(list.get(list.size()-1).direction==AttributeAndDirection.DOWN){
            list=reverseDirection(list);
        }
        ODMinimalCheckTreeNode node=root;
        for(int i=list.size()-1;i>=0;i--){
            AttributeAndDirection attributeAndDirection=list.get(i);
            node=node.children[attributeAndDirection2childrenIndex(attributeAndDirection)];
            if(node==null)
                break;
            //case XLYR
            if(node.leftList!=null){
                for(List<AttributeAndDirection> pattern:node.leftList){
                    int leftBegin=attribute2Index.getOrDefault(pattern.get(0).attribute,-1);
                    if(leftBegin!=-1 && leftBegin+pattern.size()<=i && exactMatch(list,pattern,leftBegin))
                        return false;
                }
            }
            //case XRL
            if (node.rightList != null) {
                for(List<AttributeAndDirection> pattern:node.rightList){
                    int rightBegin=attribute2Index.getOrDefault(pattern.get(0).attribute,-1);
                    if(rightBegin!=-1 && rightBegin+pattern.size()==i && exactMatch(list,pattern,rightBegin))
                        return false;
                }
            }
        }
        return true;
    }

    public ODMinimalCheckTree(int countAttribute) {
        this.countAttribute = countAttribute;
        root=new ODMinimalCheckTreeNode(AttributeAndDirection.getInstance(0,AttributeAndDirection.UP),null);
    }

    private class ODMinimalCheckTreeNode{
        ODMinimalCheckTreeNode[] children;
        List<List<AttributeAndDirection>> leftList;
        List<List<AttributeAndDirection>> rightList;

        private ODMinimalCheckTreeNode(AttributeAndDirection attribute
                ,ODMinimalCheckTreeNode parent) {
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
