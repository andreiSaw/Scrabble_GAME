package com.tools_pack;

import java.util.HashSet;

public class Dictionary {
    public HashSet<String> dic = new HashSet<String>();

    public Dictionary() {
    }

    public void addWord(String w) {
        dic.add(w);
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
}

