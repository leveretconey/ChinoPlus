package leveretconey.chino.discoverer;

import leveretconey.chino.dataStructures.ODTree;
import leveretconey.chino.dataStructures.ODTreeNodeEquivalenceClasses;

public class ODDiscovererNodeSavingInfo {
    public ODTree.ODTreeNode nodeInResultTree;
    public ODTree.ODTreeNode referenceNode;
    public ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses;

    public ODDiscovererNodeSavingInfo(ODTree.ODTreeNode nodeInResultTree,
                                      ODTree.ODTreeNode correspondingNodeInReferenceTree,
                                      ODTreeNodeEquivalenceClasses odTreeNodeEquivalenceClasses) {
        this.nodeInResultTree = nodeInResultTree;
        this.referenceNode = correspondingNodeInReferenceTree;
        this.odTreeNodeEquivalenceClasses = odTreeNodeEquivalenceClasses;
    }
}
