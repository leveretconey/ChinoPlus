package leveretconey.chino.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.discoverer.ChinoPlus;
import leveretconey.chino.sampler.BidirectionReverseNumberSampler;
import leveretconey.chino.sampler.OneLevelCheckingSampler;
import leveretconey.chino.sampler.RandomSampler;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;
import leveretconey.chino.validator.ODPrefixBasedIncrementalValidator;

public class TestDiscoverer {
    @Test
    public void testSampler(){
        int experimentCount=10;
        boolean simplifiedMessage=true;
        DataFrame data=DataFrame.fromCsv("datasets/letter 20000 17.csv");
        Util.out("swap check");
        for (int i = 0; i < experimentCount; i++) {
            Timer timer=new Timer();
            ODTree tree=new ChinoPlus(new OneLevelCheckingSampler(), new ODPrefixBasedIncrementalValidator(),!simplifiedMessage)
                    .discover(data,null);
            if(simplifiedMessage)
                timer.outTimeAndReset();
        }
        Util.out("--------------");
        Util.out("random");
        for (int i = 0; i < experimentCount; i++) {
            Timer timer=new Timer();
            ODTree tree=new ChinoPlus(new RandomSampler(), new ODPrefixBasedIncrementalValidator(),!simplifiedMessage)
                    .discover(data,null);
            if(simplifiedMessage)
                timer.outTimeAndReset();
        }
        Util.out("--------------");
        Util.out("reverse number");
        for (int i = 0; i < experimentCount; i++) {
            Timer timer=new Timer();
            ODTree tree=new ChinoPlus(new BidirectionReverseNumberSampler(), new ODPrefixBasedIncrementalValidator(),!simplifiedMessage)
                    .discover(data);
            if(simplifiedMessage)
                timer.outTimeAndReset();
        }
    }

    @Test
    public void testGc(){
        DataFrame data=DataFrame.fromCsv("datasets/50w 8.csv");
        ODTree tree=new ChinoPlus(true)
                .discover(data);
    }

    @Test
    public void makeExample(){
        DataFrame data=DataFrame.fromCsv("datasets/50w 8.csv");
        ODTree tree=new ChinoPlus(new RandomSampler(),new ODPrefixBasedIncrementalValidator(),true)
                .discover(data);
        Util.out(singleDirectionOD(tree.getAllOdsOrderByDFS()).size());
    }

    private static List<ODCandidate> singleDirectionOD(List<ODCandidate> ods){
        List<ODCandidate> result=new ArrayList<>();
        for(ODCandidate od:ods){
            if(od.isSingleDirection())
                result.add(od);
        }
        return result;
    }
}
