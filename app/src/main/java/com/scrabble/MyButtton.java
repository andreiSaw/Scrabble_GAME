package com.scrabble;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;

import java.util.Objects;

public class MyButtton extends Button

{
    private boolean lock = false;
    private boolean onPool = false;
    private boolean isEmpty;

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

    public MyButtton(Context context) {
        super(context);
        listen();
    }

    public MyButtton(Context context, AttributeSet attrs) {
        super(context, attrs);
        listen();
    }

    public MyButtton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listen();
    }

    public MyButtton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        listen();
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
        if (Objects.equals(getText().toString(), "") || Objects.equals(getText().toString(), " ")) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }
    }
}
