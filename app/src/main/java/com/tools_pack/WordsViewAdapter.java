package com.tools_pack;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tools.R;

import java.util.ArrayList;
import java.util.List;

public class WordsViewAdapter extends RecyclerView.Adapter<WordsViewAdapter.ViewHolder> {
    ArrayList<Integer> _scores;
    /*
    https://habrahabr.ru/post/237101/
     */
    private List<String> records;
    private ArrayList<String> _allrecords;

    public WordsViewAdapter(List<String> records) {
        this.records = records;
    }

    public WordsViewAdapter(List<String> records, ArrayList<String> _all, ArrayList<Integer> scores) {
        this(records);
        _allrecords = _all;
        _scores = scores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String _record = _allrecords.get(i);
        viewHolder.name.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
        viewHolder.name.setTextColor(Color.WHITE);
        String text;

        if (records.contains(_record)) {
            viewHolder.name.setBackgroundResource(R.color.colorLightBlueTile);
            text = String.format("Player1 gets %d points. %s", _scores.get(i), _record.toUpperCase());
        } else {
            text = String.format("Player2 gets %d points. %s", _scores.get(i), _record.toUpperCase());
            viewHolder.name.setBackgroundResource(R.color.colorLightGreenTile);
        }
        viewHolder.name.setText(text);

    }

    @Override
    public int getItemCount() {
        return _allrecords.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.recyclerViewItemName);
        }
    }
}
