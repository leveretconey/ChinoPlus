package leveretconey.chino.test;

import java.util.HashSet;

import leveretconey.chino.util.Util;

@SuppressWarnings("all")
public class CompareFile {
    public static void main(String[] args) {
        String moreText= Util.fromFile("C:\\Users\\leveretconey\\Desktop\\约束发现\\tree.txt");
        String lessText= Util.fromFile("C:\\Users\\leveretconey\\Desktop\\约束发现\\bf.txt");
        HashSet<String> set=new HashSet<>();
        for (String s : lessText.split("\\n")) {
            set.add(s);
        }
        for(String s:moreText.split("\\n")){
            if(!set.contains(s)){
                Util.out(s);
            }
        }
    }
}
