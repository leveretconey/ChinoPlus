package leveretconey.chino.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.ODValidationResult;

public class BruteForceOdValidator extends ODValidator{

    @Override
    public Set<Integer> validate(ODTree tree,DataFrame data){
        Set<Integer> result =new HashSet<>();
        for(ODCandidate od: tree.getAllOdsOrderByBFS()){
            result.addAll(validateOneOD(tree,od,data).violationRows);
        }
        return result;
    }

    public ODValidationResult validateOneOD(ODTree tree, ODCandidate od, DataFrame data){
        return getEquivalenceClassFromODCandidate(od,data).validate(data);
    }

    public ODTreeNodeEquivalenceClasses getEquivalenceClassFromODCandidate(ODCandidate od, DataFrame data){
        return getEquivalenceClassFromTwoLists(od.odByLeftRightAttributeList.left,
                od.odByLeftRightAttributeList.right,data);

    }

    public ODTreeNodeEquivalenceClasses getEquivalenceClassFromTwoLists
            (List<Integer> left,List<Integer> right,DataFrame data){
        ODTreeNodeEquivalenceClasses equivalenceClasses
                =new ODTreeNodeEquivalenceClasses();
        for (int column : left) {
            equivalenceClasses.left.merge(data,column);
        }
        for (int column : right) {
            equivalenceClasses.right.merge(data,column);
        }
        return equivalenceClasses;
    }
}
