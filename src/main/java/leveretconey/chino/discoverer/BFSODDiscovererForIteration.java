package leveretconey.chino.discoverer;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.minimal.ODMinimalCheckTree;
import leveretconey.chino.minimal.ODMinimalChecker;
import leveretconey.chino.minimal.ODMinimalCheckerBruteForce;
import leveretconey.chino.util.Util;

import java.util.LinkedList;
import java.util.Queue;

public class BFSODDiscovererForIteration extends ODDiscoverer{

    public static final int INITIAL_RETURN_THRESHOLD =1000;
    private boolean complete=true;
    private Queue<ODDiscovererNodeSavingInfo> queue;
    private ODMinimalChecker odMinimalChecker;
    private int returnThreshold;


    public boolean isComplete() {
        return complete;
    }

    public ODTree restartDiscovering(DataFrame data,ODTree reference){
        odMinimalChecker=new ODMinimalCheckTree(data.getColumnCount());
        queue=new LinkedList<>();
        ODTree result=new ODTree(data.getColumnCount());

        returnThreshold= INITIAL_RETURN_THRESHOLD;
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

            for (int attribute = 0; attribute < attributeCount*2; attribute++) {
                ODTreeNode child;
                if(parent.children[attribute]==null)
                    child=result.new ODTreeNode(parent,result.childrenIndex2AttributeAndDirection(attribute));
                else
                    child=parent.children[attribute];
                ODCandidate childCandidate=new ODCandidate(child);
                child.minimal=odMinimalChecker.isCandidateMinimal(childCandidate);
                if(!child.minimal)
                    continue;
                ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses =
                        info.odTreeNodeEquivalenceClasses.deepClone();
                odTreeNodeEquivalenceClasses.mergeNode(child,data);
                if(!child.confirm)
                    child.status=odTreeNodeEquivalenceClasses.validate(data).status;
                if(child.status!= ODTree.ODTreeNodeStatus.SWAP){
                    queue.offer(new ODDiscovererNodeSavingInfo(child
                            ,null,odTreeNodeEquivalenceClasses));
                }
                if(child.status== ODTree.ODTreeNodeStatus.VALID){
                    odMinimalChecker.insert(childCandidate);
                    if(!child.confirm){
                        newFoundOdCount++;
                    }
                }
            }
            if(newFoundOdCount>=returnThreshold){
                complete=false;
                return;
            }
        }
        complete=true;
    }

    private void copyConfirmNode(ODTree resultTree,ODTreeNode resultTreeNode,ODTreeNode referenceTreeNode){
        for (ODTreeNode referenceChildNode:referenceTreeNode.children) {
            if(referenceChildNode!=null && referenceChildNode.confirm){
                ODTreeNode resultChildNode =resultTree.new ODTreeNode
                        (resultTreeNode,referenceChildNode.attribute);
                resultChildNode.status=referenceChildNode.status;
                resultChildNode.confirm();
                copyConfirmNode(resultTree,resultChildNode,referenceChildNode);
            }
        }
    }
}
