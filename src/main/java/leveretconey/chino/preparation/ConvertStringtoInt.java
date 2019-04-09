package leveretconey.chino.preparation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.util.Util;

class ConvertStringtoInt {

    public static final String INPUT="datasets/ncvoter 1000 19";
    public static int[] columnsToParse={3,4,5,6,7,9,10,11,12,13,14,17};
    public static void main(String[] args) {
        String csv= Util.fromFile(INPUT+".csv");
        String[] lines=csv.split("\\n");
        int countColumn;
        DataFrame result=new DataFrame();
        List<List<String>> data=new ArrayList<>();

        //read
        for (int i = 0; i < lines.length; i++) {
            String line=lines[i];
            String[] parts=line.split(",");
            if(i==0){
                countColumn=parts.length;
                List<String> head=new ArrayList<>();
                for (int index = 0; index < countColumn; index++) {
                    head.add((index+1)+"");
                }
                result.setColumnName(head);
            }
            else {
                List<String> row=new ArrayList<>(Arrays.asList(parts));
                data.add(row);
            }
        }
        //parse
        for (int column : columnsToParse) {
            parseColumnFromStringToInteger(data,column);
        }
        lines=null;
        //output
        for (List<String> row : data) {
            List<Integer> resultRow=new ArrayList<>();
            for (String s : row) {
                resultRow.add(Integer.parseInt(s));
            }
            result.addRow(resultRow);
        }
        result.toCsv(INPUT+" done.csv");

    }

    public static void parseColumnFromStringToInteger(List<List<String>> data,int column){
        Util.out("begin to dispose "+column);
        column--;
        List<ValueAndIndex> list=new ArrayList<>();
        int rowcount=data.size();
        for (int row = 0; row < rowcount; row++) {
            list.add(new ValueAndIndex(data.get(row).get(column),row));
        }
        Collections.sort(list);
        int lastRank=1;
        for (int i = 0; i < rowcount; i++) {
            int rank;
            if(i==0)
                rank=1;
            else {
                if(list.get(i).value.equals(list.get(i-1).value))
                    rank=lastRank;
                else
                    rank=lastRank+1;
            }
            data.get(list.get(i).index).set(column,rank+"");
            lastRank=rank;
        }
    }

    private static class ValueAndIndex implements Comparable<ValueAndIndex>{

        String value;
        int index;

        public ValueAndIndex(String values, int index) {
            this.value = values;
            this.index = index;
        }

        @Override
        public int compareTo(ValueAndIndex o) {
            return value.compareTo(o.value);
        }
    }
}
