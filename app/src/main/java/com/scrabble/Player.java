package com.scrabble;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
   private String name="Player";
    private int score=0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Player(String name)
    {
        setName(name);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(String.valueOf(score));
    }
}
