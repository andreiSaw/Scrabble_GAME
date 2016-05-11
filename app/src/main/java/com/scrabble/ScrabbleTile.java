package com.scrabble;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Objects;

public class ScrabbleTile extends Button {
    private static int size = 150;
    private static int WIDTH = 1080, HEIGHT = 1920;
    // 250 for 1080 is ideal
    private static int _marginTop = 250;
    final int ONEP = 1;
    final int DBLP = 2;
    final int TRPP = 3;
    final int[][] bonusGrid = {
            {DBLP, ONEP, ONEP, ONEP, ONEP, ONEP, DBLP},
            {ONEP, DBLP, TRPP, TRPP, TRPP, DBLP, ONEP},
            {ONEP, TRPP, DBLP, ONEP, DBLP, TRPP, ONEP},
            {ONEP, TRPP, ONEP, TRPP, ONEP, TRPP, ONEP},
            {ONEP, TRPP, DBLP, ONEP, DBLP, TRPP, ONEP},
            {ONEP, DBLP, TRPP, TRPP, TRPP, DBLP, ONEP},
            {DBLP, ONEP, ONEP, ONEP, ONEP, ONEP, DBLP}
    };
    // 150 for 1080 is ideal
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
    private boolean lock = false;
    private boolean onPool = false;
    private boolean isEmpty;

    public ScrabbleTile(Context context) {
        super(context);
        listen();
        initWH();
    }

    public ScrabbleTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        listen();
        initWH();
    }

    public ScrabbleTile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listen();
        initWH();
    }

    public ScrabbleTile(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        listen();
        initWH();
    }

    public static void setResolution(int w, int h) {
        WIDTH = w;
        HEIGHT = h;
        //  double coef = 0.1388888888888889;
        double coef = 0.13;
        double marginCoef = 0.23148148148148148;
        double d = (double) WIDTH * coef;
        size = (int) d;
        d = (double) WIDTH * marginCoef;
        _marginTop = (int) d;
    }

    public void loadBonuses(int i, int j) {
        int x = bonusGrid[i][j];
        switch (x) {
            case ONEP:
                this.setBackgroundResource(R.color.colorLightBlueTile);
                break;
            case DBLP:
                this.setBackgroundResource(R.color.colorLightGreenTile);
                break;
            case TRPP:
                this.setBackgroundResource(R.color.colorLightPinkTile);
                break;
        }

    }

    private void listen() {
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Objects.equals(getText().toString(), "") || Objects.equals(getText().toString(), " ")) {
                    setEmpty(true);
                } else {
                    setEmpty(false);
                }
            }
        });
    }

    protected void setMargins() {
        int _margin = 4;
        params.setMargins(_margin, _margin, _margin, _margin);
        this.setLayoutParams(params);
    }

    protected void setMarginForTop() {
        int _margin = 4;
        params.setMargins(_margin, _marginTop, _margin, _margin);
        this.setLayoutParams(params);
    }

    public void initWH() {
        params = new RelativeLayout.LayoutParams(size, size);
    }

    public void addRule(int verb, int anchor) {
        params.addRule(verb, anchor);
        this.setLayoutParams(params);
    }

    public void addRule(int verb) {
        params.addRule(verb);
        this.setLayoutParams(params);
    }

    public boolean isLocked() {
        return lock;
    }

    public void setLocked(boolean lock) {
        this.lock = lock;
    }

    public boolean isOnPool() {
        return onPool;
    }

    public void setOnPool(boolean onPool) {
        this.onPool = onPool;
    }

    public boolean isEmpty() {
        checkEmptiness();
        return isEmpty;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
    }

    private void checkEmptiness() {
        //if (Objects.equals(getText().toString(), "") || Objects.equals(getText().toString(), " ")) {
        isEmpty = Objects.equals(getText().toString(), "") || Objects.equals(getText().toString(), " ");
    }
}
