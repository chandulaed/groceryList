package com.example.grocerylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListViewAdaptor extends RecyclerView.Adapter<ListViewAdaptor.ListViewViewHolder> {

    private ArrayList<ListStruct> grocerylist;

    private OnListClickListener listListener;

    private OnItemLongClickListner LclickListener;

    public interface OnItemLongClickListner{
        void onLCheckClick(int position);
    }


    public interface  OnListClickListener{
        void onListClick(int Position);
    }



    public void setOnListClickListener(OnListClickListener listener){
        listListener=listener;
    }

    public void setOnItemLongClickListner(OnItemLongClickListner llistner){
        LclickListener=llistner;
    }

    public static class ListViewViewHolder extends RecyclerView.ViewHolder{
        public TextView ListName;
        public TextView ListCreateDate;
        public ListViewViewHolder(@NonNull View itemView,OnListClickListener listener,OnItemLongClickListner lClickListner) {
            super(itemView);
            ListName = itemView.findViewById(R.id.list_name);
            ListCreateDate =itemView.findViewById(R.id.list_create_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position =getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onListClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (lClickListner != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            lClickListner.onLCheckClick(position);
                        }

                    }
                    return true;
                }
            });

        }
    }

    @NonNull
    @Override
    public ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_list_view,parent,false);
        ListViewViewHolder LvH = new ListViewViewHolder(v,listListener,LclickListener);
        return LvH;
    }

    public ListViewAdaptor(ArrayList<ListStruct> listItems){
        grocerylist=listItems;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder holder, int position) {
        ListStruct currentList = grocerylist.get(position);
        holder.ListName.setText(currentList.getListName());
        holder.ListCreateDate.setText(currentList.getDateCreated());
    }

    @Override
    public int getItemCount() {
        return grocerylist.size();
    }



}
