package de.steph.hitormiss;
import java.util.*;
/** Pure rules: score counts every correct decision, whether tap or timeout. */
public final class GameRules {
    public static final String[] NAMES = {"Leicht", "Mittel", "Schwer"};
    public static final long[] START_MS = {5000, 3000, 1000};
    public static long duration(int difficulty, int completed) {
        if (difficulty < 0 || difficulty > 2 || completed < 0) throw new IllegalArgumentException();
        return Math.max(300, Math.round(START_MS[difficulty] * Math.pow(0.9, completed / 3)));
    }
    public static boolean correct(boolean armed, boolean tapped) { return armed == tapped; }
    public static int[] topThree(int[] old, int score) {
        if (score < 0) throw new IllegalArgumentException();
        int[] all = Arrays.copyOf(old, old.length + 1); all[old.length] = score;
        Arrays.sort(all); int[] best = new int[Math.min(3, all.length)];
        for (int i = 0; i < best.length; i++) best[i] = all[all.length - 1 - i];
        return best;
    }
    public static final class Deck {
        private final Random random; private final List<Integer> cards = new ArrayList<>(); private int last = -1;
        public Deck(Random random) { this.random = random; }
        public int next() {
            if (cards.isEmpty()) {
                for (int i = 0; i < 40; i++) cards.add(i);
                Collections.shuffle(cards, random);
                if (cards.get(cards.size()-1) == last) Collections.swap(cards, 0, cards.size()-1);
            }
            last = cards.remove(cards.size()-1); return last;
        }
    }
    private GameRules() {}
}
