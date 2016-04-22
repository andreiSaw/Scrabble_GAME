package com.scrabble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.DataInputStream;
import java.io.IOException;

public class GameActivity extends AppCompatActivity {
    int[] bottomLineIDs = new int[7];
    Trie trie;
    Dictionary dic = new Dictionary();
    int[][] masOfIDs = new int[7][7];
    int dopID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadVocab();
        loadTable();
        dic.loadDic(this.getResources().openRawResource(R.raw.words));
        loadBottomLine();
        game();

    }

    private void game() {
        //
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                final Button button = (Button) findViewById(masOfIDs[i][j]);

                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (button.getHint() != "Pressed") {
                            button.setHint("Pressed");

                        } else {

                            button.setHint("");
                        }
                    }
                });
            }
        }
    }

    private void loadBottomLine() {
        String str = dic.getRequiredSize(7);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        for (int i = 0; i < POOL_SIZE; i++) {
            Button bt = new Button(this);
            bt.setId(++dopID);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (i != 0) {
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.RIGHT_OF, bottomLineIDs[i - 1]);
            }

            String dopStr = "" + str.charAt(i);
            bt.setText(dopStr);
            bt.setLayoutParams(params);
            relativeLayout.addView(bt);
            bottomLineIDs[i] = bt.getId();
        }

    }

    private final int POOL_SIZE = 7;

    private boolean loadVocab() {
        DataInputStream dataInputStream;
        trie = new Trie();
        String text;
        try {
            dataInputStream = new DataInputStream(getResources().openRawResource(R.raw.words7));
            while (dataInputStream.available() > 0) {
                text = dataInputStream.readLine();
                trie.addWord(text);
            }
            dataInputStream.close();
        } catch (IOException ex) {
            System.out.println("File Not found");
            return false;
        }
        return true;
    }

    private void loadTable() {

        int curID = R.id.button00;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);

        masOfIDs[0][0] = curID;

//razmetka stranicy
        for (int i = 0; i < POOL_SIZE; i++) {
            for (int j = 0; j < POOL_SIZE; j++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                if (i != 0 && j != 0) {
                    params.addRule(RelativeLayout.RIGHT_OF, masOfIDs[i][j - 1]);
                    params.addRule(RelativeLayout.BELOW, masOfIDs[i - 1][j]);
                    params.addRule(RelativeLayout.ALIGN_LEFT);

                    Button bt = new Button(this);
                    bt.setText(" ");

                    bt.setId(curID++);
                    masOfIDs[i][j] = bt.getId();

                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i == 0 && j != 0) {
                    Button bt = new Button(this);
                    bt.setText(" ");

                    bt.setId(curID++);
                    masOfIDs[i][j] = bt.getId();

                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.addRule(RelativeLayout.RIGHT_OF, masOfIDs[i][j - 1]);
                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i != 0 && j == 0) {
                    Button bt = new Button(this);
                    bt.setText(" ");

                    bt.setId(curID++);
                    masOfIDs[i][j] = bt.getId();

                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.BELOW, masOfIDs[i - 1][j]);
                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i == 0 && j == 0) {
                    //dopID = R.id.button00;
                    Button bt = (Button) findViewById(curID++);
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    bt.setLayoutParams(params);
                }
            }
        }
        //dopID = masOfIDs[POOL_SIZE - 1][POOL_SIZE - 1];
    }
}



