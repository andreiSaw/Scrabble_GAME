package com.activity_pack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tools.R;
import com.tools_pack.Player;
import com.tools_pack.WordsViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class WordsViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordsview);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Player p = (Player) extras.getSerializable("Player1");
        List<String> records = p.getPlayedWords();
        ArrayList<String> list = extras.getStringArrayList("Words");
        ArrayList<Integer> scores=extras.getIntegerArrayList("Points");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        WordsViewAdapter adapter = new WordsViewAdapter(records, list,scores);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }
}
