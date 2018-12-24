package PageReplacementAlgorithms;

import java.util.ArrayList;


public class LFUPage {
    int value;
    int freq;
    int time;
    int memIndex;
    
    static ArrayList<LFUPage> pages = new ArrayList<>();
    
    private LFUPage(int v){
        value = v;
        freq = 0;
        time = -1;
        memIndex = -1;
    }
    
    public static void intialAdd(int v){
        for(LFUPage p: pages){
            if(v==p.value)
                return;
        }
        pages.add(new LFUPage(v));
    }
    
    public static void setTimeFreq(int v, int t){
        LFUPage p = getPage(v);
        p.time = t;
        p.freq++;
    }
    
    private static LFUPage getPage(int v){
        LFUPage p = null;
        for(LFUPage l: pages){
            if(l.value == v)
                return l;
        }
        return p;
    }
    
    public static int getReplacementIndex(int[] arr){
        for(int i=0; i<arr.length; i++){
            if(arr[i] == -1)
                return i;
        }
        
        ArrayList<LFUPage> mem = new ArrayList<>();
        for(int i=0; i<arr.length; i++){
            LFUPage p =(getPage(arr[i]));
            p.memIndex = i;
            mem.add(p);
        }
        ArrayList<LFUPage> lf = getLowestFrequecies(mem);
        if(lf.size() == 1){
            lf.get(0).freq = 0;
            return lf.get(0).memIndex;
        }
        else {
            int minTime = 0;
            for(int i=0; i<lf.size(); i++){
                if(lf.get(i).time < lf.get(minTime).time)
                    minTime = i;
            }
            lf.get(minTime).freq = 0;
            return lf.get(minTime).memIndex;
        }
    }
    
    private static ArrayList<LFUPage> getLowestFrequecies(ArrayList<LFUPage> ps) {
        ArrayList<LFUPage> lf = new ArrayList<>();
        
        int minFreq = 0;
        for(int i=0; i<ps.size(); i++){
            if(ps.get(minFreq).freq > ps.get(i).freq)
                minFreq = i;
        }
        
        for(int i=0; i<ps.size(); i++){
            if(ps.get(i).freq == ps.get(minFreq).freq)
                lf.add(ps.get(i));
        }
        
        return lf;
    }
}
