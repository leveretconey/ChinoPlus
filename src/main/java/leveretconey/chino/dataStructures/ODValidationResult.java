package leveretconey.chino.dataStructures;

import java.util.ArrayList;
import java.util.List;

public class ODValidationResult {
    public ODTree.ODTreeNodeStatus status= ODTree.ODTreeNodeStatus.UNKNOWN;
    public List<Integer> violationRows;

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
