package leveretconey.chino.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.ODValidationResult;
import leveretconey.chino.dataStructures.VisitCountConsideredMap;

public class ODPrefixBasedIncrementalValidatorAllViolation extends ODPrefixBasedIncrementalValidator{


    @Override
    protected ODValidationResult validateOneOdCandidate(DataFrame data, ODTree.ODTreeNode node,
                                                        VisitCountConsideredMap<ODTreeNode,ODTreeNodeEquivalenceClasses> cache,
                                                        HashMap<ODTreeNode,ODTreeNode> nodeForNodeToGetCache
    ){
        if(!node.accessible()){
            return new ODValidationResult();
        }
        Stack<ODTreeNode> path=new Stack<>();
        ODTreeNode cacheNode=nodeForNodeToGetCache.get(node);
        do {
            path.push(node);
            node=node.parent;
        }while (node!=cacheNode);
        ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses=
                cache.get(node);
        if(cache.containKey(node)){
            odTreeNodeEquivalenceClasses=odTreeNodeEquivalenceClasses.deepClone();
        }
        while(!path.isEmpty()){
            node=path.pop();
            odTreeNodeEquivalenceClasses.mergeNode(node,data);
            if(cache.mayPut(node))
                cache.put(node,odTreeNodeEquivalenceClasses.deepClone());
            if(!node.confirm){
                node.confirm();
                ODValidationResult result=odTreeNodeEquivalenceClasses.validateForFullViolation(data);
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
}
