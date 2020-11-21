package leveretconey.chino.discoverer;

import java.util.LinkedList;
import java.util.Queue;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.minimal.ODMinimalCheckTree;
import leveretconey.chino.minimal.ODMinimalChecker;

public class BFSODDiscovererFullFD extends ODDiscoverer{
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
            ODTreeNodeEquivalenceClasses parentEc = info.odTreeNodeEquivalenceClasses;
            visitNodeCount++;
//            Util.out(String.format("当前访问到第%d个结点%s",visitNodeCount,parent));

            for (int attribute = 0; attribute < attributeCount*2; attribute++) {
                ODTreeNode child;
                if(parent.children[attribute]==null)
                    child=result.new ODTreeNode(parent,result.childrenIndex2AttributeAndDirection(attribute));
                else
                    child=parent.children[attribute];
                ODCandidate childCandidate=new ODCandidate(child);
                if(childCandidate.toString().equals("1↑,2↑~>[<=,lex]5↑")){
                    //todo debug
                    int x=1;
                }
                child.minimal= odMinimalChecker.isCandidateMinimal(childCandidate);
                if(!child.minimal)
                    continue;
                ODTreeNodeEquivalenceClasses childEc = parentEc.deepClone();
                childEc.mergeNode(child,data);
                child.minimal= !childEc.equals(parentEc);
                if(!child.minimal)
                    continue;
                if(!child.confirm)
                    child.status=childEc.validate(data).status;
                if(child.status== ODTree.ODTreeNodeStatus.VALID){
                    odMinimalChecker.insert(childCandidate);
                }
                if(child.status!= ODTree.ODTreeNodeStatus.SWAP){
                    queue.offer(new ODDiscovererNodeSavingInfo(child
                            ,null,childEc));
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
