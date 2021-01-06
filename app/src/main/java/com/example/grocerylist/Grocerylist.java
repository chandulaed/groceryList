package com.example.grocerylist;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Grocerylist extends AppCompatActivity {

    private String listID,listName;
    private TextView TotalCost;
    private androidx.appcompat.widget.Toolbar toolbar;
    private DatabaseReference listref;
    private ArrayList<ItemStruct> itemlist=new ArrayList<>();
    private RecyclerView itemViewRecycler;
    private ItemViewAdaptor itemViewAdaptor;
    private RecyclerView.LayoutManager itemViewLManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocerylist);
        listID = getIntent().getStringExtra("List_ID");
        listName = getIntent().getStringExtra("List_Name");
        TotalCost =findViewById(R.id.List_total_cost);
        TotalCost.setText(listID);
        toolbar = findViewById(R.id.list_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(listName);

        listref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items");
        listref.keepSynced(true);
        listref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(Grocerylist.this, "No Items available", Toast.LENGTH_SHORT).show();
                    itemlist.clear();
                    itemViewAdaptor.notifyDataSetChanged();
                    return;
                }else {
                    ItemStruct templist;
                    itemlist.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        templist = snapshot1.getValue(ItemStruct.class);
                        itemlist.add(templist);
                    }
                    Toast.makeText(Grocerylist.this, "List Update Completed", Toast.LENGTH_SHORT).show();
                    itemViewAdaptor.notifyDataSetChanged();
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        RecyclerView();
    }

    private void RecyclerView(){
        itemViewRecycler =findViewById(R.id.item_view_Recycler);
        itemViewRecycler.setHasFixedSize(true);
        itemViewLManager = new LinearLayoutManager(this);
        itemViewAdaptor= new ItemViewAdaptor(itemlist);
        itemViewRecycler.setLayoutManager(itemViewLManager);
        itemViewRecycler.setAdapter(itemViewAdaptor);

    }
}