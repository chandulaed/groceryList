package com.example.grocerylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemViewAdaptor extends RecyclerView.Adapter<ItemViewAdaptor.ItemViewViewHolder> {

    private ArrayList<ItemStruct> Itemlist;
    private OnItemClickListner cListner;
    public interface OnItemClickListner{
        void onCheckClick(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        cListner=listner;
    }

    public static class ItemViewViewHolder extends RecyclerView.ViewHolder{
        public TextView ItemName;
        public EditText ItemCost;
        public TextView ItemLocation;
        public TextView ItemQty;
        public CheckBox ItemCollect;
        public ItemViewViewHolder(@NonNull View itemView,OnItemClickListner listener) {
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
        }
    }



    @NonNull
    @Override
    public ItemViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        ItemViewViewHolder IvH = new ItemViewViewHolder(v,cListner);
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
        holder.ItemCost.setText(currentItem.getCost());

        if(currentItem.getCollected().equals("true")||currentItem.getCollected().equals("True")){
            holder.ItemCollect.setChecked(true);
        }else{
            holder.ItemCollect.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return Itemlist.size();
    }


}
