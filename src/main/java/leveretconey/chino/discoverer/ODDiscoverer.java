package leveretconey.chino.discoverer;

import leveretconey.chino.dataStructures.AttributeAndDirection;
import leveretconey.chino.dataStructures.DataFrame;
import leveretconey.chino.dataStructures.ODByLeftRightAttributeList;
import leveretconey.chino.dataStructures.ODCandidate;
import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTree.ODTreeNode;
import leveretconey.chino.util.Timer;
import leveretconey.chino.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ODDiscoverer {
    public abstract ODTree discover(DataFrame data, ODTree reference);
    public final ODTree discover(DataFrame data){
        return discover(data,null);
    }
}
