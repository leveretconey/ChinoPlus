package leveretconey.chino.discoverer;

import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.PartialDataFrame;
import leveretconey.chino.sampler.RandomSampler;
import leveretconey.chino.sampler.SampleBasedReverseNumberBasedSampler;
import leveretconey.chino.sampler.SampleConfig;
import leveretconey.chino.sampler.Sampler;
import leveretconey.chino.validator.ODValidator;
import leveretconey.chino.validator.PrefixBasedValidator;

public class Chino extends ODDiscoverer{

    private Sampler sampler;
    private ODValidator validator;


    private Chino(Sampler sampler,ODValidator validator) {
        this.sampler = sampler;
        this.validator = validator;
    }

    public Chino() {
        this(new SampleBasedReverseNumberBasedSampler(),new PrefixBasedValidator());
    }

    @Override
    public ODTree discover(DataFrame data, ODTree reference) {
        ODTree odTree=new ODTree(data.getColumnCount());
        PartialDataFrame sampledData=sampler.sample(data,
                new SampleConfig(Math.max(5,Math.min(data.getRowCount()/100,50))));
        while (true){
            BFSODDiscovererForIteration discoverer=new BFSODDiscovererForIteration();
            while (true){
                odTree=discoverer.discover(sampledData,odTree);
                Set<Integer> violateRowIndexes=validator.validate(odTree,data);
                if(violateRowIndexes.size()==0){
                    if(discoverer.isComplete()){
                        return odTree;
                    }
                }else {
                    sampledData.addRows(violateRowIndexes);
                    break;
                }
            }
        }
    }
}
