package com.scrabble;

import java.util.Objects;
import java.util.Vector;

public class Rack {
    int[] bottomLineIDs;
    int POOL_SIZE;
    String[] values;

    public Rack(int pool_size) {
        POOL_SIZE = pool_size;
        bottomLineIDs = new int[POOL_SIZE];
        values = new String[POOL_SIZE];
    }

    public int getButtonID(int index) {
        return bottomLineIDs[index];
    }

    public void setButtonID(int index, int id) {
        bottomLineIDs[index] = id;
    }

    public void setButtonValue(int index, String val) {
        values[index] = val;
    }

    public String getButtonValue(int index) {
        return values[index];
    }

    public String popButtonValue(int index) {
        String retString = values[index];
        values[index] = "";
        return retString;
    }

    public boolean isEmpty(int index) {
        return Objects.equals(getButtonValue(index), "");
    }


    public Vector<Integer> pushButtonValue(String val) {
        int i = 0;
        Vector<Integer> unlockedTilesVector = new Vector<>();
        while (val.length() > 0 && i != POOL_SIZE) {
            if (isEmpty(i)) {
                values[i] = val;
                unlockedTilesVector.add(getButtonID(i));
                val = val.substring(1);
            } else {
                i++;
            }
        }
        return unlockedTilesVector;
    }

    protected String getButtonValueById(int id) {
        for (int i = 0; i < POOL_SIZE; i++) {
            if (getButtonID(i) == id) {
                return getButtonValue(i);
            }
        }
        return "";
    }

    public String toString() {
        String s = "";
        for (String c : values) {
            s += c;
        }
        return s;
    }

    public void emptyRack() {
        values = new String[POOL_SIZE];
    }
}

