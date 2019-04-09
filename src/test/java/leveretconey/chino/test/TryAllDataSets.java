package leveretconey.chino.test;

import java.io.File;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.discoverer.Chino;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;

class TryAllDataSets {
    public static void main(String[] args) {
        File directory=new File("datasets");
        for(File file:directory.listFiles()){
            DataFrame dataFrame=DataFrame.fromCsv(file.getAbsolutePath());
            Util.out(file.getName()+" "+dataFrame.getRowCount()+" rows "+dataFrame.getColumnCount()+" columns begins");
            Timer timer=new Timer();
            ODTree tree=new Chino().discover(dataFrame,null);
            Util.out(tree.getAllOdsOrderByDFS().size()+" ods");
            Util.out(tree);
            Util.out(timer.getTimeUsed());
            Util.out("------------------------------");
        }
    }
}
