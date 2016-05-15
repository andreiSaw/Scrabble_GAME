package activity_pack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tools.R;
import com.tools_pack.Player;
import com.tools_pack.WordsViewActivity;

import java.util.List;


public class RecyclerViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordsview);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Player p = (Player) extras.getSerializable("Player");
        List<String> records = p.getPlayedWords();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        WordsViewActivity adapter = new WordsViewActivity(records);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }
}
