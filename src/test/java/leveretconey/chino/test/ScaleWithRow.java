package leveretconey.chino.test;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.discoverer.Chino;
import leveretconey.chino.discoverer.ODDiscoverer;
import leveretconey.chino.sampler.RandomSampler;
import leveretconey.chino.sampler.SampleConfig;
import leveretconey.chino.sampler.Sampler;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;

class ScaleWithRow {
    static double[] rates ={1,.8,.6,.4,.2,.1,.01};
    public static void main(String[] args) {
        ODDiscoverer discoverer=new Chino();
        Sampler sampler=new RandomSampler();
        DataFrame data=DataFrame.fromCsv("datasets/DBTESMA 250000 30.csv");
        for (double rate : rates) {
            DataFrame subData=sampler.sample(data,new SampleConfig(rate));
            Util.out(subData.getRowCount()+" rows");
            Timer timer=new Timer();
            Util.out(discoverer.discover(subData,null).getAllOdsOrderByBFS().size());
            Util.out((100*rate)+"% "+timer.getTimeUsedAndReset());
            Util.out("--------------------------------");
        }
    }
}
