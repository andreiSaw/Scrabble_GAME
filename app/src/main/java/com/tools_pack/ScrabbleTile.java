package com.tools_pack;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.tools.R;

import java.util.Objects;

public class ScrabbleTile extends Button {
    private static final int ONEP = 1;
    private static final int DBLP = 2;
    private static final int TRPP = 3;
    protected static final int[][] bonusGrid = {
            {DBLP, ONEP, ONEP, ONEP, ONEP, ONEP, DBLP},
            {ONEP, DBLP, TRPP, TRPP, TRPP, DBLP, ONEP},
            {ONEP, TRPP, DBLP, ONEP, DBLP, TRPP, ONEP},
            {ONEP, TRPP, ONEP, ONEP, ONEP, TRPP, ONEP},
            {ONEP, TRPP, DBLP, ONEP, DBLP, TRPP, ONEP},
            {ONEP, DBLP, TRPP, TRPP, TRPP, DBLP, ONEP},
            {DBLP, ONEP, ONEP, ONEP, ONEP, ONEP, DBLP}
    };
    private static final int _margin = 5;
    private static int size = 150;
    // 250 for 1080 is ideal
    private static int _marginTop = 250;
    // 150 for 1080 is ideal
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
    private boolean lock = false;
    private boolean isEmpty;

    public ScrabbleTile(Context context) {
        super(context);
        setTextSize();
        this.setTypeface(null, Typeface.BOLD);
        initWH();
    }

    public ScrabbleTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWH();
    }

    public ScrabbleTile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWH();
    }

    public ScrabbleTile(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWH();
    }

    public static void setResolution(int w, int h) {
        double x = (double) (w - 2 * 2 * 5 - 2 * 7 * _margin) / (double) 7;
        size = (int) x;
        double marginCoef = 0.23148148148148148;
        double d = (double) w * marginCoef;
        _marginTop = (int) d;

    }

    public void setTextSize() {
        this.setTextSize(20f);
    }

    public void loadBonuses(int i, int j) {
        if (i == j && j == 3) {
            this.setBackgroundResource(R.color.colorForMiddle);
            return;
        }
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

    public void setMargins() {
        params.setMargins(_margin, _margin, _margin, _margin);
        this.setLayoutParams(params);
    }

    public void setMarginForTop() {
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
