package com.scrabble;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Player implements Parcelable {
    private String name = "Player";
    private int score = 0;
    private List<String> playedWords;

    protected Player(Parcel in) {
        name = in.readString();
        score = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player(String name) {
        setName(name);
        playedWords=new ArrayList<>();
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getScoreToString() {
        return getScore().toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(String.valueOf(score));
    }

    public void updateScore(int addScore) {
        setScore(getScore() + addScore);
    }

    public String toString() {
        return getName() + " " + getScore();
    }

    public void addPlayedWord(String word)
    {
        playedWords.add(word);
    }
}
