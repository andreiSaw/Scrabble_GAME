package com.tools_pack;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.R;

import java.util.ArrayList;
import java.util.List;

public class WordsViewAdapter extends RecyclerView.Adapter<WordsViewAdapter.ViewHolder> {
    /*
    https://habrahabr.ru/post/237101/
     */
    private List<String> records;
    private ArrayList<String> _allrecords;

    public WordsViewAdapter(List<String> records) {
        this.records = records;
    }

    public WordsViewAdapter(List<String> records, ArrayList<String> _all) {
        this(records);
        _allrecords = _all;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String _record = _allrecords.get(i);
        int iconResourceId = 0;
        viewHolder.icon.setImageResource(iconResourceId);
        viewHolder.name.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
        viewHolder.name.setTextColor(Color.WHITE);
        viewHolder.name.setText(_record);
        if (records.contains(_record)) {
            viewHolder.name.setBackgroundResource(R.color.colorLightBlueTile);
            return;
        }
        viewHolder.name.setBackgroundResource(R.color.colorLightGreenTile);
    }

    @Override
    public int getItemCount() {
        return _allrecords.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView icon;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.recyclerViewItemName);
            icon = (ImageView) itemView.findViewById(R.id.recyclerViewItemIcon);
        }
    }
}
