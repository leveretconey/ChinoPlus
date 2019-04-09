package leveretconey.chino.discoverer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.util.Util;

@SuppressWarnings("all")
public class BFSODDiscovererFull extends ODDiscoverer{
    @Override
    public ODTree discover(DataFrame data, ODTree reference) {
        Queue<ODDiscovererNodeSavingInfo> queue=new LinkedList<>();
        ODTree result=new ODTree(data.getColumnCount());
        List<ODCandidate> ods=new ArrayList<>();
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
        int count=0;
        while (!queue.isEmpty()) {
            count++;
            if(count ==2000) {
                Util.out(result);
                result.toPic("output\\pic");
            }



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
