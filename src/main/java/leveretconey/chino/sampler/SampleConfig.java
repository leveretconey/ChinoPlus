package leveretconey.chino.sampler;


public class SampleConfig {
    public int sampleLineCount;
    public double samplePercentage;
    private boolean usePercentage;

    public boolean isUsePercentage() {
        return usePercentage;
    }

    public SampleConfig(int lineInResult) {
        this.sampleLineCount = lineInResult;
        usePercentage=false;
    }

    public SampleConfig(double percentage) {
        this.samplePercentage = percentage;
        usePercentage=true;
    }
}
