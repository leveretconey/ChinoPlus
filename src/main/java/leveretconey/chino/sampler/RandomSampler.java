package leveretconey.chino.sampler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;

public class RandomSampler extends Sampler{
    public static final double ALGORITHM_CHOOSE_THRESHOLD=0.1d;

    public RandomSampler() {
    }


    @Override
    protected Set<Integer> chooseLines(DataFrame data, int adviceSampleSize) {
        Set<Integer> result=new HashSet<>();
        if((double) adviceSampleSize / data.getRowCount() < ALGORITHM_CHOOSE_THRESHOLD){
            for (int i = 0; i < adviceSampleSize; i++) {
                int row;
                do {
                    row=random.nextInt(data.getRowCount());
                }while (result.contains(row));
                result.add(row);
            }
        }
        else {
            List<Integer> list=new ArrayList<>();
            for (int i = 0; i < data.getRowCount(); i++) {
                list.add(i);
            }

            Collections.shuffle(list,random);
            for (int i = 0; i < adviceSampleSize; i++) {
                result.add(list.get(i));
            }
        }
        return result;
    }
}
