package com.activity_pack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.tools.R;
import com.tools_pack.Board;
import com.tools_pack.DepthFirstSearch_Iterative;
import com.tools_pack.Dictionary;
import com.tools_pack.EngBag;
import com.tools_pack.Player;
import com.tools_pack.Rack;
import com.tools_pack.ScrabbleTile;
import com.tools_pack.WordToAdd;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int SHAKE_THRESHOLD = 600;
    private final int POOL_SIZE = 7;
    protected Player curPlayer;
    boolean fisetthread = false;
    Thread thread;
    Button sbmButton;
    private Player p1, p2;
    private Rack rack;
    private Dictionary dic = new Dictionary();
    private Board pool;
    private String _letterBuf = "";
    private Vector<Pair<String, Integer>> vectorOfLettersWorth;
    private EngBag engBag;
    private Drawer.Result drawerResult = null;
    private ArrayList<Integer> arrayListOfScoredPoints = new ArrayList<>();
    private ArrayList<String> arrayListOfPlayedWords = new ArrayList<>();
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
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        // Обработка клика
        public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
            if (drawerItem instanceof Nameable) {
                String str = ((Nameable) drawerItem).getName();
                if (str == null) {
                    str = MainActivity.this.getString(((Nameable) drawerItem).getNameRes());
                }
                if (str.contains(p1.getName())) {
                    str = p1.toString();
                } else if (str.contains(p2.getName())) {
                    str = p2.toString();
                }
                if (!Objects.equals(str, "Pass") && !Objects.equals(str, "Change rack")) {
                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                }
                Intent intent;
                switch (str) {
                    case "2-letters words":
                        intent = new Intent(MainActivity.this, TwoLettersWordsListActivity.class);
                        startActivity(intent);
                        break;
                    case "About":
                        intent = new Intent(MainActivity.this, AboutActivity.class);
                        MainActivity.this.startActivity(intent);
                        break;
                    case "Share":
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.StringForShare));
                        startActivity(Intent.createChooser(share, getString(R.string.SharePost)));
                        break;
                    case "New game":
                        reloadGame();
                        break;
                    case "Pass":
                        if (pool.isUnlockedButtonOnPool()) {
                            ShakeToasting();
                        } else {
                            if (p1.getPLAYER_SKIPPED() == 1 && p2.getPLAYER_SKIPPED() == 2 ||
                                    p1.getPLAYER_SKIPPED() == 2 && p2.getPLAYER_SKIPPED() == 1) {
                                showWindDialog();
                                reloadGame();
                            } else {
                                curPlayer.increment_PLAYER_SKIPPED();
                                changeCurrentPlayer();
                            }
                        }
                        break;
                    case "Change rack":
                        engBag.pushLetters(rack.toString() + _letterBuf);
                        _letterBuf = "";
                        if (pool.isUnlockedButtonOnPool()) {
                            ShakeToasting();
                        } else {
                            rack.emptyRack();
                            forcedFillRack();
                            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Played words":
                        Intent intent1 = new Intent(MainActivity.this, WordsViewActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putStringArrayList("Words", arrayListOfPlayedWords);

                        bundle.putSerializable("Player1", p1);

                        bundle.putIntegerArrayList("Points", arrayListOfScoredPoints);

                        intent1.putExtras(bundle);

                        MainActivity.this.startActivity(intent1);
                        break;
                    case "Help":
                        Intent intent2 = new Intent(MainActivity.this, HelpActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
            }
        }
    };
    private boolean flagIfWordPlayed = false;
    private Button.OnClickListener submitButtonListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!fisetthread) {
                try {
                    thread.join();
                    fisetthread = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (pool.isButtonEmpty(POOL_SIZE / 2, POOL_SIZE / 2)) {
                Toast.makeText(MainActivity.this, R.string.toast_middle, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!_letterBuf.equals("")) {
                TilePutingDowmnToasting();
                return;
            }

            Vector<WordToAdd> wordToAddVector = new Vector<>();
            Vector<Integer> cols = new Vector<>(), rows = new Vector<>();
            ScrabbleTile x;
            String y;
            String word = "";
            int curColumn = 0, curRow = 0;
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

            if (rows.isEmpty() && cols.isEmpty() || !Objects.equals(_letterBuf, "")) {
                SubmitToasting();
                return;
            }
            //TODO: anchors to prevent long time
            //TODO: SIMPLIFY


            for (int i : cols) {
                //look over each column
                // downward

                curRow = 0;
                curColumn = i;

                columnEnds = 0;
                columnStarts = 0;
                rowEnds = 0;
                rowStarts = 0;
                boolean flag = false;
                while (!flag) {

                    y = pool.getButtonValue(curRow, curColumn);
                    //find where word starts
                    while (curRow != POOL_SIZE) {
                        y = pool.getButtonValue(curRow, curColumn);
                        if (Objects.equals(y, " ") || Objects.equals(y, "")) {
                            ++curRow;
                            //continue;
                        } else {
                            rowStarts = curRow;
                            break;
                        }
                    }//while

                    if (curRow == POOL_SIZE) {
                        flag = true;
                    }

                    //find where word ends
                    while (!Objects.equals(y, " ") && curRow != POOL_SIZE) {
                        word += y;
                        ++curRow;
                        if (curRow == POOL_SIZE) {
                            flag = true;
                            break;
                        }
                        y = pool.getButtonValue(curRow, curColumn);
                    }//while
                    rowEnds = curRow - 1;

                    //checking
                    if (word.length() > 1 && dic.isValidWord(word)) {
                        wordToAddVector.add(WordToAdd.newBuilder()
                                .setWord(word)
                                .setColumnEnds(curColumn).setColumnStarts(curColumn)
                                .setRowEnds(rowEnds).setRowStarts(rowStarts)
                                .setCurrentColumn(curColumn).build());
                    }

                    word = "";
                }//while flag
            }//for i


            for (int i : rows) {
                //look over each row
                //rightward

                curRow = i;
                curColumn = 0;

                columnEnds = 0;
                columnStarts = 0;
                rowEnds = 0;
                rowStarts = 0;
                boolean flag = false;
                while (!flag) {
                    y = pool.getButtonValue(curRow, curColumn);
                    //find where word starts
                    while (curColumn != POOL_SIZE) {
                        y = pool.getButtonValue(curRow, curColumn);
                        if (Objects.equals(y, " ") || Objects.equals(y, "")) {
                            ++curColumn;
                            //continue;
                        } else {
                            columnStarts = curColumn;
                            break;
                        }
                    }//while

                    if (curColumn == POOL_SIZE) {
                        flag = true;
                    }

                    //find where word ends
                    while (!Objects.equals(y, " ") && curColumn != POOL_SIZE) {
                        word += y;
                        ++curColumn;
                        if (curColumn == POOL_SIZE) {
                            flag = true;
                            break;
                        }
                        y = pool.getButtonValue(curRow, curColumn);
                    }//while
                    columnEnds = curColumn - 1;

                    //checking
                    if (word.length() > 1 && dic.isValidWord(word)) {
                        wordToAddVector.add(WordToAdd.newBuilder()
                                .setWord(word)
                                .setColumnEnds(columnEnds).setColumnStarts(columnStarts)
                                .setRowEnds(curRow).setRowStarts(curRow)
                                .setCurrentRow(curRow).build());
                    }
                    word = "";
                }//while flag
            }//for i

            DepthFirstSearch_Iterative deepSearch;
            //todo ты смотришь что граф свзяный, но не смотришь буква-то была хотя бы одна на поле до этого
            //if there are words to add then add
            if (!wordToAddVector.isEmpty()) {
                deepSearch = new DepthFirstSearch_Iterative();
                //if graph is svyaznyi
                if (deepSearch.run(pool, wordToAddVector)) {
                    for (WordToAdd w : wordToAddVector) {
                        submitWord(w);
                    }
                    //if nor 1 one word played - turn is stays over current player
                    if (!flagIfWordPlayed) {
                        SubmitToasting();
                    } else {
                        if (pool.isUnlockedButtonOnPool()) {
                            Toast.makeText(MainActivity.this, R.string.toast_shake, Toast.LENGTH_SHORT).show();
                        } else {
                            changeCurrentPlayer();
                            fillRack();//ask new word to rack
                            flagIfWordPlayed = false;
                        }
                    }
                } else {
                    NotConnectedToasting();
                }
            } else {
                SubmitToasting();
            }
        }
    };

    private void TilePutingDowmnToasting() {
        Toast.makeText(MainActivity.this, R.string.toast_tiledown, Toast.LENGTH_SHORT).show();
    }

    private void submitWordLeftToRight(WordToAdd wd) {
        ScrabbleTile x;
        int jj = wd.getColumnStarts() - 1;
        do {
            ++jj;
            x = (ScrabbleTile) findViewById(pool.getButtonID(wd.getCurRow(), jj));
            x.setLocked(true);
            pool.setButtonLocked(wd.getCurRow(), jj, true);
        }
        while (jj != wd.getColumnEnds());
        dic.removeWord(wd.getWord());
        arrayListOfScoredPoints.add(getWordReward(wd.getWord(),
                wd.getRowStarts(), wd.getColumnStarts(),
                wd.getRowEnds(), wd.getColumnEnds()));
        curPlayer.updateScore(arrayListOfScoredPoints.get(arrayListOfScoredPoints.size() - 1));
        curPlayer.emptyPLAYER_SKIPPED();
    }

    private void submitWordUpsideDown(WordToAdd wd) {

        ScrabbleTile x;
        int ii = wd.getRowStarts() - 1;

        do {
            ++ii;
            x = (ScrabbleTile) findViewById(pool.getButtonID(ii, wd.getCurColumn()));
            x.setLocked(true);
            pool.setButtonLocked(ii, wd.getCurColumn(), true);
        }
        while (ii != wd.getRowEnds());
        //delete word
        dic.removeWord(wd.getWord());
        arrayListOfScoredPoints.add(getWordReward(wd.getWord(),
                wd.getRowStarts(), wd.getColumnStarts(),
                wd.getRowEnds(), wd.getColumnEnds()));
        curPlayer.updateScore(arrayListOfScoredPoints.get(arrayListOfScoredPoints.size() - 1));
        curPlayer.emptyPLAYER_SKIPPED();
    }

    private void submitWord(WordToAdd wordToAdd) {

        if (wordToAdd.getColumnEnds() == wordToAdd.getColumnStarts()) {
            submitWordUpsideDown(wordToAdd);
        } else {
            submitWordLeftToRight(wordToAdd);
        }
        //if one word played -raise flag
        flagIfWordPlayed = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        loadGameResolution();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadDataWithScanner();
            }
        });
        thread.start();

        Toast.makeText(MainActivity.this, R.string.toast_loading, Toast.LENGTH_SHORT).show();

        loadDistribution();
        newGame();
        loadSubmitButton();
    }

    private void ShakeToasting() {
        Toast.makeText(MainActivity.this, R.string.toast_shake, Toast.LENGTH_SHORT).show();
    }

    private void NotConnectedToasting() {
        Toast.makeText(MainActivity.this, R.string.toast_connected, Toast.LENGTH_SHORT).show();
    }

    private void SubmitToasting() {
        Toast.makeText(MainActivity.this, R.string.toast_submit, Toast.LENGTH_SHORT).show();
    }

    private void reloadGame() {
        loadWordsFromPlaersToDic();
        arrayListOfPlayedWords = new ArrayList<>();
        arrayListOfScoredPoints = new ArrayList<>();
        newGame();
        sbmButton.setText(curPlayer.getStringForButton());
    }

    private void newGame() {
        loadPlayers();
        loadNavD();
        loadPool();
        loadTable();
        loadRack();
        fisrtFillingRack();
        loadListeners();
    }

    private void loadWordsFromPlaersToDic() {
        List<String> myList;
        myList = p1.getPlayedWords();
        for (String c : myList) {
            dic.addWord(c);
        }
        myList = p2.getPlayedWords();
        for (String c : myList) {
            dic.addWord(c);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
                        new PrimaryDrawerItem().withName(getString(R.string.drawer_item_newgame)).withIcon(FontAwesome.Icon.faw_play_circle).withIdentifier(3),
                        new PrimaryDrawerItem().withName(getString(R.string.drawer_item_pass)).withIcon(FontAwesome.Icon.faw_arrow_circle_o_right),
                        new PrimaryDrawerItem().withName(getString(R.string.drawer_item_changerack)).withIcon(FontAwesome.Icon.faw_arrow_circle_o_down),
                        new PrimaryDrawerItem().withName(getString(R.string.drawer_item_playedwords)).withIcon(FontAwesome.Icon.faw_file_text),
                        new PrimaryDrawerItem().withIcon(FontAwesome.Icon.faw_key).withName(getString(R.string.drawer_item_2word)),
                        new PrimaryDrawerItem().withIcon(FontAwesome.Icon.faw_archive).withName(getString(R.string.drawer_item_help)),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_question),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_share).withIcon(FontAwesome.Icon.faw_paper_plane)).withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        try {
                            InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                        } catch (NullPointerException exception) {
                            Log.d("test", exception.getMessage());
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(drawerItemClickListener)
                .build();

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerListenernew);
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
        return true;
    }

    // Заглушка, работа с меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

    private void loadSubmitButton() {
        int _margin = 5;
        //TODO нормальная кнопка
        sbmButton = new Button(this);
        sbmButton.setText(R.string.StringForSumbitButton);
        sbmButton.setBackgroundResource(R.color.colorForMiddle);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ABOVE, rack.getButtonID(POOL_SIZE - 1));
        params.addRule(RelativeLayout.BELOW, pool.getButtonID(POOL_SIZE - 1, POOL_SIZE - 1));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        params.setMargins(_margin, _margin, _margin, _margin);
        sbmButton.setTextSize(20f);
        sbmButton.setLayoutParams(params);
        sbmButton.setId(View.generateViewId());

        sbmButton.setOnClickListener(submitButtonListener);

        sbmButton.setText(curPlayer.getStringForButton());

        relativeLayout.addView(sbmButton);
    }

    private void loadPlayers() {
        p1 = new Player("Player1");
        p2 = new Player("Player2");
        curPlayer = p1;
    }

    private void loadPool() {
        pool = new Board(POOL_SIZE);
        rack = new Rack(POOL_SIZE);
        engBag = new EngBag();
        engBag.load();
    }

    private void changeCurrentPlayer() {
        if (curPlayer.equals(p1)) {
            curPlayer = p2;
        } else {
            curPlayer = p1;
        }
        Toast.makeText(MainActivity.this, String.format("Now is %s's turn", curPlayer.getName()), Toast.LENGTH_SHORT).show();
        sbmButton.setText(curPlayer.getStringForButton());
    }

    private int getWordReward(String word, int istarts, int jstarts, int iends, int jends) {
        addWordToStore(word);
        int score = 0;
        boolean flag;
        flag = istarts == iends;
        while (word.length() > 0) {
            char firstLetterInAlphabet = 'a';
            int x = word.charAt(0) - firstLetterInAlphabet;
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
        // https://ru.wikipedia.org/wiki/Скрэббл
        //
        vectorOfLettersWorth = new Vector<>();
        DataInputStream dataInputStream;
        String text;
        String letter;
        Integer m;
        try {
            dataInputStream = new DataInputStream(getResources().openRawResource(R.raw.eng_distribution));
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

        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
                final int x1 = i;
                final int y1 = j;
                final ScrabbleTile button = (ScrabbleTile) findViewById(pool.getButtonID(i, j));
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!Objects.equals(_letterBuf, "") && button.isEmpty()) {
                            button.setText(_letterBuf);
                            pool.setButtonValue(x1, y1, _letterBuf);
                            _letterBuf = "";
                            pool.setButtonEmpty(x1, y1, false);
                        } else if (!button.isEmpty() && !button.isLocked() && Objects.equals(_letterBuf, "")) {
                            _letterBuf = button.getText().toString();
                            button.setText(" ");
                            pool.setButtonValue(x1, y1, " ");
                            pool.setButtonEmpty(x1, y1, true);
                        }
                    }
                });
            }
        }
        for (int i = 0; i < POOL_SIZE; ++i) {
            final ScrabbleTile button = (ScrabbleTile) findViewById(rack.getButtonID(i));
            final int x1 = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Objects.equals(_letterBuf, "") && !button.isEmpty()) {
                        _letterBuf = button.getText().toString();
                        rack.setButtonValue(x1, "");
                        button.setText(" ");
                    } else if (!Objects.equals(_letterBuf, "") && button.isEmpty()) {
                        button.setText(_letterBuf);
                        rack.setButtonValue(x1, _letterBuf);
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
        if (engBag.getCount() > 0) {

            String letters =
                    engBag.getLettersToString(countRack());
            fillRack(letters);
        } else {
            showWindDialog();
        }
    }

    private void showWindDialog() {
        /*
        http://developer.alexanderklimov.ru/android/alertdialog.php
         */
        String s;
        String result = "";
        if (p1.getScore() > p2.getScore()) {
            result = p1.getName() + " wins!";
        } else if (p2.getScore() > p1.getScore()) {
            result = p2.getName() + " wins!";
        } else {
            result = " It's Draw!";
        }
        s = p1.toString() + "\n" + p2.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Congratulations! " + result)
                .setMessage(s)
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void forcedFillRack() {
        int id;
        String text = engBag.getLettersToString(POOL_SIZE);
        for (int i = 0; i < POOL_SIZE; ++i) {
            id = rack.getButtonID(i);
            ScrabbleTile btn = (ScrabbleTile) findViewById(id);
            String string = "" + text.charAt(0);
            btn.setText(string);
            rack.setButtonValue(i, string);
            text = text.substring(1);
        }
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
        // String d = dic.getRequiredSize(7);
        String d = "firefox";
        fillRack(d);
    }

    private void loadTable() {

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);

        pool.setButtonID(0, 0, R.id.veryFirstButton);
        pool.setButtonValue(0, 0, " ");
        pool.setButtonEmpty(0, 0, true);
        pool.setButtonLocked(0, 0, false);

//razmetka stranicy
        for (int i = 0; i < POOL_SIZE; ++i) {
            for (int j = 0; j < POOL_SIZE; ++j) {
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
                    bt.setTextSize();
                    bt.setMarginForTop();
                    bt.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    bt.loadBonuses(i, j);
                    bt.setText("");

                    pool.setButtonValue(i, j, "");
                    pool.setButtonEmpty(i, j, true);
                    pool.setButtonLocked(i, j, false);
                }
            }
        }
    }

    private void addWordToStore(String word) {
        curPlayer.addPlayedWord(word);
        arrayListOfPlayedWords.add(word);
    }

    private void loadDataWithScanner() {
        /*
        https://github.com/dwyl/english-words
         */
        Scanner scanner = new Scanner(this.getResources().openRawResource(R.raw.words));
        while (scanner.hasNextLine()) {
            String nextline = scanner
                    .nextLine()
                    .toLowerCase()
                    .replaceAll("\n", "")
                    .replaceAll("'", "");
            String[] words = nextline.split(" ");
            for (String word : words) {
                dic.addWord(word);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Vector unlockedTilesVector = pool.getUnclockedTiles();
                    if (!unlockedTilesVector.isEmpty()) {
                        if (!Objects.equals(_letterBuf, "")) {
                            TilePutingDowmnToasting();
                            return;
                        }
                        unlockedTilesToRack();
                        if (flagIfWordPlayed) {
                            changeCurrentPlayer();
                            fillRack();
                            flagIfWordPlayed = false;
                        }
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void vibrate(long duration) {
        Vibrator vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibs.vibrate(duration);
    }

    private void unlockedTilesToRack() {
        Vector unlockedTilesVector = pool.getUnclockedTiles();
        if (!unlockedTilesVector.isEmpty()) {
            int i = 0;
            while (i < unlockedTilesVector.size()) {
                ScrabbleTile scrabbleTile = (ScrabbleTile) findViewById((Integer) unlockedTilesVector.get(i));
                String _letter;
                _letter = "" + scrabbleTile.getText();
                scrabbleTile.setEmpty(true);
                scrabbleTile.setText(" ");
                pool.setButtonValueById(scrabbleTile.getId(), " ");
                Vector unlockedTiles = rack.pushButtonValue(_letter);
                int j = 0;
                while (j < unlockedTiles.size()) {
                    ScrabbleTile scrabbleTile1 = (ScrabbleTile) findViewById((Integer) unlockedTiles.get(j));
                    scrabbleTile1.setText(rack.getButtonValueById(scrabbleTile1.getId()));
                    ++j;
                }
                ++i;
            }
            vibrate(300L);
        }
    }
}



