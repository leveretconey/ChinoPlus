package leveretconey.chino.sampler;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;

@SuppressWarnings("all")
public class BidirectionReverseNumberSampler extends Sampler{
    @Override
    protected Set<Integer> chooseLines(DataFrame data, int adviceSampleSize) {
        Set<Integer> result=new HashSet<>();
        int sampleCount=Math.max(10,Math.min(50,15000/data.getColumnCount()/data.getColumnCount()));
        PriorityQueue<IndexAndReverseMumber> upQueue=new PriorityQueue<>(IndexAndReverseMumber.upComparator);
        PriorityQueue<IndexAndReverseMumber> downQueue=new PriorityQueue<>(IndexAndReverseMumber.downComparator);
        for (int i = 0; i < data.getRowCount(); i++) {
            IndexAndReverseMumber indexAndReverseMumber=getIndexAndReverseNumber(data,i,sampleCount);
            upQueue.add(indexAndReverseMumber);
            downQueue.add(indexAndReverseMumber);
        }
        int half= adviceSampleSize /2;
        for (int i = 0; i < half; i++) {
            result.add(upQueue.poll().index);
        }
        while (result.size()< adviceSampleSize){
            result.add(downQueue.poll().index);
        }
        return result;
    }

    private IndexAndReverseMumber getIndexAndReverseNumber(DataFrame data,int index,int sampleCount){
        int upSwap=0,downSwap=0;
        for (int i = 0; i < sampleCount; i++) {
            int sampleIndex=random.nextInt(data.getRowCount());
            for (int c1 = 0; c1 < data.getColumnCount(); c1++) {
                for (int c2 = c1+1; c2 < data.getColumnCount(); c2++) {
                    int d1=data.get(index,c1)-data.get(sampleIndex,c1);
                    int d2=data.get(index,c2)-data.get(sampleIndex,c2);
                    if(d1==0 || d2==0){
                        upSwap++;downSwap++;
                    }else if( d1>0 == d2>0 ){
                        upSwap+=2;
                    }else {
                        downSwap+=2;
                    }
                }
            }
        }
        return new IndexAndReverseMumber(index,upSwap,downSwap);
    }

    static class IndexAndReverseMumber{
        public final int index;
        public final int upReverseNumber;
        public final int downReverseNumber;

        public static Comparator<IndexAndReverseMumber> upComparator=
                (i1,i2)->i1.upReverseNumber-i2.downReverseNumber;
        public static Comparator<IndexAndReverseMumber> downComparator=
                (i1,i2)->i2.upReverseNumber-i1.downReverseNumber;

        public IndexAndReverseMumber(int index, int upReverseNumber, int downReverseNumber) {
            this.index = index;
            this.upReverseNumber = upReverseNumber;
            this.downReverseNumber = downReverseNumber;
        }
    }
}
