package com.scrabble;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Dictionary {
    private static final String WORD_FILE = "res/raw/words.txt";
    public HashSet<String> dic = new HashSet<String>();

    public Dictionary() {
    }

    private String rearrangeWord(String source) {
        Random rnd = new Random();
        int length = source.length();
        String messed = "";
        while (length > 0) {
            int c = rnd.nextInt(length);
            messed += source.charAt(c);
            source = source.substring(0, c) + source.substring(c+1);
            --length;
        }
        return messed;
    }

    /**
     * load the word.txt to the dic hashset
     */
    public void loadDic(InputStream is) {
        Scanner scanner = new Scanner(is);
        // e.printStackTrace();

        if (scanner != null) {
            while (scanner.hasNext()) {
                dic.add(scanner.next());
            }
        }
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * check whether the word is valid or not
     */
    public boolean isValidWord(String word) {
        return dic.contains(word);
    }

    /**
     * check whether the words are valid or not
     */
    public boolean isValidWord(HashSet<String> words) {
        for (String word : words) {
            if (!isValidWord(word)) {
                return false;
            }
        }
        return true;
    }

    public void removeWord(String word) {
        dic.remove(word);
    }

    private List getListRequiredSize(int length) {
        List<String> myList = new ArrayList<>();
        for (String t : dic) {
            if (t.length() == length) {
                myList.add(t);
            }
        }
        return myList;
    }

    public String getRequiredSize(int length) {
        List myList = getListRequiredSize(length);
        int count = myList.size();
        Random rnd = new Random();
        int index = rnd.nextInt(count);
        String indexed=(String) myList.get(index);
        return indexed;
        //return rearrangeWord(indexed);
    }
}

