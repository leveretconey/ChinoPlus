package leveretconey.chino.test;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.discoverer.BFSODDiscovererForIteration;
import leveretconey.chino.util.Util;

class drawPicForPaper {
    public static void main(String[] args) {
        DataFrame data=DataFrame.fromCsv("backup/flights_20_500k.csv");
        data=data.subDataFrame(3,4);
        ODTree tree=new BFSODDiscovererForIteration().discover(data,null);
        Util.out(tree.getAllOdsOrderByDFS().size());
        tree.toPic("output\\paperPic","jpg",true);
    }
}
