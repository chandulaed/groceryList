package com.example.grocerylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SpeechAdaptor extends RecyclerView.Adapter {
    private ArrayList<TranscriptItem> TList;
    public SpeechAdaptor(ArrayList<TranscriptItem> TItemList){
        this.TList=TItemList;
    }

    @Override
    public int getItemViewType(int position) {
        if(TList.get(position).from.equals("U")){
            return 1;
        }else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 1) {
            view = layoutInflater.inflate(R.layout.speed_trans_item, parent, false);
            return (new ViewHolder1(view));
        } else{
            view = layoutInflater.inflate(R.layout.ai_trans_item, parent, false);
        return (new ViewHolder2(view));
    }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(TList.get(position).from.equals("U")){
            ViewHolder1 viewHolder1=(ViewHolder1) holder;
            viewHolder1.utext.setText(TList.get(position).message);
        }else{
            ViewHolder2 viewHolder2=(ViewHolder2) holder;
            viewHolder2.aitext.setText(TList.get(position).message);
        }
    }

    @Override
    public int getItemCount() {
        return TList.size();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder{
        TextView utext;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            utext=itemView.findViewById(R.id.sp_item);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder{
        TextView aitext;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            aitext=itemView.findViewById(R.id.ai_item);
        }
    }
}
