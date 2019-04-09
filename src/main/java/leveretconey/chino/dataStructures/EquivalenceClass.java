package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import leveretconey.chino.sampler.DataAndIndex;

public class EquivalenceClass{
    public List<List<Integer>> groups=null;

    public EquivalenceClass() {
    }


    public void merge(DataFrame data,int column){
        //lazy update
        if(groups==null){
            List<Integer> list=new ArrayList<>();
            for (int row = 0; row < data.getRowCount(); row++) {
                list.add(row);
            }
            groups =new ArrayList<>();
            groups.add(list);
        }

        if(groups.size()==data.getRowCount())
            return;
        List<List<Integer>> newClasses=new ArrayList<>();
        for (List<Integer> group : groups) {
            if(group.size()==1){
                newClasses.add(group);
                continue;
            }
            List<DataAndIndex> mergeData=new ArrayList<>();
            for (Integer row : group) {
                mergeData.add(new DataAndIndex(data.get(row,column),row));
            }
            Collections.sort(mergeData);
            ArrayList<Integer> listToInsert=new ArrayList<>();
            for (int i = 0; i < mergeData.size(); i++) {
                if(i>0 && mergeData.get(i).data>mergeData.get(i-1).data){
                    listToInsert.trimToSize();
                    newClasses.add(listToInsert);
                    listToInsert=new ArrayList<>();
                }
                listToInsert.trimToSize();
                listToInsert.add(mergeData.get(i).index);
            }
            newClasses.add(listToInsert);
        }
        groups =newClasses;
    }


    @Override
    public String toString() {
        return groups.toString();
    }

    public EquivalenceClass deepClone(){
        EquivalenceClass result=new EquivalenceClass();
        if(groups!=null){
            result.groups=new ArrayList<>();
            for(List<Integer> group: groups){
                List<Integer> list=new ArrayList<>(group);
                result.groups.add(list);
            }
        }
        return result;
    }
}
