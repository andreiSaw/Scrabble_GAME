package com.scrabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class ScoreTable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);
        Intent intent = getIntent();
        String s1 = intent.getStringExtra("Player1");
        String s2 = intent.getStringExtra("Player2");
        int x = s1.indexOf(" ");
        Player p1 = new Player(s1.substring(0, x));
        p1.setScore(Integer.parseInt(s1.substring(x + 1)));
        Player p2 = new Player(s2.substring(0, x = s2.indexOf(" ")));
        p2.setScore(Integer.parseInt(s2.substring(x + 1)));

        init(p1, p2);
    }

    public void init(Player p1, Player p2) {
        Button btn1 = (Button) findViewById(R.id.buttonForScore1);
        String x = p1.getScoreToString();
        btn1.setText((x));
        btn1 = (Button) findViewById(R.id.buttonForScore2);
        x = p2.getScoreToString();
        btn1.setText(x);
    }
}
