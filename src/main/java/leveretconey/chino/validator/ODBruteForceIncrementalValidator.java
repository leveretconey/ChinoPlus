package leveretconey.chino.validator;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.dataStructures.ODCandidate;

class ODBruteForceIncrementalValidator extends ODBruteForceFullValidator {
    @Override
    protected List<ODCandidate> chooseODs(List<ODCandidate> ods) {
        List<ODCandidate> result=new ArrayList<>();
        for(ODCandidate od:ods){
            if(!od.odByODTreeNode.confirm){
                result.add(od);
            }
        }
        return result;
    }
}
