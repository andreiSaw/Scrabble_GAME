package com.tools_pack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private String name = "Player";
    private int score = 0;
    private List<String> playedWords;
    private int PLAYER_SKIPPED = 0;
    private String StringForButton = "S U B M I T\n";

    public Player(String name) {
        setName(name);
        StringForButton += getName();
        playedWords = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getScoreToString() {
        return getScore().toString();
    }

    public void updateScore(int addScore) {
        setScore(getScore() + addScore);
    }

    public String toString() {
        return String.format("%s has %d points", getName(), getScore());
    }

    public void addPlayedWord(String word) {
        playedWords.add(word);
    }

    public List<String> getPlayedWords() {
        return playedWords;
    }

    public int getPLAYER_SKIPPED() {
        return PLAYER_SKIPPED;
    }

    public void increment_PLAYER_SKIPPED() {
        this.PLAYER_SKIPPED++;
    }

    public void emptyPLAYER_SKIPPED() {
        this.PLAYER_SKIPPED = 0;
    }

    public String getStringForButton() {
        return StringForButton;
    }
}