package com.scrabble;

public class Board {
    int[][] masOfIDs;
    String[][] strings;
    private int POOL_SIZE;

    public Board(int size) {
        POOL_SIZE = size;
        masOfIDs = new int[POOL_SIZE][POOL_SIZE];
        strings = new String[POOL_SIZE][POOL_SIZE];
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
}
