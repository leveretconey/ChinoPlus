package leveretconey.chino;

import java.util.List;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.discoverer.ChinoPlus;
import leveretconey.chino.util.Util;

class Main{
    public static void main(String[] args) {


        DataFrame dataFrame=DataFrame.fromCsv("integer datasets/DBTESMA 250000 30.csv");
        ODTree discover = new ChinoPlus().discover(dataFrame);
        List<ODCandidate> ods = discover.getAllOdsOrderByBFS();
        for (ODCandidate od : ods) {
            Util.out(od);
        }

//        int experimentCount=5;
//        if(args!=null && args.length==1){
//            try {
//                experimentCount=Integer.parseInt(args[0]);
//            } catch (NumberFormatException e) {}
//        }
//
//
//        Util.out("chino 双向原版");
//        Timer timer=new Timer();
//        DataFrame data=DataFrame.fromCsv("integer datasets/DBTESMA 250000 30.csv");
//        Util.out("读取数据时间："+timer.getTimeUsed()/1000.0+"s");
//        for(int i=1;i<=experimentCount;i++) {
//            timer.reset();
//            Util.out("\n第"+i+"次实验");
//            ChinoPlus chino = new ChinoPlus(
//                    new OneLevelCheckingSampler(),
//                    new ODPrefixBasedIncrementalValidator(),
////                    new ODPrefixBasedIncrementalValidatorNoPath(),
////                    new ODPrefixBasdIncrementalValidatorAllViolation(),
//                    true
//            );
//            chino.setSamplerRandomSeed(i*i);
//            ODTree tree=chino.discover(data);
//        }
    }
}
