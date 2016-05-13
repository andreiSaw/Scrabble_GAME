package com.scrabble;

/*
https://en.wikipedia.org/wiki/Scrabble_letter_distributions#English
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bag {
    String LETTERS =
            "AAAAAAAAA" +
                    "BB" +
                    "CC" +
                    "DDDD" +
                    "EEEEEEEEEEEE" +
                    "FF" +
                    "GGG" +
                    "HH" +
                    "IIIIIIIII" +
                    "J" +
                    "K" +
                    "LLLL" +
                    "MM" +
                    "NNNNNN" +
                    "OOOOOOOO" +
                    "PP" +
                    "Q" +
                    "RRRRRR" +
                    "SSSS" +
                    "TTTTTT" +
                    "UUUU" +
                    "VV" +
                    "WW" +
                    "X" +
                    "YY" +
                    "Z";
    private int count = -1;
    private List<String> list;

    public Bag() {
        list = new ArrayList<>();
        String context = LETTERS;
        String temp = "";
        while (context.length() > 0) {
            temp = context.substring(0, 1);
            list.add(temp.toLowerCase());
            context = context.substring(1);
        }
    }

    public String getLettersToString(int count) {
        String retString = "";
        int n = count;
        if (count > getCount()) {
            n = getCount();
        }
        Random rnd = new Random();
        for (int i = 0; i < n; ++i) {
            int x = rnd.nextInt(getCount());
            retString += list.get(x);
            list.remove(x);
        }
        return retString;
    }

    public int getCount() {
        makeCount();
        return count;
    }

    private void makeCount() {
        count = list.size();
    }

    protected void pushLetters(String letters) {
        while (letters.length() > 0) {
            String x = "" + letters.charAt(0);
            list.add(x);
            letters = letters.substring(1);
        }
    }

}
