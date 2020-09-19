package leveretconey.chino.sampler;

import java.util.HashSet;
import java.util.Set;

import leveretconey.chino.dataStructures.AttributeAndDirection;
import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.EquivalenceClass;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.PartialDataFrame;

public class OneLevelCheckingSampler extends Sampler{
    private static final int LOW_BOUND=10;
    private static final int UPPER_BOUND=100;
    long seed=-1;

    @Override
    protected Set<Integer> chooseLines(DataFrame data, int adviceSampleSize) {
        Set<Integer> result=new HashSet<>();
        int rowCount=data.getRowCount();
        int columnCount=data.getColumnCount();
        if(rowCount<=LOW_BOUND){
            for (int i = 0; i < rowCount; i++) {
                result.add(i);
            }
            return result;
        }

        int candidateSize=Math.min(rowCount,UPPER_BOUND);
        PartialDataFrame candidateData;
        RandomSampler sampler=new RandomSampler();
        if(seed!=-1){
            sampler.setRandomSeed(seed);
        }
        candidateData=sampler.sample(data,new SampleConfig(candidateSize));


        EquivalenceClass[][] equivalenceClasses=new EquivalenceClass[columnCount][2];
        for (int column = 0; column < columnCount; column++) {
            equivalenceClasses[column][0]=new EquivalenceClass();
            equivalenceClasses[column][0].merge(candidateData,
                    AttributeAndDirection.getInstance(column,AttributeAndDirection.UP));
            equivalenceClasses[column][1]=new EquivalenceClass();
            equivalenceClasses[column][1].merge(candidateData,
                    AttributeAndDirection.getInstance(column,AttributeAndDirection.DOWN));
        }

        for (int c1 = 0; c1 < columnCount; c1++) {
            for (int c2 = c1+1; c2 < columnCount; c2++) {
                result.addAll(new ODTreeNodeEquivalenceClasses
                        (equivalenceClasses[c1][0],equivalenceClasses[c2][0])
                        .validate(candidateData).violationRows);
                result.addAll(new ODTreeNodeEquivalenceClasses
                        (equivalenceClasses[c1][0],equivalenceClasses[c2][1])
                        .validate(candidateData).violationRows);
            }
        }
        Set<Integer> realResult=new HashSet<>();
        for (Integer i : result) {
            realResult.add(candidateData.getRealIndex(i));
        }
        return realResult;
    }

    @Override
    public void setRandomSeed(long randomSeed) {
        seed=randomSeed;
    }
}
