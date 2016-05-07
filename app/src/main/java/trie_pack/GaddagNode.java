package trie_pack;

import java.util.ArrayList;
import java.util.List;

public class GaddagNode extends TrieNode {
    private GaddagNode parent;
    private GaddagNode[] children;
    private char character;
    private boolean isEOW;
   // private boolean isLeaf;
    private boolean isBreak=false;

    @Override
    protected void addWord(String word) {

      //  isLeaf = false;
        int charPos;
        if (word.charAt(0) == '>') {
            charPos = 26;
        } else {
            charPos = word.charAt(0) - 'a';
        }

        if (children[charPos] == null) {
            children[charPos] = new GaddagNode(word.charAt(0));
            children[charPos].parent = this;
            children[charPos].isBreak = true;
        }

        if (word.length() > 1) {
            children[charPos].addWord(word.substring(1));
        } else {
            children[charPos].isEOW = true;
        }
    }

    public GaddagNode() {
        int NUMBERofELEMENTS = 26 + 1;
        children = new GaddagNode[NUMBERofELEMENTS];
       // isLeaf = true;
        isEOW = false;
    }

    public GaddagNode(char character) {
        this();
        this.character = character;
    }

    @Override
    protected GaddagNode getNode(char c) {
        return children[c - 'a'];
    }

    @Override
    protected List getWords() {
        //Create a list to return
        List<String> list = new ArrayList<>();

        //If this node represents a word, add it
        if (isEOW) {
            list.add(toString());
        }

        //If any children
       // if (!isLeaf)
        {
            //Add any words belonging to any children
            for (GaddagNode aChildren : children) {
                if (aChildren != null) {
                    list.addAll(aChildren.getWords());
                }
            }
        }

        return list;
    }

    public boolean isLeaf() {
        return true;
    }
    public boolean isBreak()
    {
        return character == '>' && isBreak;
    }
}