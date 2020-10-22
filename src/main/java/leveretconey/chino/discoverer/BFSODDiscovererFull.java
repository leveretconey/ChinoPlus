package leveretconey.chino.discoverer;

import java.util.LinkedList;
import java.util.Queue;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.minimal.ODMinimalCheckTree;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.minimal.ODMinimalChecker;
import leveretconey.chino.util.Util;

public class BFSODDiscovererFull extends ODDiscoverer{
    @Override
    public ODTree discover(DataFrame data, ODTree reference) {
        int visitNodeCount=0;
        Queue<ODDiscovererNodeSavingInfo> queue=new LinkedList<>();
        ODTree result=new ODTree(data.getColumnCount());
        int attributeCount=data.getColumnCount();
        ODMinimalChecker odMinimalChecker=new ODMinimalCheckTree(data.getColumnCount());

        //note that the direction of all nodes in the second level are always UP
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
        while (!queue.isEmpty()) {
            ODDiscovererNodeSavingInfo info=queue.poll();
            ODTreeNode parent=info.nodeInResultTree;
            visitNodeCount++;
            Util.out(String.format("当前访问到第%d个结点%s",visitNodeCount,parent));

            for (int attribute = 0; attribute < attributeCount*2; attribute++) {
                ODTreeNode child;
                if(parent.children[attribute]==null)
                    child=result.new ODTreeNode(parent,result.childrenIndex2AttributeAndDirection(attribute));
                else
                    child=parent.children[attribute];
                ODCandidate childCandidate=new ODCandidate(child);
                child.minimal= odMinimalChecker.isCandidateMinimal(childCandidate);
                if(!child.minimal)
                    continue;
                ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses =
                        info.odTreeNodeEquivalenceClasses.deepClone();
                odTreeNodeEquivalenceClasses.mergeNode(child,data);
                if(!child.confirm)
                    child.status=odTreeNodeEquivalenceClasses.validate(data).status;
                if(child.status== ODTree.ODTreeNodeStatus.VALID){
                    odMinimalChecker.insert(childCandidate);
                }
                if(child.status!= ODTree.ODTreeNodeStatus.SWAP){
                    queue.offer(new ODDiscovererNodeSavingInfo(child
                            ,null,odTreeNodeEquivalenceClasses));
                }
            }
        }
        return result;
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
