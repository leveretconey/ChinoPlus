package leveretconey.chino;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.discoverer.ChinoPlus;
import leveretconey.chino.sampler.OneLevelCheckingSampler;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;
import leveretconey.chino.validator.ODPrefixBasedIncrementalValidator;
import leveretconey.chino.validator.ODPrefixBasedIncrementalValidatorFDMinimal;

class Main{
    public static void main(String[] args) {

        DataFrame dataFrame=DataFrame.fromCsv("integer datasets/DBTESMA 250000 30.csv");
        Timer timer=new Timer();
        Util.out("原始版本");
        ODTree discover = new ChinoPlus(
                new OneLevelCheckingSampler(),
                new ODPrefixBasedIncrementalValidator(),
                true).discover(dataFrame);
        timer.outTimeAndReset();
        List<ODCandidate> ods = discover.getAllOdsOrderByBFS();
        Util.out(ods.size());
        Util.out("");

        timer=new Timer();
        Util.out("用FD检查minimal list的版本");
        discover = new ChinoPlus(
                new OneLevelCheckingSampler(),
                new ODPrefixBasedIncrementalValidatorFDMinimal(),
                true).discover(dataFrame);
        timer.outTimeAndReset();
        ods = discover.getAllOdsOrderByBFS();
        Util.out(ods.size());
    }
}
