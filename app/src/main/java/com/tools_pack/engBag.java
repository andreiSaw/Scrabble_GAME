package com.tools_pack;

/*
https://en.wikipedia.org/wiki/Scrabble_letter_distributions#English
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EngBag {

    private String letters =
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

    public EngBag() {
        list = new ArrayList<>();
    }

    public void load() {
        String context = letters;
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

    protected void makeCount() {
        count = list.size();
    }

    public void pushLetters(String letters) {
        while (letters.length() > 0) {
            String x = "" + letters.charAt(0);
            list.add(x);
            letters = letters.substring(1);
        }
    }
}


