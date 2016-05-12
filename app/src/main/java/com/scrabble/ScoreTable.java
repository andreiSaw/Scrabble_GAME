package com.scrabble;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ScoreTable extends MainActivity {

    protected static Player player1, player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);
        Intent intent = getIntent();

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
