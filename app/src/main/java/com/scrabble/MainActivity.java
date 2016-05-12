package com.scrabble;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private final int POOL_SIZE = 7;
    protected Player curPlayer;
    private Player p1, p2;
    private Button sbmButton, scrButton, plrButton;
    private Rack rack;
    private Dictionary dic = new Dictionary();
    private Board pool;
    private String _letterBuf = "";
    private Vector<Pair<String, Integer>> vectorOfLettersWorth;
    private Bag bag;

    private Button.OnClickListener scrButtonListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ScoreTable.class);
            List<Player> list = new ArrayList<>();
            list.add(p1);
            list.add(p2);
            //TODO: check if it works
            intent.putExtra("Players", (Serializable) list);
            MainActivity.this.startActivity(intent);
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
                                pool.setButtonLocked(ii, jj, true);
                            }
                            while (ii != rowEnds);
                        }
                        while (jj != columnEnds);

                        curPlayer.updateScore(getWordReward(word, rowStarts, columnStarts, rowEnds, columnEnds));
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
                                pool.setButtonLocked(ii, jj, true);
                            }
                            while (jj != columnEnds);
                        }
                        while (ii != rowEnds);
                        curPlayer.updateScore(getWordReward(word, rowStarts, columnStarts, rowEnds, columnEnds));
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
                    boolean f1 = pool.isButtonLocked(i, j);
                    boolean f2 = pool.isButtonEmpty(i, j);
                    if (!f1 && !f2) {
                        rack.pushButtonValue(pool.getButtonValue(i, j));
                    }
                }
            }
            //TODO: shake app
            //if nor 1 one word played - turn is stays over current player
            if (!flagIfWordPlayed) {

            } else {
                changeCurrentPlayer();
            }
            fillRack();//ask new word from trie to rack
        }
    };
    private Drawer.Result drawerResult = null;
    private DrawerLayout.DrawerListener mDrawerListenernew = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            drawerResult.updateBadge(p1.getScoreToString(), 1);
            drawerResult.updateBadge(p2.getScoreToString(), 2);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadPlayers();
        loadNavD();
        loadGameResolution();
        loadPool();
        loadTable();
        loadDataWithScanner();
        loadRack();
        fisrtFillingRack();
        loadAllButtons();
        loadDistribution();
        loadListeners();

    }

    private void loadNavD() {
        /*
        https://habrahabr.ru/post/250765/
         */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(p1.getName()).withIcon(FontAwesome.Icon.faw_android).withIdentifier(1).withBadge(p1.getScoreToString()),
                        new PrimaryDrawerItem().withName(p2.getName()).withIcon(FontAwesome.Icon.faw_android).withIdentifier(2).withBadge(p2.getScoreToString()),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question),//create action
                        new PrimaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_paper_plane).withBadge("12+").withIdentifier(1)).withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            String str = ((Nameable) drawerItem).getName();
                            if (str == null) {
                                str = MainActivity.this.getString(((Nameable) drawerItem).getNameRes());
                            }
                            if (str.contains(p1.getName())) {
                                str += String.format(" has %d points", p1.getScore());

                            } else if (str.contains(p2.getName())) {
                                str += String.format(" has %d points", p2.getScore());
                            }
                            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                            Intent intent;
                            switch (str) {
                                case "Help":
                                    intent = new Intent(MainActivity.this, InfoTab.class);
                                    MainActivity.this.startActivity(intent);
                                    break;
                                /*
                                case "Player1":
                                    ScoreTable.player1 = p1;
                                    ScoreTable.player2 = p2;
                                    intent = new Intent(MainActivity.this, ScoreTable.class);
                                    MainActivity.this.startActivity(intent);
                                    break;
                                case "Player2":
                                    ScoreTable.player1 = p1;
                                    ScoreTable.player2 = p2;
                                    intent = new Intent(MainActivity.this, ScoreTable.class);
                                    MainActivity.this.startActivity(intent);
                                    break;*/
                                default:
                                    break;
                            }
                        }
