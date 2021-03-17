package com.example.grocerylist;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemViewAdaptor extends RecyclerView.Adapter<ItemViewAdaptor.ItemViewViewHolder> {

    private ArrayList<ItemStruct> Itemlist;

    private OnItemClickListner cListner;

    private OnCostClickListner costClickListner;

    private OnItemLongClickListner LclickListener;

    public interface OnItemClickListner{
        void onCheckClick(int position);

    }

    public interface  OnCostClickListner {
        void onCostClick(int position, String text);
    }



    public interface OnItemLongClickListner{
        void onLCheckClick(int position);
    }

    public void setOnCostClickListener(OnCostClickListner clister){
        costClickListner=clister;
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        cListner=listner;
    }


    public void setOnItemLongClickListner(OnItemLongClickListner llistner){
        LclickListener=llistner;
    }

    public static class ItemViewViewHolder extends RecyclerView.ViewHolder{
        public TextView ItemName;
        public TextView ItemCost;
        public TextView ItemLocation;
        public TextView ItemQty;
        public CheckBox ItemCollect;

        public ItemViewViewHolder(@NonNull View itemView,OnItemClickListner listener, OnItemLongClickListner lClickListner,OnCostClickListner costlistner,ArrayList<ItemStruct> items) {
            super(itemView);
            ItemName = itemView.findViewById(R.id.item_name);
            ItemCost= itemView.findViewById(R.id.item_price);
            ItemLocation= itemView.findViewById(R.id.item_location);
            ItemQty= itemView.findViewById(R.id.item_quantity);
            ItemCollect= itemView.findViewById(R.id.item_collected);

            ItemCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position =getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onCheckClick(position);
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

            ItemCost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(costlistner!=null){
                        int position = getAdapterPosition();
                        String Text = items.get(position).getCost();
                        if (position != RecyclerView.NO_POSITION) {
                            costlistner.onCostClick(position,Text);
                        }

                    }
                }
            });
        }
    }



    @NonNull
    @Override
    public ItemViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        ItemViewViewHolder IvH = new ItemViewViewHolder(v,cListner,LclickListener,costClickListner,Itemlist);
        return IvH;
    }


    public ItemViewAdaptor(ArrayList<ItemStruct> listItems){

        Itemlist =listItems;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewViewHolder holder, int position) {
        ItemStruct currentItem = Itemlist.get(position);
        holder.ItemName.setText(currentItem.getItemName());
        if(currentItem.getItemLocation().equals("false")||currentItem.getItemLocation().equals("False")){
            holder.ItemLocation.setText("");
        }else {
            holder.ItemLocation.setText(currentItem.getItemLocation());
        }
        holder.ItemQty.setText(currentItem.getItemQty());



        if(currentItem.getCollected().equals("true")||currentItem.getCollected().equals("True")){
            holder.ItemCollect.setChecked(true);
            holder.itemView.setBackgroundColor(Color.GREEN);


        }else{
            holder.ItemCollect.setChecked(false);
            holder.itemView.setBackgroundColor(Color.WHITE);


        }


            holder.ItemCost.setText( "Rs." + currentItem.getCost());

    }

    @Override
    public int getItemCount() {
        return Itemlist.size();
    }


}
