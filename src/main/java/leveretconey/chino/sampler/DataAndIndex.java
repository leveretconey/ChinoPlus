package leveretconey.chino.sampler;

public class DataAndIndex implements Comparable<DataAndIndex>{
    public int data;
    public int index;

    public DataAndIndex(int data, int index) {
        this.data = data;
        this.index = index;
    }

    @Override
    public int compareTo(DataAndIndex o) {
        return data-o.data;
    }
}
