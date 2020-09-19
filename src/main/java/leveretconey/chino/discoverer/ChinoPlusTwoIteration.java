package leveretconey.chino.discoverer;

import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.PartialDataFrame;
import leveretconey.chino.sampler.OneLevelCheckingSampler;
import leveretconey.chino.sampler.Sampler;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;
import leveretconey.chino.validator.ODPrefixBasedIncrementalValidator;
import leveretconey.chino.validator.ODPrefixBasedIncrementalValidatorAllViolation;
import leveretconey.chino.validator.ODValidator;

public class ChinoPlusTwoIteration extends ChinoPlus{


    public ChinoPlusTwoIteration() {
        this(false);
    }
    public ChinoPlusTwoIteration(boolean printDebugInfo) {
        this(new OneLevelCheckingSampler(),new ODPrefixBasedIncrementalValidator(),printDebugInfo);
    }


    private ChinoPlusTwoIteration(Sampler sampler, ODValidator validator, boolean printDebugInfo) {
        super(sampler, validator, printDebugInfo);
    }

    @Override
    public ODTree discover(DataFrame data, ODTree reference) {
        Timer fullTimer=new Timer();
        PartialDataFrame sampledData=sampler.sample(data);
        Timer timer=new Timer();
        ODTree tree=new BFSODDiscovererForIteration().discover(sampledData);
            out("第一轮发现用时："+timer.getTimeUsedAndReset()/1000.0+"s");
            out("od数量:"+tree.getAllOdsOrderByDFS().size());
        Set<Integer> violationRows=new ODPrefixBasedIncrementalValidatorAllViolation().validate(tree,data);
            out("验证用时:"+timer.getTimeUsedAndReset()/1000.0+"s");
            out("冲突集大小:"+violationRows.size());
        sampledData.addRows(violationRows);
            tree=new BFSODDiscovererFull().discover(data,tree);
            out("第2轮发现用时："+timer.getTimeUsedAndReset()/1000.0+"s");
            out("算法结束");
            out("od个数:"+tree.getAllOdsOrderByBFS().size());
            out("总用时:"+fullTimer.getTimeUsed()/1000.0+"s");
        return tree;
    }
}
