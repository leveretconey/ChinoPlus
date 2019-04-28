package leveretconey.chino.validator;

import java.util.List;

import leveretconey.chino.dataStructures.ODCandidate;

public class ODPrefixBasedFullValidator extends ODPrefixBasedIncrementalValidator {

    @Override
    protected List<ODCandidate> chooseODs(List<ODCandidate> ods) {
        return ods;
    }
}
