package leveretconey.chino.util;

public class Logger {
    private String outPutPath;
    private boolean outputToConsole;
    private StringBuilder sb=new StringBuilder();

    public Logger(String outPutPath, boolean outputToConsole) {
        this.outPutPath = outPutPath;
        this.outputToConsole = outputToConsole;
    }

    public void log(String prefix,String content){
        String line=prefix+":"+content;
        if(outputToConsole)
            Util.out(line);
        if(sb.length()!=0)
            sb.append("\n");
        sb.append(line);
    }

    public void save(){
        if(outPutPath!=null && !"".equals(outPutPath)){
            Util.toFile(sb.toString(),outPutPath);
        }
    }
}
