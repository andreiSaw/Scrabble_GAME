package com.scrabble;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;

public class Dictionary {
    private static final String WORD_FILE = "res/raw/words.txt";
    public HashSet<String> dic = new HashSet<String>();

    public Dictionary() {
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

    public String getRequiredSize(int length) {
        for (String t : dic) {
            if (t.length() == length) {
                return t;
            }
        }
        return null;
    }
}

