package com.scrabble;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import dawg_pack.DAFSA;
import trie_pack.Trie;

public class GameActivity extends AppCompatActivity {
    Player p1, p2;
    Player curPlayer;
    Button sbmButton, scrButton, plrButton;
    Rack rack;
    Trie trie;
    Dictionary dic = new Dictionary();
    Board pool;
    int dopID;
    String _letterBuf = "";
    Vector<Pair<String, Integer>> vectorOfLettersWorth;
    List<String> playedWords = new ArrayList<>();
    private Bag bag;
    private static int WIDTH = 1080, HEIGHT = 1920;
    private static double coef = 0.1388888888888889;
    private static int _size = 150;

    /*
        private static final Color DL_COLOR = new Color(140, 230, 250);
        private static final Color DW_COLOR = new Color(255, 150, 150);
        private static final Color TL_COLOR = new Color(176, 229, 124);
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        loadGameResolution();
        loadVocab();
        loadPlayers();
        loadPool();
        loadTable();
        dic.loadDic(this.getResources().openRawResource(R.raw.words));
        loadRack();
        fisrtFillingRack();
        loadAllButtons();
        loadDistribution();
        loadListeners();

    }

    private void loadGameResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        ScrabbleTile.setResolution(width, height);
        HEIGHT = height;
        WIDTH = width;
        double d = (WIDTH) * coef;
        _size = (int) d;
    }

    private void loadAllButtons() {
        loadSubmitButton();
        loadScoreButton();
        loadPlayerButton();
    }

    private void loadSubmitButton() {
        int _margin = 1;
        sbmButton = new Button(this);
        sbmButton.setText("S U B M I T");
        sbmButton.setBackgroundResource(R.drawable.button_primary_selector);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(_size * 2, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ABOVE, rack.getButtonID(POOL_SIZE - 1));
        params.setMargins(_margin, _margin, _margin, _margin);
        sbmButton.setLayoutParams(params);
        sbmButton.setId(++dopID);
        relativeLayout.addView(sbmButton);
        sbmButton.setOnClickListener(submitButtonListener);
    }

    private void loadScoreButton() {
        int _margin = 1;
        scrButton = new Button(this);
        scrButton.setText("S C O R E");
        scrButton.setBackgroundResource(R.drawable.button_orange_opacity);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(_size * 2, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(_margin, _margin, _margin, _margin);
        params.addRule(RelativeLayout.LEFT_OF, sbmButton.getId());
        params.addRule(RelativeLayout.ABOVE, rack.getButtonID(POOL_SIZE / 2));
        scrButton.setLayoutParams(params);
        scrButton.setId(++dopID);
        relativeLayout.addView(scrButton);
        scrButton.setOnClickListener(scrButtonListener);
    }

    private void loadPlayerButton() {
        int _margin = 1;
        plrButton = new Button(this);
        plrButton.setText(curPlayer.getName());
        plrButton.setBackgroundResource(R.drawable.button_info_selector);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.LEFT_OF, scrButton.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ABOVE, rack.getButtonID(0));
        params.setMargins(_margin, _margin, _margin, _margin);
        plrButton.setLayoutParams(params);
        plrButton.setId(++dopID);
        relativeLayout.addView(plrButton);
    }


    private void loadPlayers() {
        p1 = new Player("Player1");
        p2 = new Player("Player2");
        curPlayer = p1;
    }

    private void loadPool() {
        pool = new Board(POOL_SIZE);
        rack = new Rack(POOL_SIZE);
        bag = new Bag();
    }

    private Button.OnClickListener scrButtonListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GameActivity.this, ScoreTable.class);
            List<Player> list = new ArrayList<>();
            list.add(p1);
            list.add(p2);
            //TODO: check if it works
            intent.putExtra("Players", (Serializable) list);
         /*
            intent.putExtra("Player1", p1.toString());
            intent.putExtra("Player2", p2.toString());
*/
            GameActivity.this.startActivity(intent);
        }
    };
    private Button.OnClickListener submitButtonListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            boolean flagIfWordPlayed = false;
            Vector<Integer> cols = new Vector<>(), rows = new Vector<>();
            ScrabbleTile x;
            String y;
            String word = "";
            int curColumn, curRow;
            int columnStarts = 0, columnEnds = 0, rowStarts = 0, rowEnds = 0;
            //i - is row
            //j - is column

            for (int i = 0; i < POOL_SIZE; ++i) {
                for (int j = 0; j < POOL_SIZE; ++j) {
                    x = (ScrabbleTile) findViewById(pool.getButtonID(i, j));
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
            //TODO: anchors to prevent long time
            //TODO: SIMPLIFY
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
                                x = (ScrabbleTile) findViewById(pool.getButtonID(ii, jj));
                                x.setLocked(true);
                            }
                            while (ii != rowEnds);
                        }
                        while (jj != columnEnds);

                        curPlayer.updateScore(getWordReward(word));
                        //set button style to green color
                        sbmButton.setBackgroundResource(R.drawable.button_success_selector);
                        //delete word from dic
                        dic.removeWord(word);
                        //if one word played -raise flag
                        flagIfWordPlayed = true;
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
                                x = (ScrabbleTile) findViewById(pool.getButtonID(ii, jj));
                                x.setLocked(true);
                            }
                            while (jj != columnEnds);
                        }
                        while (ii != rowEnds);
                        curPlayer.updateScore(getWordReward(word));
                        //set button style to green color
                        sbmButton.setBackgroundResource(R.drawable.button_success_selector);
                        //delete word from dic
                        dic.removeWord(word);
                        //if one word played -raise flag
                        flagIfWordPlayed = true;
                    }
                }
                word = "";
            }
            //TODO: kick out all not locked tiles
            for (int i = 0; i < POOL_SIZE; i++) {
                for (int j = 0; j < POOL_SIZE; j++) {
                    if (!pool.isButtonLocked(i, j) && !pool.isButtonEmpty(i, j)) {
                        rack.pushButtonValue(pool.getButtonValue(i, j));
                    }
                }
            }
            //if nor 1 one word played - turn is stays over current player
            if (!flagIfWordPlayed) {

            } else {
                changeCurrentPlayer();
            }
            fillRack();//ask new word from trie to rack
        }
    };

    private void changeCurrentPlayer() {
        if (curPlayer.equals(p1)) {
            curPlayer = p2;
        } else {
            curPlayer = p1;
        }
        plrButton.setText(curPlayer.getName());
    }

    private int getWordReward(String word) {
        addWordToStore(word);
        int score = 0;
        while (word.length() > 0) {
            int x = word.charAt(0) - 'a';
            score += vectorOfLettersWorth.get(x).second;
            word = word.substring(1);
        }
        return score;
    }

    private boolean loadDistribution() {
        //
        // https://en.wikipedia.org/wiki/Scrabble_letter_distributions
        //
        vectorOfLettersWorth = new Vector<>();
        DataInputStream dataInputStream;
        String text;
        String letter;
        Integer m;
        try {
            dataInputStream = new DataInputStream(getResources().openRawResource(R.raw.distribution));
            while (dataInputStream.available() > 0) {
                text = (dataInputStream.readLine());
                letter = text.substring(0, 1);
                text = text.substring(4);
                m = Integer.parseInt(text);
                vectorOfLettersWorth.add(new Pair<>(letter, m));
            }
            dataInputStream.close();
        } catch (IOException ex) {
            System.out.println("File Not found");
            return false;
        }
        /*
        tv = new TextView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_LEFT);
        params.addRule(RelativeLayout.ABOVE, sbmButton.getId());
        tv.setLayoutParams(params);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        relativeLayout.addView(tv);
        */
        return true;
    }

    private void loadListeners() {
        //
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                final ScrabbleTile button = (ScrabbleTile) findViewById(pool.getButtonID(i, j));
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
            final ScrabbleTile button = (ScrabbleTile) findViewById(rack.getButtonID(i));
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

    private void loadRack() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        for (int i = 0; i < POOL_SIZE; ++i) {
            ScrabbleTile bt = new ScrabbleTile(this);
            bt.setId(++dopID);
            bt.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (i != 0) {
                bt.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bt.addRule(RelativeLayout.RIGHT_OF, rack.getButtonID(i - 1));
            }
            relativeLayout.addView(bt);
            rack.setButtonID(i, bt.getId());
        }

    }

    private int countRack() {
        int count = POOL_SIZE;
        ScrabbleTile x;
        for (int i = 0; i < POOL_SIZE; ++i) {
            x = (ScrabbleTile) findViewById(rack.getButtonID(i));
            if (!x.isEmpty()) {
                --count;
            }
        }
        return count;
    }

    private void fillRack() {
        String letters = bag.getLettersToString(countRack());
        fillRack(letters);
        //simplify

    }

    private void fillRack(String text) {
        int id;
        for (int i = 0; i < POOL_SIZE; ++i) {
            id = rack.getButtonID(i);
            ScrabbleTile btn = (ScrabbleTile) findViewById(id);
            if (btn.isEmpty()) {
                String string = "" + text.charAt(0);
                btn.setText(string);
                rack.setButtonValue(i, string);
                text = text.substring(1);
            }
        }
    }

    private void fisrtFillingRack() {
        String d = dic.getRequiredSize(7);
        fillRack(d);
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

        pool.setButtonID(0, 0, curID);


//razmetka stranicy
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                if (i != 0 && j != 0) {
                    ScrabbleTile bt = new ScrabbleTile(this);

                    bt.addRule(RelativeLayout.RIGHT_OF, pool.getButtonID(i, j - 1));
                    bt.addRule(RelativeLayout.BELOW, pool.getButtonID(i - 1, j));
                    bt.addRule(RelativeLayout.ALIGN_LEFT);

                    int x = curID++;
                    bt.setId(x);
                    pool.setButtonID(i, j, x);
                    bt.setText(" ");
                    pool.setButtonValue(i, j, " ");

                    relativeLayout.addView(bt);
                } else if (i == 0 && j != 0) {
                    ScrabbleTile bt = new ScrabbleTile(this);

                    int x = curID++;
                    bt.setId(x);
                    pool.setButtonID(i, j, x);
                    bt.setText(" ");
                    pool.setButtonValue(i, j, " ");

                    bt.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    bt.addRule(RelativeLayout.RIGHT_OF, pool.getButtonID(i, j - 1));
                    // bt.setLayoutParams(params);
                    relativeLayout.addView(bt);
                } else if (i != 0) {
                    //} else if (i != 0 && j == 0) {
                    ScrabbleTile bt = new ScrabbleTile(this);

                    int x = curID++;
                    bt.setId(x);
                    pool.setButtonID(i, j, x);
                    bt.setText(" ");
                    pool.setButtonValue(i, j, " ");

                    bt.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    bt.addRule(RelativeLayout.BELOW, pool.getButtonID(i - 1, j));
                    relativeLayout.addView(bt);
                } else {
                    //} else if (i == 0 && j == 0) {
                    ScrabbleTile bt = ((ScrabbleTile) findViewById(curID++));
                    bt.initWH();
                    pool.setButtonID(0, 0, bt.getId());
                    bt.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    bt.addRule(RelativeLayout.ALIGN_PARENT_START);
                    bt.setLocked(false);
                    bt.setEmpty(true);
                }
            }
        }
        dopID = curID;
    }

    private void addWordToStore(String word) {
        curPlayer.addPlayedWord(word);
        playedWords.add(word);
    }

    private int usingDawg() {
        DataInputStream dataInputStream;
        String text;
        DAFSA dafsa = new DAFSA();
        int wordCount = 0;
        try {
            dataInputStream = new DataInputStream(getResources().openRawResource(R.raw.words));
            while (dataInputStream.available() > 0) {
                text = dataInputStream.readLine();
                dafsa.insert(text);
                wordCount++;
            }
            dataInputStream.close();

            /*
            while (scanner.hasNextLine()){
                String nextline = scanner
                        .nextLine()
                        .toLowerCase()
                        .replaceAll("\n", "")
                        .replaceAll("'", "");
                String[] words = nextline.split(" ");
               */
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordCount;
    }
}



