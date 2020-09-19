package leveretconey.chino.validator;

import java.util.*;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.ODValidationResult;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.dataStructures.VisitCountConsideredMap;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;

public class ODPrefixBasedIncrementalValidatorNoPath extends ODPrefixBasedIncrementalValidator{



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
           if(path.isEmpty()){
               ODValidationResult result=odTreeNodeEquivalenceClasses.validate(data);
               if(result.status!=node.status){
                   cache.addVisitCount(cacheNode);
                   updateCacheVisitCountBeforeNodeChildrenCut
                           (node,cache,nodeForNodeToGetCache);
               }else {
                    setPathConfirm(node);
               }
               node.setStatus(result.status);
               return result;
           }
       }
       //not reachable
       ODValidationResult result=new ODValidationResult();
       result.status=node.status;
       return result;
    }

    protected void setPathConfirm(ODTreeNode node){
        while (!node.confirm){
            node.confirm();
            node=node.parent;
        }
    }


}
