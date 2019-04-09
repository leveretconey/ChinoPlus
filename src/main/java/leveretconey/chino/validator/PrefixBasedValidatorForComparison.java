package leveretconey.chino.validator;

import java.util.List;
import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;

public class PrefixBasedValidatorForComparison extends PrefixBasedValidator {

    @Override
    protected List<ODCandidate> chooseODs(List<ODCandidate> ods) {
        return ods;
    }
}
