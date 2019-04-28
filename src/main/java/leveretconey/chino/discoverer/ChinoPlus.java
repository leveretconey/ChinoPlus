package leveretconey.chino.discoverer;

import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.minimal.ODMinimalCheckTree;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;
import leveretconey.chino.dataStructures.PartialDataFrame;
import leveretconey.chino.minimal.ODMinimalChecker;
import leveretconey.chino.sampler.OneLevelCheckingSampler;
import leveretconey.chino.sampler.Sampler;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;
import leveretconey.chino.validator.ODValidator;
import leveretconey.chino.validator.ODPrefixBasedIncrementalValidator;

public class ChinoPlus extends ODDiscoverer{

    private Sampler sampler;
    private ODValidator validator;
    private boolean printDebugInfo;

    public ChinoPlus() {
        this(false);
    }
    public ChinoPlus(boolean printDebugInfo) {
        this(new OneLevelCheckingSampler(),new ODPrefixBasedIncrementalValidator(),printDebugInfo);
    }

    public ChinoPlus(Sampler sampler, ODValidator validator) {
        this(sampler,validator,false);
    }
    public ChinoPlus(Sampler sampler, ODValidator validator, boolean printDebugInfo) {
        this.sampler = sampler;
        this.validator = validator;
        this.printDebugInfo = printDebugInfo;
    }

    private void out(Object o){
        if(printDebugInfo){
            Util.out(o);
        }
    }

    @Override
    public ODTree discover(DataFrame data, ODTree reference) {
            out("数据集大小"+data.getRowCount()+"行"+data.getColumnCount()+"列");
            Timer timer=new Timer();
        ODTree odTree=new ODTree(data.getColumnCount());
            Timer sampleTimer=new Timer();

        PartialDataFrame sampledData=sampler.sample(data);
            out("抽样用时"+sampleTimer.getTimeUsedAndReset()/1000.0+"s");
            out("抽样数量"+sampledData.getRowCount());
            int round=0;

        while (true){
                round++;
                out("------");
                out("第"+round+"轮开始");
                Timer roundTimer=new Timer();
                int subRound=0;
            BFSODDiscovererForIteration discoverer=new BFSODDiscovererForIteration();
            while (true){
                    subRound++;
                    out("\n第"+subRound+"次迭代");
                    Timer subtimer=new Timer();
                odTree=discoverer.discover(sampledData,odTree);
                    out("发现用时"+subtimer.getTimeUsedAndReset()/1000.0+"s");
                    out("OD数量"+odTree.getAllOdsOrderByBFS().size());
                Set<Integer> violateRowIndexes=validator.validate(odTree,data);
                    out("检测用时"+subtimer.getTimeUsedAndReset()/1000.0+"s");
                    out("剩余OD数量"+odTree.getAllOdsOrderByBFS().size());
                    showPartStatisticAndReset();
                if(violateRowIndexes.size()==0){
                    if(discoverer.isComplete()){
                            out("------");
                            out("第"+round+"轮结束");
                            out("本轮用时"+roundTimer.getTimeUsed()/1000+"s");
                            out("新数据集大小"+sampledData.getRowCount());
                            out("------");
                            out("------");
//                            out(odTree);
                            out("最终统计");
                            out("用时"+timer.getTimeUsed()/1000.0+"s");
                            out("OD数量"+odTree.getAllOdsOrderByBFS().size());
                            out("数据集大小"+sampledData.getRowCount());
                            out("------");
                        return odTree;
                    }
                }else {
                    sampledData.addRows(violateRowIndexes);
                        out("------");
                        out("第"+round+"轮结束");
                        out("本轮用时"+roundTimer.getTimeUsed()/1000.0+"s");
                        out("新数据集大小"+sampledData.getRowCount());
                        out("------");
                    break;
                }
            }
        }
    }
    void showPartStatisticAndReset(){
        out("check时间 "+ ODTreeNodeEquivalenceClasses.validateTime/1000.0+"s");
        out("minimal检查时间 "+ ODMinimalCheckTree.minimalCheckTime/1000.0+"s");
        out("product时间 "+ODTreeNodeEquivalenceClasses.mergeTime/1000.0+"s");
        out("clone时间 "+ODTreeNodeEquivalenceClasses.cloneTime/1000.0+"s");
        ODTreeNodeEquivalenceClasses.validateTime=0;
        ODTreeNodeEquivalenceClasses.cloneTime=0;
        ODTreeNodeEquivalenceClasses.mergeTime=0;
        ODMinimalChecker.minimalCheckTime=0;
    }
}
