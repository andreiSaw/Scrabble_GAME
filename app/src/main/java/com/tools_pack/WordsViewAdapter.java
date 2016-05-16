package com.tools_pack;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.R;

import java.util.List;

public class WordsViewAdapter extends RecyclerView.Adapter<WordsViewAdapter.ViewHolder> {

    private List<String> records;

    public WordsViewAdapter(List<String> records) {
        this.records = records;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String record = records.get(i);
        int iconResourceId = 0;
        viewHolder.icon.setImageResource(iconResourceId);
        viewHolder.name.setText(record);
        if ((i + 1) % 2 == 0) {
            viewHolder.name.setBackgroundResource(R.color.colorLightBlueTile);
            return;
        }
        viewHolder.name.setBackgroundResource(R.color.colorLightGreenTile);
        viewHolder.name.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
    }

    @Override
    public int getItemCount() {
        return records.size();
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
