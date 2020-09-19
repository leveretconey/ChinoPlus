package leveretconey.chino.discoverer;

import java.util.Set;

import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODCandidate;
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

    protected Sampler sampler;
    protected ODValidator validator;
    protected boolean printDebugInfo;

    private long totalDiscoverTime=0;
    private long totalValidateTime=0;
    private long totalProductTime=0;
    private long totalCloneTime=0;
    private long totalCheckTime=0;
    private long totalMinimalTime=0;


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

    protected void out(Object o){
        if(printDebugInfo){
            Util.out(o);
        }
    }

    public void setSamplerRandomSeed(long seed){
        sampler.setRandomSeed(seed);
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
//            odTree=new ODTree(data.getColumnCount());
            BFSODDiscovererForIteration discoverer=new BFSODDiscovererForIteration();
            while (true){
                    subRound++;
                    out("\n第"+subRound+"次迭代");
                    Timer subtimer=new Timer();
                odTree=discoverer.discover(sampledData,odTree);
                    totalDiscoverTime+=subtimer.getTimeUsed();
                    out("发现用时"+subtimer.getTimeUsedAndReset()/1000.0+"s");
                    out("OD数量"+odTree.getAllOdsOrderByBFS().size());
                Set<Integer> violateRowIndexes=validator.validate(odTree,data);
                    totalValidateTime+=subtimer.getTimeUsed();
                    out("检测用时"+subtimer.getTimeUsedAndReset()/1000.0+"s");
                    out("剩余OD数量"+odTree.getAllOdsOrderByBFS().size());
                    dealPartTime();
                if(violateRowIndexes.size()==0){
                    if(discoverer.isComplete()){
                            out("------");
                            out("第"+round+"轮结束");
                            out("本轮用时"+roundTimer.getTimeUsed()/1000+"s");
                            out("新数据集大小"+sampledData.getRowCount());
                            out("------");
                            out("-----------------------------------------------------");
//                            out(odTree);
                            out("最终统计");
                            out("用时"+timer.getTimeUsed()/1000.0+"s");
                            out("OD数量"+odTree.getAllOdsOrderByBFS().size());
                            out("数据集大小"+sampledData.getRowCount());
                            out("discover时间:"+totalDiscoverTime/1000.0+"s");
                            out("validate时间:"+totalValidateTime/1000.0+"s");
                            Util.out("");
                            out("check时间:"+totalCheckTime/1000.0+"s");
                            out("minimal检查时间:"+totalMinimalTime/1000.0+"s");
                            out("product时间:"+totalProductTime/1000.0+"s");
                            out("clone时间:"+totalCloneTime/1000.0+"s");
                            out("-----------------------------------------------------");
                        return odTree;
                    }else {
                        //todo debug
                        if (odTree.getAllOdsOrderByDFS().size()>100000){
                            for (ODCandidate od : odTree.getAllOdsOrderByBFS()) {
                                Util.out(od);
                            }
                            return odTree;
                        }
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
    void dealPartTime(){
        out("check时间 "+ ODTreeNodeEquivalenceClasses.validateTime/1000.0+"s");
        out("minimal检查时间 "+ ODMinimalCheckTree.minimalCheckTime/1000.0+"s");
        out("product时间 "+ODTreeNodeEquivalenceClasses.mergeTime/1000.0+"s");
        out("clone时间 "+ODTreeNodeEquivalenceClasses.cloneTime/1000.0+"s");
        totalCheckTime+=ODTreeNodeEquivalenceClasses.validateTime;
        totalMinimalTime+=ODMinimalCheckTree.minimalCheckTime;
        totalProductTime+=ODTreeNodeEquivalenceClasses.mergeTime;
        totalCloneTime+=ODTreeNodeEquivalenceClasses.cloneTime;
        ODTreeNodeEquivalenceClasses.validateTime=0;
        ODTreeNodeEquivalenceClasses.cloneTime=0;
        ODTreeNodeEquivalenceClasses.mergeTime=0;
        ODMinimalChecker.minimalCheckTime=0;
    }
}
