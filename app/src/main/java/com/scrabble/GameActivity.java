package com.scrabble;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import trie_pack.Gaddag;
import trie_pack.Trie;

public class GameActivity extends AppCompatActivity {
    Button sbmButton;
    int[] bottomLineIDs = new int[7];
    Trie trie;
    Dictionary dic = new Dictionary();
    Board pool;
    int dopID;
    String _letterBuf = "";
    Vector<Pair<String, Double>> vectorOfHeuristic;
    TextView tv;
    Double score = 0.0;
    List<String> playedWords = new ArrayList<>();
    private Bag bag;

    /*
        private static final Color DL_COLOR = new Color(140, 230, 250);
        private static final Color DW_COLOR = new Color(255, 150, 150);
        private static final Color TL_COLOR = new Color(176, 229, 124);
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        loadVocab();
        loadPool();
        loadTable();
        dic.loadDic(this.getResources().openRawResource(R.raw.words));
        loadBottomLine();
        loadBag();
        fillBottomLine();
        loadSubmitButton();
        loadHeuristic();
        game();

    }

    private void loadPool() {
        pool = new Board(POOL_SIZE);
    }


    private void loadBag() {
        bag = new Bag();
    }

    private Button.OnClickListener submitButtonListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            Vector<Integer> cols = new Vector<>(), rows = new Vector<>();
            MyButtton x;
            String y;
            String word = "";
            int curColumn, curRow;
            int columnStarts = 0, columnEnds = 0, rowStarts = 0, rowEnds = 0;
            //i - is row
            //j - is column

            for (int i = 0; i < POOL_SIZE; ++i) {
                for (int j = 0; j < POOL_SIZE; ++j) {
                    x = (MyButtton) findViewById(pool.getButtonID(i, j));
                    pool.setButtonValue(i, j, x.getText().toString());
                    if (!x.isEmpty() && !x.isLocked()) {
                        if (!cols.contains(j)) {
                            cols.add(j);
                        }
                        if (!rows.contains(i)) {
                            rows.add(i);
                        }
                    }
                }
            }

            //look over each column
            // downward
            for (int i : cols) {
                curColumn = i;
                curRow = 0;
                y = pool.getButtonValue(curRow, curColumn);
                //find where word starts
                while (curRow != POOL_SIZE) {
                    y = pool.getButtonValue(curRow, curColumn);
                    if (Objects.equals(y, " ")) {
                        ++curRow;
                        //continue;
                    } else {
                        rowStarts = curRow;
                        break;
                    }
                }
                //find where word ends
                while (!Objects.equals(y, " ") && curRow != POOL_SIZE) {
                    word += y;
                    ++curRow;
                    if (curRow == POOL_SIZE) {
                        break;
                    }
                    y = pool.getButtonValue(curRow, curColumn);
                }
                rowEnds = curRow - 1;

                //checking
                if (word.length() > 1) {
                    if (dic.isValidWord(word)) {
                        int ii = rowStarts - 1, jj = columnStarts - 1;
                        do {
                            ++jj;
                            do {
                                ++ii;
                                x = (MyButtton) findViewById(pool.getButtonID(ii, jj));
                                x.setLocked(true);
                            }
                            while (ii != rowEnds);
                        }
                        while (jj != columnEnds);

                        updateScore(word);
                        //set button style to green color
                        sbmButton.setBackgroundResource(R.drawable.button_success_selector);
                        //delete word from dic
                        dic.removeWord(word);
                    }
                }
                word = "";
            }

            //look over each row
            //rightward

            for (int i : rows) {
                curRow = i;
                curColumn = 0;
                y = pool.getButtonValue(curRow, curColumn);
                //find where word starts
                while (curColumn != POOL_SIZE) {
                    y = pool.getButtonValue(curRow, curColumn);
                    if (Objects.equals(y, " ")) {
                        ++curColumn;
                        //continue;
                    } else {
                        columnStarts = curColumn;
                        break;
                    }
                }
                //find where word ends
                while (!Objects.equals(y, " ") && curColumn != POOL_SIZE) {
                    word += y;
                    ++curColumn;

                    if (curColumn == POOL_SIZE) {
                        break;
                    }
                    y = pool.getButtonValue(curRow, curColumn);
                }
                columnEnds = curColumn - 1;

                //checking
                if (word.length() > 1) {
                    if (dic.isValidWord(word)) {
                        int ii = rowStarts - 1, jj = columnStarts - 1;
                        do {
                            ++ii;
                            do {
                                ++jj;
                                x = (MyButtton) findViewById(pool.getButtonID(ii, jj));
                                x.setLocked(true);
                            }
                            while (jj != columnEnds);
                        }
                        while (ii != rowEnds);

                        updateScore(word);
                        //set button style to green color
                        sbmButton.setBackgroundResource(R.drawable.button_success_selector);
                        //delete word from dic
                        dic.removeWord(word);
                    }
                }
                word = "";
            }
            //ask new word from trie to bottom (rack)
            fillBottomLine();
        }
    };

    private void loadSubmitButton() {
        sbmButton = new Button(this);
        sbmButton.setText("S U B M I T");
        sbmButton.setBackgroundResource(R.drawable.button_primary_selector);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ABOVE, bottomLineIDs[POOL_SIZE / 2]);
        sbmButton.setLayoutParams(params);
        sbmButton.setId(++dopID);
        relativeLayout.addView(sbmButton);
        //checking
        sbmButton.setOnClickListener(submitButtonListener);
    }

    private void updateScore(String word) {
        addWordToStore(word);
        String w = "";
        while (word.length() > 0) {
            w += word.charAt(0);
            int x = word.charAt(0) - 'a';
            score += vectorOfHeuristic.get(x).second;
            word = word.substring(1);
            w = "";
        }
        Double s = score * 100;
        tv.setText(String.valueOf(s.intValue()));

    }

    private boolean loadHeuristic() {
        vectorOfHeuristic = new Vector<>();
        DataInputStream dataInputStream;
        String text;
        String letter;
        Double m;
        try {
            dataInputStream = new DataInputStream(getResources().openRawResource(R.raw.heuristic));
            while (dataInputStream.available() > 0) {
                text = (dataInputStream.readLine());
                letter = text.substring(0, 1);
                text = text.substring(3);
                m = Double.parseDouble(text);
                vectorOfHeuristic.add(new Pair<>(letter, m));
            }
            dataInputStream.close();
        } catch (IOException ex) {
            System.out.println("File Not found");
            return false;
        }
        tv = new TextView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_LEFT);
        params.addRule(RelativeLayout.ABOVE, sbmButton.getId());
        tv.setLayoutParams(params);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        relativeLayout.addView(tv);
        return true;
    }

    private void game() {
        //
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                final MyButtton button = (MyButtton) findViewById(pool.getButtonID(i, j));
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!Objects.equals(_letterBuf, "") && button.isEmpty()) {
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
            bt.setLayoutParams(params);
            relativeLayout.addView(bt);
            bottomLineIDs[i] = bt.getId();
        }

    }

    private int countBottomLine() {
        int count = POOL_SIZE;
        MyButtton x;
        for (int i = 0; i < POOL_SIZE; ++i) {
            x = (MyButtton) findViewById(bottomLineIDs[i]);
            if (!x.isEmpty()) {
                --count;
            }
        }
        return count;
    }

    private void fillBottomLine() {
        List letters = bag.getLetters(countBottomLine());
        int count = letters.size();
        for (int i = 0; i < count; ++i) {
            String string = "" + letters.get(0);
            MyButtton btn = (MyButtton) findViewById(bottomLineIDs[i]);
            if (btn.isEmpty()) {
                btn.setText(string);
                letters.remove(0);
            }


        }
    }

    private final int POOL_SIZE = 7;

    private boolean loadVocab() {
        //GADDAG gaddag = new GADDAG();
        Gaddag gaddag=new Gaddag();
        DataInputStream dataInputStream;
        trie = new Trie();
        String text;
        try {
            dataInputStream = new DataInputStream(getResources().openRawResource(R.raw.words));
            while (dataInputStream.available() > 0) {
                text = dataInputStream.readLine();

                gaddag.addWord(text);

                //trie.addWord(text);
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

        pool.setButtonID(0, 0, curID);


//razmetka stranicy
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                if (i != 0 && j != 0) {
                    params.addRule(RelativeLayout.RIGHT_OF, pool.getButtonID(i, j - 1));
                    params.addRule(RelativeLayout.BELOW, pool.getButtonID(i - 1, j));
                    params.addRule(RelativeLayout.ALIGN_LEFT);

                    MyButtton bt = new MyButtton(this);
                    bt.setText(" ");
                    bt.setOnPool(true);
                    bt.setId(curID++);
                    pool.setButtonID(i, j, bt.getId());

                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i == 0 && j != 0) {
                    MyButtton bt = new MyButtton(this);
                    bt.setText(" ");
                    bt.setOnPool(true);

                    bt.setId(curID++);

                    pool.setButtonID(i, j, bt.getId());

                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.addRule(RelativeLayout.RIGHT_OF, pool.getButtonID(i, j - 1));
                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i != 0) {
                    //} else if (i != 0 && j == 0) {
                    MyButtton bt = new MyButtton(this);
                    bt.setText(" ");
                    bt.setOnPool(true);

                    bt.setId(curID++);

                    pool.setButtonID(i, j, bt.getId());

                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.BELOW, pool.getButtonID(i - 1, j));
                    bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else {
                    //} else if (i == 0 && j == 0) {
                    MyButtton bt = ((MyButtton) findViewById(curID++));
                    pool.setButtonID(0,0,bt.getId());
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    bt.setLayoutParams(params);
                    bt.setOnPool(true);
                    bt.setLocked(false);
                    bt.setEmpty(true);
                }
            }
        }
        dopID = curID;
    }

    private void addWordToStore(String word) {
        playedWords.add(word);
    }
}



