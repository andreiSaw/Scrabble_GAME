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
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class GameActivity extends AppCompatActivity {
    Button sbmButton;
    int[] bottomLineIDs = new int[7];
    Trie trie;
    Dictionary dic = new Dictionary();
    int[][] masOfIDs = new int[7][7];
    int dopID;
    String _letterBuf = "";
    Vector<Pair<String, Double>> vectorOfHeuristic;
    TextView tv;
    Double score = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadVocab();
        loadTable();
        dic.loadDic(this.getResources().openRawResource(R.raw.words));
        loadBottomLine();
        loadSubmitButton();
        loadHeuristic();
        game();

    }

    private Button.OnClickListener submitButtonListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            Vector<Integer> cols = new Vector<>(), rows = new Vector<>();
            MyButtton x;
            Vector<MyButtton> myButttons = new Vector<>();
            String word = "";
            int curColumn, curRow;
            int columnStarts = 0, columnEnds = 0, rowStarts = 0, rowEnds = 0;
            //i - is row
            //j - is column
            for (int i = 0; i < POOL_SIZE; ++i) {
                for (int j = 0; j < POOL_SIZE; ++j) {
                    x = (MyButtton) findViewById(masOfIDs[i][j]);
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
                x = (MyButtton) findViewById(masOfIDs[curRow][curColumn]);
                //find where word starts
                while (curRow != POOL_SIZE) {
                    x = (MyButtton) findViewById(masOfIDs[curRow][curColumn]);
                    if (x.isEmpty()) {
                        ++curRow;
                        //continue;
                    } else {
                        rowStarts = curRow;
                        break;
                    }
                }
                //find where word ends
                while (!x.isEmpty() && curRow != POOL_SIZE) {
                    myButttons.add(x);
                    word += x.getText();
                    ++curRow;
                    if (curRow == POOL_SIZE) {
                        break;
                    }
                    x = (MyButtton) findViewById(masOfIDs[curRow][curColumn]);
                }
                rowEnds = curRow - 1;
                //checking
                if (word.length() > 2) {
                    List myList = trie.getWords(word);

                    if (!myList.isEmpty()) {
                        if (myList.contains(word)) {
                            for (MyButtton y : myButttons) {
                                y.setLocked(true);
                            }
                            //set button style to green color
                            sbmButton.setBackgroundResource(R.drawable.button_success_selector);
                            //delete word from dic
                            dic.removeWord(word);
                        }
                    } else {
                        sbmButton.setBackgroundResource(R.drawable.button_danger_selector);
                    }
                    //ask new word from trie to bottom (rack)
                    fillBottomLine();
                }
                word = "";
                myButttons.removeAllElements();
            }
//look over each row
            //rightward

            for (int i : rows) {
                curRow = i;
                curColumn = 0;
                x = (MyButtton) findViewById(masOfIDs[curRow][curColumn]);

                //find where word starts
                while (curColumn != POOL_SIZE) {
                    x = (MyButtton) findViewById(masOfIDs[curRow][curColumn]);
                    if (x.isEmpty()) {
                        ++curColumn;
                        //not nessesary
                        //continue;
                    } else {
                        columnStarts = curColumn;
                        break;
                    }
                }
                //find where word ends
                while (!x.isEmpty() && curColumn != POOL_SIZE) {
                    myButttons.add(x);
                    word += x.getText();
                    ++curColumn;

                    if (curColumn == POOL_SIZE) {
                        break;
                    }

                    x = (MyButtton) findViewById(masOfIDs[curRow][curColumn]);
                }
                columnEnds = curColumn - 1;

                //checking
                if (word.length() > 2) {
                    List myList = trie.getWords(word);

                    if (!myList.isEmpty()) {
                        if (myList.contains(word)) {
                            for (MyButtton y : myButttons) {
                                y.setLocked(true);

                            }
                            updateScore(word);
                            //set button style to green color
                            sbmButton.setBackgroundResource(R.drawable.button_success_selector);
                            //delete word from dic
                            dic.removeWord(word);
                        }
                        //ask new word from trie to bottom (rack)
                        fillBottomLine();
                    } else {
                        sbmButton.setBackgroundResource(R.drawable.button_danger_selector);
                    }
                }
                word = "";
                myButttons.removeAllElements();


            }
        }
    };

    private void loadSubmitButton() {
        sbmButton = new Button(this);
        sbmButton.setText("S U B M I T");
        // btn.setBackgroundResource(R.drawable.button_success_selector);
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
        String w = "";
        while (word.length() > 0) {
            w += word.charAt(0);
            int x = word.charAt(0) - 'a';
            score += vectorOfHeuristic.get(x).second;
            word = word.substring(1);
            w = "";
        }
        Double s=score*100;
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
                vectorOfHeuristic.add(new Pair<String, Double>(letter, m));
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

    private void fillBottomLine() {
        String word = dic.getRequiredSize(7);

        for (int i = 0; i < POOL_SIZE; ++i) {
            String string = "" + word.charAt(i);
            MyButtton btn = (MyButtton) findViewById(bottomLineIDs[i]);
            btn.setText(string);
        }
    }

    private final int POOL_SIZE = 7;

    private boolean loadVocab() {
        DataInputStream dataInputStream;
        trie = new Trie();
        String text;
        try {
            dataInputStream = new DataInputStream(getResources().openRawResource(R.raw.words));
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
                    bt.setLocked(false);
                    bt.setEmpty(true);
                }
            }
        }
    }
}



