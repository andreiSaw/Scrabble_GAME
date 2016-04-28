package com.scrabble;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    int[] bottomLineIDs = new int[7];
    Trie trie;
    Dictionary dic = new Dictionary();
    int[][] masOfIDs = new int[7][7];
    int dopID;
    String _letterBuf = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadVocab();
        loadTable();
        dic.loadDic(this.getResources().openRawResource(R.raw.words));
        loadBottomLine();
        loadSubmitButton();
        game();

    }

    private void loadSubmitButton() {
        Button btn=new Button(this);
        btn.setText("S U B M I T");
        btn.setBackgroundResource(R.drawable.button_success_selector);
        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.secondRelativeLayout);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ABOVE,bottomLineIDs[POOL_SIZE/2]);
        btn.setLayoutParams(params);
        relativeLayout.addView(btn);
        //checking
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyButtton x;
                for(int i=0;i<POOL_SIZE;++i)
                {
                    for(int j=0;j<POOL_SIZE;++j)
                    {
                        x=(MyButtton)findViewById(masOfIDs[i][j]);
                        if(!x.isEmpty()&&!x.isLocked())
                        {
                            x.setLocked(true);
                        }
                    }
                }
            }
        });
    }

    private void game() {
        //
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                final MyButtton button = (MyButtton) findViewById(masOfIDs[i][j]);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!Objects.equals(_letterBuf, "") && button.isEmpty()) {
                            //if (_letterBuf != "" && button.getText() == " ") {
                            button.setText(_letterBuf);
                            _letterBuf = "";
                        } else if (!button.isEmpty() && !button.isLocked() && Objects.equals(_letterBuf, "")) {
                            _letterBuf = button.getText().toString();
                            button.setText(" ");
                        }
                    }
                });
            }
        }
        for (int i = 0; i < POOL_SIZE; ++i) {
            final MyButtton button = (MyButtton) findViewById(bottomLineIDs[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Objects.equals(_letterBuf, "") && !button.isEmpty()) {
                        _letterBuf = button.getText().toString();
                        button.setText(" ");
                    } else if (!Objects.equals(_letterBuf, "") && button.isEmpty()) {
                        button.setText(_letterBuf);
                        _letterBuf = "";
                    }
                }
            });
        }
    }

    private void loadBottomLine() {
        String str = dic.getRequiredSize(7);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        for (int i = 0; i < POOL_SIZE; ++i) {
            MyButtton bt = new MyButtton(this);
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
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                if (i != 0 && j != 0) {
                    params.addRule(RelativeLayout.RIGHT_OF, masOfIDs[i][j - 1]);
                    params.addRule(RelativeLayout.BELOW, masOfIDs[i - 1][j]);
                    params.addRule(RelativeLayout.ALIGN_LEFT);

                    MyButtton bt = new MyButtton(this);
                    bt.setText(" ");
                    bt.setOnPool(true);

                    bt.setId(curID++);
                    masOfIDs[i][j] = bt.getId();

                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i == 0 && j != 0) {
                    MyButtton bt = new MyButtton(this);
                    bt.setText(" ");
                    bt.setOnPool(true);

                    bt.setId(curID++);
                    masOfIDs[i][j] = bt.getId();

                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.addRule(RelativeLayout.RIGHT_OF, masOfIDs[i][j - 1]);
                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i != 0 && j == 0) {
                    MyButtton bt = new MyButtton(this);
                    bt.setText(" ");
                    bt.setOnPool(true);

                    bt.setId(curID++);
                    masOfIDs[i][j] = bt.getId();

                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.BELOW, masOfIDs[i - 1][j]);
                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i == 0 && j == 0) {

                    MyButtton bt = ((MyButtton) findViewById(curID++));
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    bt.setLayoutParams(params);
                    bt.setOnPool(true);
                }
            }
        }
    }
}



