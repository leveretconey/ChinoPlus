package leveretconey.chino.dataStructures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import leveretconey.chino.sampler.RandomSampler;
import leveretconey.chino.sampler.SampleConfig;
import leveretconey.chino.util.Util;


public class DataFrame {
    protected List<List<Integer>> data=new ArrayList<List<Integer>>();
    private List<String> columnName=new ArrayList<>();

    public  int getColumnCount(){
        return columnName.size();
    }
    public  int getRowCount(){
        return data.size();
    }
    public int get(int row,int column){
        return data.get(row).get(column);
    }
    public List<Integer> getRow(int row){
        return data.get(row);
    }
    public void deleteRow(int row){
        data.remove(row);
    }
    public void addRow(List<Integer> row){
        data.add(row);
    }

    public List<String> getColumnName() {
        return columnName;
    }

    public void setColumnName(List<String> columnName) {
        this.columnName = columnName;
    }

    public static DataFrame fromCsv(String filePath){
        return fromCsv(filePath,"utf-8",true);
    }
    public static DataFrame fromCsv(String filePath, String encoding, boolean hasHead){
        try {
            File inputFile=new File(filePath);
            if(!inputFile.exists())
                return null;
            FileInputStream fis=new FileInputStream(inputFile);
            InputStreamReader isr=new InputStreamReader(fis, encoding);
            BufferedReader reader = new BufferedReader(isr);

            DataFrame result=new DataFrame();
            if(hasHead ){
                String line=reader.readLine();
                if(line==null)
                    return null;
                String[] parts=line.split(",");
                result.columnName=Arrays.asList(parts);
            }
            String line;
            while ( (line=reader.readLine()   )!=null ){
                List<Integer> list=new ArrayList<>();
                String[] parts=line.split(",");
                for (String part : parts) {
                    list.add(Integer.parseInt(part));
                }
                result.data.add(list);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public void toCsv(String filePath){
        toCsv(filePath,"utf-8");
    }
    public void toCsv(String filePath,String encoding){
        BufferedWriter writer=null;
        try {
            writer=new BufferedWriter(new OutputStreamWriter
                    (new FileOutputStream(filePath),encoding));
            for (int i = 0; i < columnName.size(); i++) {
                if(i!=0)
                    writer.write(',');
                writer.write(columnName.get(i));
            }
            for (List<Integer> row : data) {
                writer.write('\n');
                for (int i = 0; i < row.size(); i++) {
                    if(i!=0)
                        writer.write(',');
                    writer.write(String.valueOf(row.get(i)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DataFrame chooseColumns(Set<Integer> columnIndexes){
        boolean[] choose=new boolean[getColumnCount()];
        int originalColumnCount=getColumnCount();
        for (Integer columnIndex : columnIndexes) {
            if(columnIndex>=1 && columnIndex<=originalColumnCount)
                choose[columnIndex-1]=true;
        }

        List<String> newColumnName=new ArrayList<>();
        for (int i = 0; i < originalColumnCount; i++) {
            if(choose[i]){
                newColumnName.add(columnName.get(i));
            }
        }
        columnName=newColumnName;

        List<List<Integer>> newData=new ArrayList<>();
        for (List<Integer> originalRow : data) {
            List<Integer> newRow=new ArrayList<>();
            for (int i = 0; i < originalColumnCount; i++) {
                if (choose[i]){
                    newRow.add(originalRow.get(i));
                }
            }
            newData.add(newRow);
        }
        data=newData;
        return this;
    }

    public DataFrame randomSelectColumns(int columnCount){
        if(columnCount>getColumnCount() || columnCount<0){
            return this;
        }
        List<Integer> columns=new ArrayList<>();
        for (int column = 1; column <= getColumnCount(); column++) {
            columns.add(column);
        }
        Collections.shuffle(columns);
        Set<Integer> columnToChoose=new HashSet<>();
        for (int i = 0; i < columnCount; i++) {
            columnToChoose.add(columns.get(i));
        }
        chooseColumns(columnToChoose);
        return this;
    }

    public DataFrame subDataFrame(int countRow, int countColumn){
        return new RandomSampler().sample(this,new SampleConfig(countRow))
                .randomSelectColumns(countColumn);
    }
}
