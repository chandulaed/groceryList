package com.example.grocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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
    String itemkey;
    String collected;
    private void RecyclerView(){
        itemViewRecycler =findViewById(R.id.item_view_Recycler);
        itemViewRecycler.setHasFixedSize(true);
        itemViewLManager = new LinearLayoutManager(this);
        itemViewAdaptor= new ItemViewAdaptor(itemlist);
        itemViewRecycler.setLayoutManager(itemViewLManager);
        itemViewRecycler.setAdapter(itemViewAdaptor);

        itemViewAdaptor.setOnItemClickListner(new ItemViewAdaptor.OnItemClickListner() {
            @Override
            public void onCheckClick(int position) {
                String selectitem = itemlist.get(position).getItemName();
                DatabaseReference itemref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items");
                itemref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()!=true){
                            itemref.removeEventListener(this);
                            return;
                        }else {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.child("itemName").getValue().equals(selectitem))  {
                                    itemkey=String.valueOf(snapshot1.getKey());
                                    collected=String.valueOf(snapshot1.child("collected").getValue());
                                    Toast.makeText(Grocerylist.this, itemkey, Toast.LENGTH_SHORT).show();
                                    itemref.removeEventListener(this);
                                    return;
                                }
                            }
                            return;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                new java.util.Timer().schedule(
                        new java.util.TimerTask(){
                            @Override
                            public  void run() {
                if(itemkey!=null) {
                    DatabaseReference collectref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items").child(itemkey);
                    if (collected.equals("True")) {
                        collectref.child("collected").setValue("False");
                    } else if (collected.equals("False")) {
                        collectref.child("collected").setValue("True");
                    }else{
                        itemkey=null;
                        collected=null;
                    }
                }}},3000);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share: {
                Intent Ishare =new Intent(Grocerylist.this,Share.class);
                Ishare.putExtra("listId",listID);
                startActivity(Ishare);

                return true;
            }
            case R.id.logout:
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}