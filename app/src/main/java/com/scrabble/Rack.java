package com.scrabble;

import android.widget.RelativeLayout;

public class Rack {
    int[] bottomLineIDs;
    int POOL_SIZE;

    public Rack(int pool_size) {
        POOL_SIZE = pool_size;
        bottomLineIDs = new int[POOL_SIZE];
    }
    public int getButtonID(int i)
    {
        return bottomLineIDs[i];
    }
    public void setButtonID(int i,int id)
    {
        bottomLineIDs[i]=id;
    }
}
