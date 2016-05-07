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
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
       Player p1= intent.getParcelableExtra("Player1");
        Player p2=intent.getParcelableExtra("Player2");
        init(p1,p2);
    }
    public void init(Player p1,Player p2)
    {
        Button btn1=(Button)findViewById(R.id.buttonForScore1);
        btn1.setText(p1.getScore());
        btn1=(Button)findViewById(R.id.buttonForScore2);
        btn1.setText(p2.getScore());
    }
}
