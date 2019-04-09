package leveretconey.chino.sampler;

import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.PartialDataFrame;

abstract public class Sampler {

    public Sampler() {
    }
    final public PartialDataFrame sample(DataFrame originalData,SampleConfig config){
        if(originalData==null)
            return null;
        int dataLineCount=originalData.getRowCount();
        int sampleLineCount=config.isUsePercentage()?
                (int)(dataLineCount * config.samplePercentage):
                config.sampleLineCount;
        if(sampleLineCount>dataLineCount)
            sampleLineCount=dataLineCount;
        Set<Integer> sampleLines=chooseLines(originalData,sampleLineCount);
        PartialDataFrame sampleResult=new PartialDataFrame(originalData,sampleLines);
        sampleResult.setColumnName(originalData.getColumnName());

        return sampleResult;
    }

    protected abstract Set<Integer> chooseLines(DataFrame input, int sampleLineCount);
}
