package leveretconey.chino.test;

import java.util.HashSet;
import java.util.Set;

import javax.xml.validation.Validator;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.discoverer.BFSODDiscovererFull;
import leveretconey.chino.discoverer.Chino;
import leveretconey.chino.discoverer.BFSODDiscovererForIteration;
import leveretconey.chino.discoverer.ChinoForDebug;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;
import leveretconey.chino.validator.BruteForceOdValidator;
import leveretconey.chino.validator.ODValidator;
import leveretconey.chino.validator.PrefixBasedValidatorForComparison;

public class TestSimpleDiscoverer {
    public static final int COUNT_ROW=100000;
    public static final int COUNT_COLUMN=30;

    public static void main(String[] args) {
        DataFrame data=DataFrame.fromCsv("datasets/no 5 2.csv");
//        data=data.subDataFrame(10000,999);
        Timer timer=new Timer();
        ODTree tree=new Chino().discover(data,null);
        Util.out(timer.getTimeUsedAndReset());

        tree=new BFSODDiscovererFull().discover(data,null);
        Util.out(timer.getTimeUsedAndReset());
    }
}
