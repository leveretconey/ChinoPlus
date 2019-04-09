package leveretconey.chino.discoverer;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

@SuppressWarnings("all")
public class BFSODDiscovererForIteration extends ODDiscoverer{

    private boolean complete=true;
    private Queue<ODDiscovererNodeSavingInfo> queue;
    private  List<ODCandidate> ods;
    private int returnThreshold;

    public boolean isComplete() {
        return complete;
    }

    public ODTree restartDiscovering(DataFrame data,ODTree reference){
        queue=new LinkedList<>();
        ODTree result=new ODTree(data.getColumnCount());
        ods=new ArrayList<>();
        returnThreshold=1000;
        int attributeCount=data.getColumnCount();

        for (int attribute = 0; attribute < attributeCount; attribute++) {
            if(reference!=null) {
                copyConfirmNode(result, result.getRoot().children[attribute]
                        , reference.getRoot().children[attribute]);
            }
            ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses = new ODTreeNodeEquivalenceClasses();
            odTreeNodeEquivalenceClasses.mergeNode(result.getRoot().children[attribute], data);
            queue.offer(new ODDiscovererNodeSavingInfo(result.getRoot().children[attribute]
                    , null, odTreeNodeEquivalenceClasses));
        }
        BFSTraversing(data,result);
        return result;
    }

    public ODTree continueDiscovering(DataFrame data,ODTree tree){
        returnThreshold*=2;
        BFSTraversing(data, tree);
        return tree;
    }
    @Override
    public ODTree discover(DataFrame data, ODTree reference) {
        if (complete)
            return restartDiscovering(data,reference);
        else
            return continueDiscovering(data,reference);
    }

    private void BFSTraversing(DataFrame data,ODTree result){
        int attributeCount=data.getColumnCount();
        int newFoundOdCount=0;
        while (!queue.isEmpty()) {

            ODDiscovererNodeSavingInfo info=queue.poll();
            ODTreeNode parent=info.nodeInResultTree;

            for (int attribute = 0; attribute < attributeCount; attribute++) {
                ODTreeNode child=parent.children[attribute]==null?
                        result.new ODTreeNode(parent,attribute):parent.children[attribute];
                child.minimal= isODCandidateMinimal(child,ods);
                if(!child.minimal)
                    continue;
                ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses =
                        info.odTreeNodeEquivalenceClasses.deepClone();
                odTreeNodeEquivalenceClasses.mergeNode(child,data);
                if(!child.confirm)
                    child.status=odTreeNodeEquivalenceClasses.validate(data).status;
                if(child.status== ODTree.ODTreeNodeStatus.VALID){
                    ods.add(new ODCandidate(child));
                    if(!child.confirm){
                        newFoundOdCount++;
                        if(newFoundOdCount==returnThreshold){
                            complete=false;
                            return;
                        }
                    }
                }
                if(child.status!= ODTree.ODTreeNodeStatus.SWAP){
                    queue.offer(new ODDiscovererNodeSavingInfo(child
                            ,null,odTreeNodeEquivalenceClasses));
                }
            }
        }
        complete=true;
    }

    private void copyConfirmNode(ODTree resultTree,ODTreeNode resultTreeNode,ODTreeNode referenceTreeNode){
        for (int attribute = 0; attribute < referenceTreeNode.children.length; attribute++) {
            ODTreeNode referenceChildNode=referenceTreeNode.children[attribute];
            if(referenceChildNode!=null && referenceChildNode.confirm){
                ODTreeNode resultChildNode =resultTree.new ODTreeNode(resultTreeNode,attribute);
                resultChildNode.status=referenceChildNode.status;
                resultChildNode.confirm();
                copyConfirmNode(resultTree,resultChildNode,referenceChildNode);
            }
        }
    }
}
