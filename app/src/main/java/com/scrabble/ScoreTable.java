package com.scrabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class ScoreTable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);
        Intent intent = getIntent();
        /*
        String s1 = intent.getStringExtra("Player1");
        String s2 = intent.getStringExtra("Player2");
        int x = s1.indexOf(" ");
        Player p1 = new Player(s1.substring(0, x));
        p1.setScore(Integer.parseInt(s1.substring(x + 1)));
        Player p2 = new Player(s2.substring(0, x = s2.indexOf(" ")));
        p2.setScore(Integer.parseInt(s2.substring(x + 1)));
        */
        List list = (List) intent.getSerializableExtra("Players");
        player1 = (Player) list.get(0);
        player2 = (Player) list.get(1);
        init();
    }

    Player player1, player2;

    public void init() {
        Button btn1 = (Button) findViewById(R.id.buttonForScore1);
        String x = player1.getScoreToString();
        btn1.setText((x));
        btn1 = (Button) findViewById(R.id.buttonForScore2);
        x = player2.getScoreToString();
        btn1.setText(x);
    }

    public void score1OnClick(View view) {
        goNextShowWords(player1);
    }

    public void score2OnClick(View view) {
        goNextShowWords(player2);
    }

    private void goNextShowWords(Player p) {
        Intent intent = new Intent(ScoreTable.this, WordsTable.class);
        intent.putExtra("Player", p);
        ScoreTable.this.startActivity(intent);
    }
}
