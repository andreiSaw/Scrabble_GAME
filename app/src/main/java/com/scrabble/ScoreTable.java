package com.scrabble;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.List;

public class ScoreTable extends MainActivity {

    Player player1, player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);
        Intent intent = getIntent();
        List list = (List) intent.getSerializableExtra("Players");
        player1 = (Player) list.get(0);
        player2 = (Player) list.get(1);
        init();
    }

    public void init() {
        Button btn1 = (Button) findViewById(R.id.buttonForScore1);
        String x = player1.getScoreToString();
        btn1.setText((x));
        btn1 = (Button) findViewById(R.id.buttonForScore2);
        x = player2.getScoreToString();
        btn1.setText(x);
    }
}
