package de.steph.hitormiss;

import android.app.Activity;
import android.os.*;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public final class MainActivity extends Activity {
    private static final int BG = Color.rgb(17,32,39), PANEL = Color.rgb(29,48,57), INK = Color.rgb(245,239,225), MUTED = Color.rgb(171,191,195), GOLD = Color.rgb(245,184,92);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SharedPreferences prefs;
    private LinearLayout root;
    private TextView scoreLabel, timeLabel, feedback;
    private ProgressBar timer;
    private ReactionImageView picture;
    private int difficulty = 0, score = 0, card = 0;
    private long deadline, duration;
    private boolean playing, accepting, destroyed, loading, atMenu;
    private int loadGeneration;
    private GameRules.Deck deck;
    private final Map<Integer, Bitmap> images = new HashMap<>();
    private final Runnable tick = new Runnable() {
        public void run() {
            if (!playing || !accepting) return;
            long left = deadline - SystemClock.elapsedRealtime();
            if (left <= 0) { decide(false); return; }
            timer.setProgress((int)(1000 * left / duration));
            timeLabel.setText(String.format(Locale.GERMAN, "%.1f s", left / 1000.0));
            handler.postDelayed(this, 16);
        }
    };

    private static final class ReactionImageView extends ImageView {
        ReactionImageView(android.content.Context context) { super(context); }
        @Override public boolean performClick() { return super.performClick(); }
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);
        prefs = getSharedPreferences("hitormiss_scores_v1", MODE_PRIVATE);
        if (state != null) difficulty = state.getInt("difficulty", 0);
        menu(null);
    }
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    private GradientDrawable shape(int color, float radius) { GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d; }
    private TextView text(String value, int size, int color) {
        TextView t=new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setFontFeatureSettings("tnum"); t.setPadding(0,dp(4),0,dp(4)); return t;
    }
    private void base(boolean scroll) {
        atMenu=false;
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(22),dp(16),dp(22),dp(18)); root.setBackgroundColor(BG);
        // Insets keep controls outside Android 15 edge-to-edge system bars.
        root.setOnApplyWindowInsetsListener((v,insets)-> {
            root.setPadding(dp(22)+insets.getSystemWindowInsetLeft(),dp(16)+insets.getSystemWindowInsetTop(),dp(22)+insets.getSystemWindowInsetRight(),dp(18)+insets.getSystemWindowInsetBottom());
            return insets;
        });
        if (scroll) { ScrollView s=new ScrollView(this); s.setBackgroundColor(BG); s.setFillViewport(true); s.addView(root); setContentView(s); }
        else setContentView(root);
    }
    private void gap(int height) { View v=new View(this); root.addView(v,new LinearLayout.LayoutParams(1,dp(height))); }
    private Button button(String label, boolean primary, Runnable action) {
        Button b=new Button(this); b.setText(label); b.setAllCaps(false); b.setTextSize(16); b.setTypeface(null,Typeface.BOLD);
        b.setTextColor(primary?BG:INK); b.setBackground(shape(primary?GOLD:PANEL,14)); b.setMinHeight(dp(52));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(54)); p.topMargin=dp(8); root.addView(b,p);
        b.setOnClickListener(v->action.run()); return b;
    }
    private void heading(String eyebrow, String title) {
        TextView e=text(eyebrow,12,GOLD); e.setLetterSpacing(.18f); root.addView(e);
        TextView h=text(title,38,INK); h.setTypeface(null,Typeface.BOLD); root.addView(h);
    }
    private void stop() { loadGeneration++; loading=false; playing=false; accepting=false; handler.removeCallbacksAndMessages(null); getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); }
    private void menu(String notice) {
        stop(); base(true); atMenu=true; heading("REAKTION · ENTSCHEIDUNG", "HitOrMiss");
        root.addView(text("Ein Blick. Eine Entscheidung.",19,MUTED)); gap(16);
        TextView rules=text("Bewaffnet? Bild antippen.\nHarmlos? Zeit ablaufen lassen.\n\nEin Fehler beendet die Runde. Jede richtige Entscheidung bringt einen Punkt.",16,INK);
        rules.setBackground(shape(PANEL,16)); rules.setPadding(dp(18),dp(14),dp(18),dp(14)); root.addView(rules);
        if (notice!=null) { gap(10); root.addView(text(notice,14,GOLD)); }
        gap(18); root.addView(text("SCHWIERIGKEIT",12,MUTED));
        for(int i=0;i<3;i++) {
            final int d=i; String label=GameRules.NAMES[i]+"  ·  "+(GameRules.START_MS[i]/1000)+" Sekunden";
            button((difficulty==i?"✓  ":"")+label,difficulty==i,()->{difficulty=d;menu(notice);});
        }
        gap(8); root.addView(text("Alle 3 Bilder: 10 % schneller · Minimum 0,3 s",13,MUTED));
        button("Runde starten  →",true,this::prepare);
        button("Bestenlisten",false,this::leaderboards);
        gap(10); root.addView(text("40 Comicbilder · Offline · Keine Werbung",12,MUTED));
    }
    private void prepare() {
        stop(); base(true); heading("HITORMISS", "Bereit machen"); root.addView(text("Bilder werden geladen …",17,MUTED));
        final int generation = loadGeneration; loading=true;
        new Thread(()-> {
            try {
                if(images.isEmpty()) {
                    Map<Integer,Bitmap> loaded=new HashMap<>();
                    for(int i=0;i<40;i++) {
                        String name=(i<20?"safe_":"armed_")+String.format(Locale.ROOT,"%02d",i%20+1)+".jpg";
                        try(InputStream in=getAssets().open("images/"+name)) {
                            BitmapFactory.Options o=new BitmapFactory.Options(); o.inPreferredConfig=Bitmap.Config.RGB_565;
                            Bitmap bmp=BitmapFactory.decodeStream(in,null,o); if(bmp==null) throw new IOException(name); loaded.put(i,bmp);
                        }
                    }
                    synchronized(images) { images.putAll(loaded); }
                }
                handler.post(()-> { if(!destroyed && !isFinishing() && generation == loadGeneration) start(); });
            } catch(Exception e) { handler.post(()->{ if(!destroyed) menu("Bilder konnten nicht geladen werden. Bitte App neu installieren."); }); }
        },"card-loader").start();
    }
    private void start() {
        loading=false; score=0; deck=new GameRules.Deck(new Random()); playing=true; getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        base(false);
        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand=text("HitOrMiss",24,INK); brand.setTypeface(null,Typeface.BOLD); bar.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
        TextView level=text(GameRules.NAMES[difficulty],14,GOLD); bar.addView(level); root.addView(bar);
        LinearLayout stats=new LinearLayout(this); scoreLabel=text("0 Punkte",20,INK); stats.addView(scoreLabel,new LinearLayout.LayoutParams(0,-2,1));
        timeLabel=text("",20,GOLD); stats.addView(timeLabel); root.addView(stats);
        timer=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal); timer.setMax(1000); timer.setProgressTintList(android.content.res.ColorStateList.valueOf(GOLD));
        root.addView(timer,new LinearLayout.LayoutParams(-1,dp(8))); gap(12);
        picture=new ReactionImageView(this); picture.setScaleType(ImageView.ScaleType.FIT_CENTER); picture.setBackground(shape(PANEL,16));
        root.addView(picture,new LinearLayout.LayoutParams(-1,0,1));
        picture.setContentDescription("Personenbild. Nur bei sichtbarer Bewaffnung antippen."); picture.setFocusable(true); picture.requestFocus();
        picture.setOnTouchListener((v,event)-> {
            if(event.getActionMasked()==MotionEvent.ACTION_DOWN && accepting) {
                if(SystemClock.elapsedRealtime()>=deadline) decide(false); else decide(true);
                v.performClick(); return true;
            }
            return true;
        });
        picture.setOnClickListener(v->{ if(accepting) decide(SystemClock.elapsedRealtime()<deadline); });
        picture.setOnKeyListener((v,key,event)-> {
            if((key==KeyEvent.KEYCODE_ENTER || key==KeyEvent.KEYCODE_SPACE || key==KeyEvent.KEYCODE_DPAD_CENTER) && event.getAction()==KeyEvent.ACTION_DOWN && accepting) {
                decide(SystemClock.elapsedRealtime()<deadline); return true;
            } return false;
        });
        feedback=text("Bewaffnet antippen · Harmlos abwarten",14,MUTED); feedback.setGravity(Gravity.CENTER); root.addView(feedback);
        button("Runde abbrechen",false,()->menu("Runde abgebrochen – kein Bestenlisteneintrag."));
        next();
    }
    private void next() {
        if(!playing)return;
        card=deck.next(); picture.setImageBitmap(images.get(card)); picture.setAlpha(1f); picture.setEnabled(true);
        duration=GameRules.duration(difficulty,score); scoreLabel.setText(score+" Punkte");
        feedback.setText("Bewaffnet antippen · Harmlos abwarten"); feedback.setTextColor(MUTED);
        // Start after the frame containing the next picture was actually submitted.
        accepting=false; timer.setProgress(1000); timeLabel.setText(String.format(Locale.GERMAN,"%.1f s",duration/1000.0));
        picture.postOnAnimation(()->picture.postOnAnimation(()-> {
            if(!playing)return; deadline=SystemClock.elapsedRealtime()+duration; accepting=true; tick.run();
        }));
    }
    private void decide(boolean tapped) {
        if(!playing || !accepting)return;
        accepting=false; handler.removeCallbacks(tick);
        boolean armed=card>=20;
        if(!GameRules.correct(armed,tapped)) {
            finishRound(armed?"Zu langsam: Die Person war bewaffnet.":"Falscher Tipp: Die Person war harmlos."); return;
        }
        score++; scoreLabel.setText(score+" Punkte"); feedback.setText(tapped?"Richtig erkannt  +1":"Richtig abgewartet  +1"); feedback.setTextColor(GOLD);
        picture.animate().alpha(.72f).setDuration(120).start();
        handler.postDelayed(this::next,350);
    }
    private int[] scores(int d) {
        String raw=prefs.getString("top_"+d,""); if(raw.isEmpty())return new int[0];
        try { String[] p=raw.split(","); int[] a=new int[p.length]; for(int i=0;i<a.length;i++)a[i]=Integer.parseInt(p[i]); return a; }
        catch(NumberFormatException e){return new int[0];}
    }
    private void finishRound(String reason) {
        stop(); int previousBest=scores(difficulty).length==0?-1:scores(difficulty)[0];
        int[] top=GameRules.topThree(scores(difficulty),score); StringBuilder raw=new StringBuilder();
        for(int n:top){if(raw.length()>0)raw.append(',');raw.append(n);} prefs.edit().putString("top_"+difficulty,raw.toString()).apply();
        base(true); heading("RUNDE BEENDET", "Entscheidung zählt");
        TextView number=text(String.valueOf(score),82,GOLD); number.setTypeface(null,Typeface.BOLD); root.addView(number);
        root.addView(text("Punkte · "+GameRules.NAMES[difficulty],22,INK)); root.addView(text(reason,16,MUTED));
        if(score>previousBest)root.addView(text("Neue persönliche Bestleistung!",16,GOLD));
        gap(18); table(difficulty);
        button("Noch eine Runde",true,this::prepare); button("Schwierigkeit wählen",false,()->menu(null)); button("Alle Bestenlisten",false,this::leaderboards);
    }
    private void table(int d) {
        TextView title=text("TOP 3 · "+GameRules.NAMES[d].toUpperCase(Locale.GERMAN),13,GOLD); root.addView(title);
        int[] top=scores(d); for(int i=0;i<3;i++) {
            TextView row=text((i+1)+".     "+(i<top.length?top[i]+" Punkte":"Noch kein Ergebnis"),18,INK);
            row.setBackground(shape(PANEL,10)); row.setPadding(dp(16),dp(12),dp(16),dp(12));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.topMargin=dp(6); root.addView(row,p);
        }
    }
    private void leaderboards() { stop(); base(true); heading("PERSÖNLICHE REKORDE","Bestenlisten"); for(int i=0;i<3;i++){gap(16);table(i);}button("Zurück",true,()->menu(null)); }
    @Override public void onBackPressed() { if(atMenu) finish(); else menu(playing?"Runde abgebrochen – kein Bestenlisteneintrag.":null); }
    @Override protected void onPause() { super.onPause(); if(playing || loading)menu("Runde beim Verlassen der App abgebrochen."); else { loadGeneration++; handler.removeCallbacksAndMessages(null); } }
    @Override protected void onSaveInstanceState(Bundle out) { out.putInt("difficulty",difficulty); super.onSaveInstanceState(out); }
    @Override protected void onDestroy() { destroyed=true; stop(); super.onDestroy(); }
}
