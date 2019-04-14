package leveretconey.chino.test;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.discoverer.BFSODDiscovererFull;
import leveretconey.chino.discoverer.ChinoPlus;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;

public class TestSimpleDiscoverer {
    public static final int COUNT_ROW=100000;
    public static final int COUNT_COLUMN=30;

    public static void main(String[] args) {
        DataFrame data=DataFrame.fromCsv("datasets/no 5 2.csv");
//        data=data.subDataFrame(10000,999);
        Timer timer=new Timer();
        ODTree tree=new ChinoPlus().discover(data,null);
        Util.out(timer.getTimeUsedAndReset());

        tree=new BFSODDiscovererFull().discover(data,null);
        Util.out(timer.getTimeUsedAndReset());
    }
}
