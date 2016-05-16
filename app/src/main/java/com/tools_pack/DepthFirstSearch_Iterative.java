package com.tools_pack;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

public class DepthFirstSearch_Iterative {

    // Use a stack for the iterative DFS version
    public void dfs_iterative(ArrayList<ArrayList<Integer>> adj, int s, Vector<Integer> vector) {
        boolean[] visited = new boolean[adj.size()];
        Stack<Integer> st = new Stack<Integer>();
        st.push(s);
        while (!st.isEmpty()) {
            int v = st.pop();
            if (!visited[v]) {
                visited[v] = true;
                vector.add(v);
                // auxiliary stack to visit neighbors in the order they appear
                // in the adjacency list
                // alternatively: iterate through ArrayList in reverse order
                // but this is only to get the same output as the recursive dfs
                // otherwise, this would not be necessary
                Stack<Integer> auxStack = new Stack<>();
                for (int w : adj.get(v)) {
                    if (!visited[w]) {
                        auxStack.push(w);
                    }
                }
                while (!auxStack.isEmpty()) {
                    st.push(auxStack.pop());
                }
            }
        }
        System.out.println();
    }


    // ----------------------------------------------------------------------
    // Testing our implementation
    public boolean run(Board pool, Vector<WordToAdd> wordToAddVector) {
        Vector<Integer> vectorofcoordinates = new Vector<>();
        Board board = null;
        try {
            board = (Board) pool.clone();
            for (WordToAdd wordToAdd : wordToAddVector) {
                if (wordToAdd.getColumnEnds() == wordToAdd.getColumnStarts()) {
                    for (int i = wordToAdd.getRowStarts(); i <= wordToAdd.getRowEnds(); ++i) {
                        board.setButtonLocked(i, wordToAdd.getCurColumn(), true);
                    }
                } else {
                    for (int i = wordToAdd.getColumnStarts(); i <= wordToAdd.getColumnEnds(); ++i) {
                        board.setButtonLocked(wordToAdd.getCurRow(), i, true);
                    }
                }
            }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // Create adjacency list representation
        ArrayList<ArrayList<Integer>> adjLists = new ArrayList<ArrayList<Integer>>();
        final int n = 7 * 7;

        // Add an empty adjacency list for each vertex
        for (int v = 0; v < n; v++) {
            adjLists.add(new ArrayList<Integer>());
        }

        int start = -1;
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 7; ++j) {
                if (board.isButtonLocked(i, j)) {
                    vectorofcoordinates.add(i * 7 + j);
                    if (i != 6) {
                        if (board.isButtonLocked(i + 1, j)) {
                            adjLists.get((i * 7) + j).add((i + 1) * 7 + j);
                            adjLists.get((i + 1) * 7 + j).add(i * 7 + j);
                        }
                    }
                    if (j != 6) {
                        if (board.isButtonLocked(i, j + 1)) {
                            adjLists.get(i * 7 + j).add(i * 7 + j + 1);
                            adjLists.get(i * 7 + j + 1).add(i * 7 + j);
                        }
                    }
                    if (start == -1) {
                        start = (i) * 7 + j;
                    }
                }
            }
        }
        Vector<Integer> vector = new Vector<>();
/*

        // insert neighbors of vertex 0 into adjacency list for vertex 0
        adjLists.get(0).add(1);
        adjLists.get(0).add(2);
        adjLists.get(0).add(3);

        // insert neighbors of vertex 1 into adjacency list for vertex 1
        adjLists.get(1).add(5);
        adjLists.get(1).add(6);

        // insert neighbors of vertex 2 into adjacency list for vertex 2
        adjLists.get(2).add(4);

        // insert neighbors of vertex 3 into adjacency list for vertex 3
        adjLists.get(3).add(2);
        adjLists.get(3).add(4);

        // insert neighbors of vertex 4 into adjacency list for vertex 4
        adjLists.get(4).add(1);

        // insert neighbors of vertex 5 into adjacency list for vertex 5
        // -> nothing to do since vertex 5 has no neighbors

        // insert neighbors of vertex 6 into adjacency list for vertex 5
        adjLists.get(6).add(4);
*/
        // Print vertices in the order in which they are visited by dfs()

        dfs_iterative(adjLists, start, vector);
        return vector.containsAll(vectorofcoordinates);
    }
}