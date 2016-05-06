package trie_pack;

import java.util.ArrayList;
import java.util.List;

public class Gaddag extends Trie {
    private GaddagNode root;
    private String separator = ">";

    //private final int NUMBERofELEMENTS=26+1;

    public Gaddag() {
        root = new GaddagNode();
    }

    private String reverseString(String x) {
        String retString = "";
        for (int i = 0; i < x.length(); ++i) {
            retString = x.charAt(i) + retString;
        }
        return retString;
    }

    @Override
    public void addWord(String word) {
        int i = 1;
        word=word.toLowerCase();
        while (i <= word.length()) {
            String x = reverseString(word.substring(0, i)) + separator + word.substring(i);
            ++i;
            root.addWord(x);
        }
    }

    @Override
    public List getWords(String prefix) {
        //Find the node which represents the last letter of the prefix
        GaddagNode lastNode = root;
        for (int i = 0; i < prefix.length(); ++i) {
            lastNode = lastNode.getNode(prefix.charAt(i));

            //If no node matches, then no words exist, return empty list
            if (lastNode == null) {
                return new ArrayList();
            }
        }

        //Return the words which eminate from the last node
        return lastNode.getWords();
    }

}
