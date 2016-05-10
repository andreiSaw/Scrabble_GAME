package com.scrabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class WordsTable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_table);
        Intent intent = getIntent();
        Player p = intent.getParcelableExtra("Player");
        if(p.getPlayedWords().isEmpty())
        {
            return;
        }
        init(p);
    }

    private void init(Player p) {
        List played = p.getPlayedWords();
        int k = played.size();
        final ListView listView = (ListView) findViewById(R.id.listView);
        for (int i = 0; i < k; ++i) {
            TextView tv = new TextView(this);
            tv.setText((CharSequence) played.get(i));
            listView.addView(tv);
        }
    }
}
