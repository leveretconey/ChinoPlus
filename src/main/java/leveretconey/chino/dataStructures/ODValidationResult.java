package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.List;

public class ODValidationResult {
    public ODTree.ODTreeNodeStatus status= ODTree.ODTreeNodeStatus.UNKNOWN;
    public List<Integer> violationRows;

    public ODValidationResult(ODTree.ODTreeNodeStatus status) {
        this();
        this.status = status;
    }

    public ODValidationResult() {
        violationRows=new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ODValidationResult{" +
                "status=" + status +
                ", violationRows=" + violationRows +
                '}';
    }
}
