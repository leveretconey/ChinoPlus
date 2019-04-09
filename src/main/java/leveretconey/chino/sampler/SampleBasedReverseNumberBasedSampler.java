package leveretconey.chino.sampler;

import java.util.Random;

import leveretconey.chino.dataStructures.DataFrame;

public class SampleBasedReverseNumberBasedSampler extends ReverseNumberBasedSampler{
    public SampleBasedReverseNumberBasedSampler() {
    }


    @Override
    protected int getReverseNumber(DataFrame dataFrame, int row) {
        int result=0;
        int max_sample_times=Math.min(Math.max(5,10000/(dataFrame.getColumnCount()*dataFrame.getColumnCount())),40);
        int sampleCount=Math.min(max_sample_times,dataFrame.getRowCount()-1);
        Random random=new Random();
        while (sampleCount>0){
            int sampleRow=random.nextInt(dataFrame.getRowCount());
            if(sampleRow!=row){
                sampleCount--;
                result+=getReverseNumberInTwoRows(dataFrame,row,sampleRow);
            }
        }
        return result;
    }
}
