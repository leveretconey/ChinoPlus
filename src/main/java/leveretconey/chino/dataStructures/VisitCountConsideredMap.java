package leveretconey.chino.dataStructures;

import java.util.HashMap;
import java.util.Map;

public class VisitCountConsideredMap<K,V> {
    private Map<K,Integer> remainingVisitCount=new HashMap<>();
    private Map<K,V> map=new HashMap<>();

    public VisitCountConsideredMap() {
    }

    public void addVisitCount(K key,int addCount){
        if(addCount!=0) {
            int count = remainingVisitCount.getOrDefault(key, 0)+addCount;
            if(count>0) {
                remainingVisitCount.put(key, count);
            }else {
                map.remove(key);
                remainingVisitCount.remove(key);
            }
        }
    }
    public boolean mayPut(K key){
        return remainingVisitCount.containsKey(key);
    }
    public boolean containKey(K key){
        return map.containsKey(key);
    }

    public void addVisitCount(K key){
        addVisitCount(key,1);
    }

    public void put(K key,V value){
        int count=remainingVisitCount.getOrDefault(key,0);
        if(count>0){
            map.put(key,value);
        }
    }

    public void put(K key,V value,int addVisitCount){
        addVisitCount(key,addVisitCount);
        put(key,value);
    }

    public V get(K key){
        if(!map.containsKey(key)){
            throw new RuntimeException("key not available");
        }
        int count=remainingVisitCount.getOrDefault(key,0);
        V result=map.get(key);
        if(count==1){
            map.remove(key);
            remainingVisitCount.remove(key);
        }else {
            remainingVisitCount.put(key,count-1);
        }
        return result;
    }

}
