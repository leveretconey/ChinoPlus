package leveretconey.chino.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import leveretconey.chino.util.Util;

class GCTime {
    public static void main(String[] args) {
        String s= Util.fromFile("backup\\gc.txt");
        String[] lines=s.split("\\n");
        Pattern pattern=Pattern.compile(" (\\d+\\.\\d+) secs");
        double result=0;
        for (String line : lines) {
            Matcher matcher=pattern.matcher(line);
            matcher.find();
            result+=Double.parseDouble(matcher.group(1));
        }
        Util.out((int)(result*1000));
    }
}
