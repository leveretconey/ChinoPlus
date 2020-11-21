package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import leveretconey.chino.sampler.DataAndIndex;

public class EquivalenceClass{
    public int[] indexes;
    public List<Integer> begins;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null) return false;
        if (!(o instanceof EquivalenceClass)) return false;
        EquivalenceClass that = (EquivalenceClass) o;
        if( this.begins == that.begins) return true;
        if(this.begins == null || that.begins == null) return false;
        return this.begins.size() == that.begins.size();
    }

    private final static Comparator<DataAndIndex> reverseComparator=(i1, i2)->i2.data-i1.data;
    private final static Comparator<DataAndIndex> normalComparator=(i1,i2)->i1.data-i2.data;

    public EquivalenceClass() {
    }

    public boolean initialized(){
        return indexes!=null;
    }

    private void initialize(DataFrame data){
        int countRow=data.getRowCount();
        indexes=new int[countRow];
        for (int i = 0; i < countRow; i++) {
            indexes[i]=i;
        }
        begins=new ArrayList<>();
        begins.add(0);
        begins.add(countRow);

    }


    public EquivalenceClass merge(DataFrame data,AttributeAndDirection attributeAndDirection){
        //lazy update
        if(!initialized()){
            initialize(data);
        }

        int column=attributeAndDirection.attribute;
        Comparator<DataAndIndex> comparator=attributeAndDirection.direction==AttributeAndDirection.UP
                ?normalComparator:reverseComparator;

        if(begins.size()==data.getRowCount()+1)
            return this;
        List<Integer> newBegins=new ArrayList<>();
        for(int beginPointer=0;beginPointer<begins.size()-1;beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);
            if(groupBegin==groupEnd-1){
                newBegins.add(groupBegin);
                continue;
            }
            int value=0;
            try {
                value=data.get(indexes[groupBegin],column);
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean same=true;
            List<DataAndIndex> mergeData=new ArrayList<>();
            for(int i=groupBegin;i<groupEnd;i++){
                int row=indexes[i];
                int rowValue=data.get(row,column);
                if(rowValue!=value){
                    same=false;
                }
                mergeData.add(new DataAndIndex(rowValue,row));
            }
            if(same){
                newBegins.add(groupBegin);
                continue;
            }
            mergeData.sort(comparator);
            int fillPointer=groupBegin;
            for (int i = 0; i < mergeData.size(); i++) {
                if(i==0 || mergeData.get(i-1).data!=mergeData.get(i).data){
                    newBegins.add(fillPointer);
                }
                indexes[fillPointer]=mergeData.get(i).index;
                fillPointer++;
            }
        }
        begins=newBegins;
        begins.add(data.getRowCount());
        return this;
    }


    @Override
    public String toString() {
        return "EquivalenceClass{" +
                "indexes=" + Arrays.toString(indexes) +
                ", begins=" + begins +
                '}';
    }

    public EquivalenceClass deepClone(){
        EquivalenceClass result=new EquivalenceClass();
        if(initialized()) {
            result.indexes = Arrays.copyOf(indexes, indexes.length);
            result.begins = new ArrayList<>(begins);
        }
        return result;
    }
}
