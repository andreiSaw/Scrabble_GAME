package com.scrabble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private String name = "Player";
    private int score = 0;
    private List<String> playedWords;

    public Player(String name) {
        setName(name);
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
        return getName() + " " + getScore();
    }

    public void addPlayedWord(String word) {
        playedWords.add(word);
    }

    public List getPlayedWords() {
        return playedWords;
    }

}
