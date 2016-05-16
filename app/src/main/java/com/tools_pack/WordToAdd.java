package com.tools_pack;

import android.util.Pair;

import java.util.Vector;

public class WordToAdd {
    private String word;
    private int rowStarts, rowEnds, columnStarts, columnEnds, curColumn, curRow;

    private WordToAdd() {
        // private constructor
    }

    public static Builder newBuilder() {
        return new WordToAdd().new Builder();
    }

    public int getRowStarts() {
        return rowStarts;
    }

    public int getRowEnds() {
        return rowEnds;
    }

    public int getColumnStarts() {
        return columnStarts;
    }

    public int getColumnEnds() {
        return columnEnds;
    }

    public int getCurColumn() {
        return curColumn;
    }

    public int getCurRow() {
        return curRow;
    }

    public String getWord() {
        return word;
    }

    public Vector<Pair<Integer, Integer>> getCoordinates() {
        Vector<Pair<Integer, Integer>> coordinatesVector = new Vector<>();
        if (columnStarts == columnEnds) {
            for (int i = rowStarts; i <= rowEnds; ++i) {
                coordinatesVector.add(new Pair<>(i, curColumn));
            }
        } else {
            for (int i = columnStarts; i <= columnEnds; ++i) {
                coordinatesVector.add(new Pair<>(curRow, i));
            }
        }
        return coordinatesVector;
    }

    public class Builder {
        private Builder() {
            // private constructor
        }

        public WordToAdd build() {
            return WordToAdd.this;
        }

        public Builder setRowEnds(int i) {
            WordToAdd.this.rowEnds = i;
            return this;
        }

        public Builder setRowStarts(int i) {
            WordToAdd.this.rowStarts = i;
            return this;
        }

        public Builder setColumnStarts(int i) {
            WordToAdd.this.columnStarts = i;
            return this;
        }

        public Builder setColumnEnds(int i) {
            WordToAdd.this.columnEnds = i;
            return this;
        }

        public Builder setCurrentColumn(int i) {
            WordToAdd.this.curColumn = i;
            return this;
        }

        public Builder setCurrentRow(int i) {
            WordToAdd.this.curRow = i;
            return this;
        }

        public Builder setWord(String word) {
            WordToAdd.this.word = word;
            return this;
        }
    }
}