/*
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
                                try {
                                    int badge = Integer.valueOf(badgeable.getBadge());
                                    if (badge > 0) {
                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
                                    }
                                } catch (Exception e) {
                                    Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
                                }
                                }
                                */
                    }
                })
                .build();
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerListenernew);
        //// TODO: onclick draw

    }

    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    // Заглушка, работа с меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Заглушка, работа с меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadGameResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        ScrabbleTile.setResolution(width, height);
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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ABOVE, rack.getButtonID(POOL_SIZE - 1));
        params.setMargins(_margin, _margin, _margin, _margin);
        sbmButton.setLayoutParams(params);
        sbmButton.setId(View.generateViewId());
        relativeLayout.addView(sbmButton);
        sbmButton.setOnClickListener(submitButtonListener);
    }

    private void loadScoreButton() {
        int _margin = 1;
        scrButton = new Button(this);
        scrButton.setText("S C O R E");
        scrButton.setBackgroundResource(R.drawable.button_orange_opacity);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(_margin, _margin, _margin, _margin);
        params.addRule(RelativeLayout.LEFT_OF, sbmButton.getId());
        params.addRule(RelativeLayout.ABOVE, rack.getButtonID(POOL_SIZE / 2));
        scrButton.setLayoutParams(params);
        scrButton.setId(View.generateViewId());
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
        plrButton.setId(View.generateViewId());
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

    private void changeCurrentPlayer() {
        if (curPlayer.equals(p1)) {
            curPlayer = p2;
        } else {
            curPlayer = p1;
        }
        plrButton.setText(curPlayer.getName());
    }

    private int getWordReward(String word, int istarts, int jstarts, int iends, int jends) {
        addWordToStore(word);
        int score = 0;
        boolean flag;
        //if (jstarts == jends) {
        flag = istarts == iends;
        while (word.length() > 0) {
            int x = word.charAt(0) - 'a';
            score += vectorOfLettersWorth.get(x).second * pool.getBonus(istarts, jstarts);
            if (!flag) {
                istarts++;
            } else {
                jstarts++;
            }
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
            int x = View.generateViewId();
            bt.setId(x);
            bt.initWH();
            bt.setMargins();
            bt.setBackgroundResource(R.color.colorForRack);
            rack.setButtonID(i, x);
            bt.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (i != 0) {
                bt.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bt.addRule(RelativeLayout.RIGHT_OF, rack.getButtonID(i - 1));
            }
            relativeLayout.addView(bt);

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

    /* private boolean loadVocab() {
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
 */
    private void loadTable() {

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
//TODO Builder pattern

        pool.setButtonID(0, 0, R.id.veryFirstButton);
        pool.setButtonValue(0, 0, " ");
        pool.setButtonEmpty(0, 0, true);
        pool.setButtonLocked(0, 0, false);


//razmetka stranicy
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                if (i != 0 && j != 0) {
                    ScrabbleTile bt = new ScrabbleTile(this);

                    int x = View.generateViewId();

                    bt.setId(x);
                    bt.setText(" ");
                    bt.loadBonuses(i, j);
                    bt.addRule(RelativeLayout.RIGHT_OF, pool.getButtonID(i, j - 1));
                    bt.addRule(RelativeLayout.BELOW, pool.getButtonID(i - 1, j));
                    bt.addRule(RelativeLayout.ALIGN_LEFT);
                    bt.setMargins();

                    pool.setButtonID(i, j, x);
                    pool.setButtonValue(i, j, " ");
                    pool.setButtonEmpty(i, j, true);
                    pool.setButtonLocked(i, j, false);

                    relativeLayout.addView(bt);
                } else if (i == 0 && j != 0) {
                    ScrabbleTile bt = new ScrabbleTile(this);

                    int x = View.generateViewId();

                    pool.setButtonID(i, j, x);
                    pool.setButtonValue(i, j, " ");
                    pool.setButtonEmpty(i, j, true);
                    pool.setButtonLocked(i, j, false);

                    bt.setId(x);
                    bt.setText(" ");
                    bt.addRule(RelativeLayout.RIGHT_OF, pool.getButtonID(i, j - 1));
                    bt.setMarginForTop();
                    bt.loadBonuses(i, j);

                    relativeLayout.addView(bt);
                } else if (i != 0) {
                    //} else if (i != 0 && j == 0) {
                    ScrabbleTile bt = new ScrabbleTile(this);

                    int x = View.generateViewId();

                    pool.setButtonID(i, j, x);
                    pool.setButtonValue(i, j, " ");
                    pool.setButtonEmpty(i, j, true);
                    pool.setButtonLocked(i, j, false);

                    bt.setId(x);
                    bt.setText(" ");
                    bt.addRule(RelativeLayout.BELOW, pool.getButtonID(i - 1, j));
                    bt.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    bt.setMargins();
                    bt.loadBonuses(i, j);

                    relativeLayout.addView(bt);
                } else {
                    //} else if (i == 0 && j == 0) {
                    ScrabbleTile bt = ((ScrabbleTile) findViewById(pool.getButtonID(i, j)));

                    bt.initWH();
                    bt.setLocked(false);
                    bt.setEmpty(true);
                    bt.setMarginForTop();
                    bt.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    bt.loadBonuses(i, j);
                }
            }
        }
    }

    private void addWordToStore(String word) {
        curPlayer.addPlayedWord(word);
        //playedWords.add(word);
    }

    private void loadDataWithScanner() {
        Scanner scanner = new Scanner(this.getResources().openRawResource(R.raw.words));
        while (scanner.hasNextLine()) {
            String nextline = scanner
                    .nextLine()
                    .toLowerCase()
                    .replaceAll("\n", "")
                    .replaceAll("'", "");
            String[] words = nextline.split(" ");
            for (String word : words) {
                //  trie.addWord(word);
                dic.addWord(word);
            }
        }
    }
}



