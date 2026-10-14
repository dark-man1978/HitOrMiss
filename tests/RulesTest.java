import de.steph.hitormiss.GameRules;
import java.util.*;
public class RulesTest {
    static void check(boolean b,String m){if(!b)throw new AssertionError(m);}
    public static void main(String[] args) {
        for(int d=0;d<3;d++) {
            for(int count=0;count<3;count++)check(GameRules.duration(d,count)==GameRules.START_MS[d],"initial three");
            check(GameRules.duration(d,3)==Math.round(GameRules.START_MS[d]*.9),"speed increase");
            check(GameRules.duration(d,6)==Math.round(GameRules.START_MS[d]*.81),"second increase");
            long prior=Long.MAX_VALUE;
            for(int i=0;i<1000;i++){long t=GameRules.duration(d,i);check(t>=300 && t<=prior,"monotonic floor");prior=t;}
            check(GameRules.duration(d,1000)==300,"floor reached");
        }
        check(GameRules.correct(true,true),"armed tap");check(GameRules.correct(false,false),"harmless wait");
        check(!GameRules.correct(true,false),"armed missed");check(!GameRules.correct(false,true),"harmless tapped");
        check(Arrays.equals(GameRules.topThree(new int[]{20,10,5},15),new int[]{20,15,10}),"rank middle");
        check(Arrays.equals(GameRules.topThree(new int[]{20,10,5},30),new int[]{30,20,10}),"new best");
        check(Arrays.equals(GameRules.topThree(new int[]{20,10,5},0),new int[]{20,10,5}),"low excluded");
        check(Arrays.equals(GameRules.topThree(new int[]{20,10,5},10),new int[]{20,10,10}),"ties retained");
        check(Arrays.equals(GameRules.topThree(new int[0],0),new int[]{0}),"first zero");
        GameRules.Deck deck=new GameRules.Deck(new Random(33));int previous=-1;
        for(int cycle=0;cycle<100;cycle++){Set<Integer> seen=new HashSet<>();int armed=0;
            for(int i=0;i<40;i++){int c=deck.next();check(c!=previous,"no consecutive repeat");check(c>=0 && c<40,"range");seen.add(c);if(c>=20)armed++;previous=c;}
            check(seen.size()==40 && armed==20,"balanced complete cycle");}
        System.out.println("PASS: time progression, 300 ms floor, all decisions, rankings, ties, 100 balanced deck cycles.");
    }
}
