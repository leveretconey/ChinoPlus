package leveretconey.chino.discoverer;

import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.PartialDataFrame;
import leveretconey.chino.sampler.RandomSampler;
import leveretconey.chino.sampler.SampleBasedReverseNumberBasedSampler;
import leveretconey.chino.sampler.SampleConfig;
import leveretconey.chino.sampler.Sampler;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;
import leveretconey.chino.validator.ODValidator;
import leveretconey.chino.validator.PrefixBasedValidator;

public class ChinoForDebug extends ODDiscoverer{

    private Sampler sampler;
    private ODValidator validator;


    private ChinoForDebug(Sampler sampler,ODValidator validator) {
        this.sampler = sampler;
        this.validator = validator;
    }

    public ChinoForDebug() {
        this(new SampleBasedReverseNumberBasedSampler(),new PrefixBasedValidator());
    }

    @Override
    public ODTree discover(DataFrame data, ODTree reference) {
        Util.out("数据集大小"+data.getRowCount()+"行"+data.getColumnCount()+"列");
        Timer timer=new Timer();
        ODTree odTree=new ODTree(data.getColumnCount());
        Timer sampleTimer=new Timer();
        int sampleRowCount=Math.max(5,Math.min(data.getRowCount()/100,50));
        Util.out("抽样的行数"+sampleRowCount);
        PartialDataFrame sampledData=sampler.sample(data,new SampleConfig(sampleRowCount));
        Util.out("抽样用时"+sampleTimer.getTimeUsedAndReset()/1000.0+"s");
        int round=0;

        while (true){
            round++;
            Util.out("------");
            Util.out("第"+round+"轮开始");
            Timer roundTimer=new Timer();
            int subRound=0;
            BFSODDiscovererForIteration discoverer=new BFSODDiscovererForIteration();
            while (true){
                subRound++;

                Util.out("第"+subRound+"次迭代");
                Timer subtimer=new Timer();
                odTree=discoverer.discover(sampledData,odTree);
                Util.out("发现用时"+subtimer.getTimeUsedAndReset()/1000.0+"s");
                Util.out("发现"+odTree.getAllOdsOrderByBFS().size()+" OD");
                Set<Integer> violateRowIndexes=validator.validate(odTree,data);
                Util.out("检测用时"+subtimer.getTimeUsedAndReset()/1000.0+"s");
                Util.out("剩余"+odTree.getAllOdsOrderByBFS().size()+" OD");
                if(violateRowIndexes.size()==0){
                    if(discoverer.isComplete()){
                        Util.out("------");
                        Util.out("第"+round+"轮结束");
                        Util.out("本轮用时"+roundTimer.getTimeUsed()/1000+"s");
                        Util.out("新数据集大小"+sampledData.getRowCount());
                        Util.out("------");
                        Util.out("------");
                        Util.out(odTree);

                        Util.out("最终统计");
                        Util.out("用时"+timer.getTimeUsed()/1000.0+"s");
                        Util.out("OD数量"+odTree.getAllOdsOrderByBFS().size());
                        Util.out("数据集大小"+sampledData.getRowCount());
                        Util.out("check时间 "+ ODTreeNodeEquivalenceClasses.validateTime);
                        Util.out("minimal检查时间 "+ ODDiscoverer.minimalCheckTime);
                        Util.out("product时间 "+ODTreeNodeEquivalenceClasses.mergeTime);
                        return odTree;
                    }
                }else {
                    sampledData.addRows(violateRowIndexes);
                    Util.out("------");
                    Util.out("第"+round+"轮结束");
                    Util.out("本轮用时"+roundTimer.getTimeUsed()/1000.0+"s");
                    Util.out("新数据集大小"+sampledData.getRowCount());
                    Util.out("------");
                    break;
                }
            }
        }


    }
}
