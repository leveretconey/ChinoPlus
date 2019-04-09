package leveretconey.chino.validator;

import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;

public abstract class ODValidator {

    public abstract Set<Integer> validate(ODTree tree, DataFrame data);
}
