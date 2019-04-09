package leveretconey.chino.sampler;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;

public abstract class ReverseNumberBasedSampler extends Sampler {
    public ReverseNumberBasedSampler() {
    }

    @SuppressWarnings("all")
    @Override
    final protected Set<Integer> chooseLines(DataFrame originalData, int sampleLineCount) {
        PriorityQueue<RowAndReverseNumber> queue=new PriorityQueue<>();
        for (int i = 0; i < originalData.getRowCount(); i++) {
            int reverseNumber=getReverseNumber(originalData,i);
            queue.add(new RowAndReverseNumber(i,reverseNumber));
        }
        Set<Integer> result=new HashSet<>();
        for (int i = 0; i < sampleLineCount; i++) {
            result.add(queue.poll().row);
        }
        return result;
    }

    abstract protected int getReverseNumber(DataFrame dataFrame,int row);

    protected class RowAndReverseNumber implements Comparable<RowAndReverseNumber>{
        public int row;
        public int reverseNumber;

        @Override
        public int compareTo(RowAndReverseNumber o) {
            return o.reverseNumber-this.reverseNumber;
        }

        public RowAndReverseNumber(int row, int reverseNumber) {
            this.row = row;
            this.reverseNumber = reverseNumber;
        }

        @Override
        public String toString() {
            return "RowAndReverseNumber{" +
                    "row=" + row +
                    ", reverseNumber=" + reverseNumber +
                    '}';
        }
    }
    protected int getReverseNumberInTwoRows(DataFrame dataFrame,int row1,int row2){
        int result=0;
        for(int column1=0;column1<dataFrame.getColumnCount();column1++){
            for(int column2=column1+1;column2<dataFrame.getColumnCount();column2++){
                int minus1=dataFrame.get(row1,column1)-dataFrame.get(row2,column1);
                int minus2=dataFrame.get(row1,column2)-dataFrame.get(row2,column2);
                if(minus1==0 || minus2==0)
                    result+=1;
                else if( (minus1>0 && minus2<0) || (minus1<0 && minus2>0) ){
                    result+=2;
                }
            }
        }
        return result;
    }
}
