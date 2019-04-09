package leveretconey.chino.validator;

import java.util.*;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.ODValidationResult;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.VisitCountConsideredMap;
import leveretconey.chino.util.Util;

public class PrefixBasedValidator extends ODValidator{

    @Override
    public Set<Integer> validate(ODTree tree, DataFrame data) {
        VisitCountConsideredMap<ODTreeNode,ODTreeNodeEquivalenceClasses> cache
                =new VisitCountConsideredMap<>();
        Set<Integer> violationRows=new HashSet<>();
        List<ODCandidate> ods=tree.getAllOdsOrderByDFS();
        List<ODCandidate> notConfirmedODs= chooseODs(ods);

        if(notConfirmedODs.size()==0){
            return violationRows;
        }

        HashMap<ODTreeNode,ODTreeNode> nodeForNodeToGetCache=
                predictCacheVisitCount(cache,notConfirmedODs);
        cache.put(tree.getRoot(),new ODTreeNodeEquivalenceClasses());

        for(ODCandidate od:notConfirmedODs){
            ODValidationResult result= validateOneOdCandidate
                    (data,od.odByODTreeNode,cache,nodeForNodeToGetCache);
            violationRows.addAll(result.violationRows);
        }
        return violationRows;
    }

    protected List<ODCandidate> chooseODs(List<ODCandidate> ods){
        List<ODCandidate> result=new ArrayList<>();
        for (ODCandidate od:ods){
            if(!od.odByODTreeNode.confirm){
                result.add(od);
            }
        }
        return result;
    }

    private HashMap<ODTreeNode,ODTreeNode> predictCacheVisitCount(VisitCountConsideredMap<ODTreeNode,ODTreeNodeEquivalenceClasses>
                                             cache, List<ODCandidate> ods){
        HashSet<ODTreeNode> possibleCacheNodes=new HashSet<>();
        HashMap<ODTreeNode,ODTreeNode> result=new HashMap<>();
        possibleCacheNodes.add(ods.get(0).odByODTreeNode.getRoot());
        for(ODCandidate od:ods){
            ODTreeNode odNode=od.odByODTreeNode,node=odNode;
            while (!possibleCacheNodes.contains(node)){
                possibleCacheNodes.add(node);
                node=node.parent;
            }
            cache.addVisitCount(node);
            result.put(odNode,node);
        }
        return result;
    }

    private ODValidationResult validateOneOdCandidate(DataFrame data, ODTree.ODTreeNode node,
                                                      VisitCountConsideredMap<ODTreeNode,ODTreeNodeEquivalenceClasses> cache,
                                                      HashMap<ODTreeNode,ODTreeNode> nodeForNodeToGetCache
    ){
       if(!node.accessible()){
            return new ODValidationResult();
       }
       Stack<Integer> path=new Stack<>();
       ODTreeNode cacheNode=nodeForNodeToGetCache.get(node);
       do {
           path.push(node.attribute);
           node=node.parent;
       }while (node!=cacheNode);
       ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses=
               cache.get(node);
       if(cache.containKey(node)){
           odTreeNodeEquivalenceClasses=odTreeNodeEquivalenceClasses.deepClone();
       }
       while(!path.isEmpty()){
           int attribute=path.pop();
           node=node.children[attribute];
           odTreeNodeEquivalenceClasses.mergeNode(node,data);
           if(cache.mayPut(node))
               cache.put(node,odTreeNodeEquivalenceClasses.deepClone());
           if(!node.confirm){
               node.confirm();
               ODValidationResult result=odTreeNodeEquivalenceClasses.validate(data);
               if(result.status!=node.status){
                   cache.addVisitCount(cacheNode);
                   updateCacheVisitCountBeforeNodeChildrenCut
                           (node,cache,nodeForNodeToGetCache);
                   node.setStatus(result.status);
                   result.status= ODTree.ODTreeNodeStatus.UNKNOWN;
                   return result;
               }
           }
       }
       ODValidationResult result=new ODValidationResult();
       result.status=node.status;
       return result;
    }

    private void updateCacheVisitCountBeforeNodeChildrenCut(ODTreeNode nodeToCutChildren,
                                                    VisitCountConsideredMap<ODTreeNode,ODTreeNodeEquivalenceClasses> cache,
                                                    HashMap<ODTreeNode,ODTreeNode> nodeForNodeToGetCache
    ){
        List<ODCandidate> ods=new ArrayList<>();
        nodeToCutChildren.getAllODsOrderByDFSRecursion(ods,node -> node.minimal && node.status== ODTree.ODTreeNodeStatus.VALID);
        for (ODCandidate od : ods) {
            cache.addVisitCount(nodeForNodeToGetCache.get(od.odByODTreeNode),-1);
        }
    }

}
