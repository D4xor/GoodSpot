package com.example.goodspot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodspot.Model.ItemsMark;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private ArrayList<ItemsMark> itemsMarkArrayList;
    public static class ListViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv;
        public TextView nam,desc,typ;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.imageView);
            nam = itemView.findViewById(R.id.textView);
            desc = itemView.findViewById(R.id.textView2);
            typ = itemView.findViewById(R.id.textView3);
        }
    }

    public ListAdapter(ArrayList<ItemsMark> items){
        itemsMarkArrayList = items;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spots, parent,false);
        ListViewHolder listViewHolder = new ListViewHolder(v);
        return  listViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ItemsMark itemsMark = itemsMarkArrayList.get(position);

        holder.iv.setImageResource(itemsMark.getImage());
        holder.nam.setText(itemsMark.getName());
        holder.desc.setText(itemsMark.getDesc());
        holder.typ.setText("Catégorie : " + itemsMark.getType());

    }

    @Override
    public int getItemCount() {
        return itemsMarkArrayList.size();
    }

}
