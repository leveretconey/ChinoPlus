package leveretconey.chino.sampler;

import java.util.Random;
import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.PartialDataFrame;

abstract public class Sampler {

    private static final int LOW_BOUND=5;
    private static final int UPPER_BOUND=100;

    protected Random random;
    public Sampler() {
        random=new Random();
    }

    public Sampler(long randomSeed) {
        this.random = new Random(randomSeed);
    }

    public void setRandomSeed(long randomSeed){
        random.setSeed(randomSeed);
    }


    final public PartialDataFrame sample(DataFrame data){
        int sampleRowCount=Math.max(5,Math.min(data.getRowCount()/100,100));
        return sample(data,new SampleConfig(sampleRowCount));
    }

    final public PartialDataFrame sample(DataFrame data,SampleConfig adviceConfig){
        if(data==null)
            return null;
        int dataLineCount=data.getRowCount();
        int sampleLineCount=adviceConfig.isUsePercentage()?
                (int)(dataLineCount * adviceConfig.samplePercentage):
                adviceConfig.sampleLineCount;
        if(sampleLineCount>dataLineCount)
            sampleLineCount=dataLineCount;
        Set<Integer> sampleLines=chooseLines(data,sampleLineCount);
        PartialDataFrame result=new PartialDataFrame(data,sampleLines);
        result.setColumnName(data.getColumnName());
        return result;
    }

    protected abstract Set<Integer> chooseLines(DataFrame data, int adviceSampleSize);
}
