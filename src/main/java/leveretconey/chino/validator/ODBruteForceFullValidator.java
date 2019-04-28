package leveretconey.chino.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import leveretconey.chino.dataStructures.AttributeAndDirection;
import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.ODValidationResult;
import leveretconey.chino.util.Util;

public class ODBruteForceFullValidator extends ODValidator{

    @Override
    public Set<Integer> validate(ODTree tree,DataFrame data){
        Set<Integer> result =new HashSet<>();
        List<ODCandidate> ods=tree.getAllOdsOrderByDFS();
        ods=chooseODs(ods);
        for(ODCandidate od: ods){
            result.addAll(validateOneOD(od,data).violationRows);
        }
        return result;
    }
    protected List<ODCandidate> chooseODs(List<ODCandidate> ods){
        return ods;
    }

    public ODValidationResult validateOneOD(ODCandidate od, DataFrame data){
        return getEquivalenceClassFromODCandidate(od,data).validate(data);
    }

    public ODTreeNodeEquivalenceClasses getEquivalenceClassFromODCandidate(ODCandidate od, DataFrame data){
        return getEquivalenceClassFromTwoLists(od.odByLeftRightAttributeList.left,
                od.odByLeftRightAttributeList.right,data);
    }

    public ODTreeNodeEquivalenceClasses getEquivalenceClassFromTwoLists
            (List<AttributeAndDirection> left, List<AttributeAndDirection> right, DataFrame data){
        ODTreeNodeEquivalenceClasses equivalenceClasses
                =new ODTreeNodeEquivalenceClasses();
        for (AttributeAndDirection column : left) {
            equivalenceClasses.left.merge(data,column);
        }
        for (AttributeAndDirection column : right) {
            equivalenceClasses.right.merge(data,column);
        }
        return equivalenceClasses;
    }
}
