package leveretconey.chino.dataStructures;

import java.util.Collection;
import java.util.Set;

public class PartialDataFrame extends DataFrame {
    private Set<Integer> rowIndexes;
    private DataFrame originalDataFrame;

    public PartialDataFrame(DataFrame dataFrame, Set<Integer> rowIndexes) {
        this.originalDataFrame=dataFrame;
        this.rowIndexes =rowIndexes;
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
        }
    }

    public void addRows(Collection<Integer> rows){
        for (Integer row : rows) {
            addRow(row);
        }
    }


    @Override
    public void deleteRow(int row) {
        if(rowIndexes.contains(row)){
            rowIndexes.remove(row);
            data.remove(row);
        }
    }
}
