package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PartialDataFrame extends DataFrame {
    private Set<Integer> rowIndexes;
    private List<Integer> realIndexes;
    private DataFrame originalDataFrame;

    public PartialDataFrame(DataFrame dataFrame, Set<Integer> rowIndexes) {
        this.originalDataFrame=dataFrame;
        this.rowIndexes =rowIndexes;
        this.realIndexes=new ArrayList<>(rowIndexes);
        for (Integer row : rowIndexes) {
            data.add(originalDataFrame.getRow(row));
        }
    }

    public boolean containRow(int row){
        return rowIndexes.contains(row);
    }

    public void addRow(int row){
        if(!rowIndexes.contains(row)){
            rowIndexes.add(row);
            data.add(originalDataFrame.getRow(row));
            realIndexes.add(row);
        }
    }

    public void addRows(Collection<Integer> rows){
        for (Integer row : rows) {
            addRow(row);
        }
    }


    public int getRealIndex(int row){
        return realIndexes.get(row);
    }
}
