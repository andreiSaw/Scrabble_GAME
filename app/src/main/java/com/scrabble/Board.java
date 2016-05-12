package com.scrabble;

import java.util.Objects;

public class Board {
    private final static int[][] bonusgrid = ScrabbleTile.bonusGrid;
    int[][] masOfIDs;
    String[][] strings;
    private int POOL_SIZE;
    private boolean[][] lockedBoard;
    private boolean[][] emptyBoard;

    public Board(int size) {
        POOL_SIZE = size;
        masOfIDs = new int[POOL_SIZE][POOL_SIZE];
        strings = new String[POOL_SIZE][POOL_SIZE];
        lockedBoard = new boolean[POOL_SIZE][POOL_SIZE];
        emptyBoard = new boolean[POOL_SIZE][POOL_SIZE];
    }

    public int getButtonID(int i, int j) {
        return masOfIDs[i][j];
    }

    public String getButtonValue(int i, int j) {
        return strings[i][j];
    }

    public void setButtonValue(int i, int j, String val) {
        strings[i][j] = val;
    }

    public void setButtonID(int i, int j, int val) {
        masOfIDs[i][j] = val;
    }

    public void setButtonLocked(int i, int j, boolean f) {
        lockedBoard[i][j] = f;
    }

    public boolean isButtonLocked(int i, int j) {
        return lockedBoard[i][j];
    }

    public boolean isButtonEmpty(int i, int j) {
        isButtonValueEmpty(i,j);
        return emptyBoard[i][j];
    }

    private void isButtonValueEmpty(int i, int j)
    {
        emptyBoard[i][j] = Objects.equals(getButtonValue(i, j), " ") || Objects.equals(getButtonValue(i, j), "");
    }

    public void setButtonEmpty(int i, int j, boolean f) {
        emptyBoard[i][j] = f;
    }

    public int getBonus(int i, int j) {
        return bonusgrid[i][j];
    }
}
