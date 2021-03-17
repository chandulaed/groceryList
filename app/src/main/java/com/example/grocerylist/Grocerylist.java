package com.example.grocerylist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
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
import java.util.Collections;

public class Grocerylist extends AppCompatActivity {

    private String listID,listName;
    private TextView TotalCost;
    private androidx.appcompat.widget.Toolbar toolbar;
    private float totalcost = 0;
    private ArrayList<String> itemkey = new ArrayList<>();
    private DatabaseReference listref;
    private ArrayList<ItemStruct> itemlist=new ArrayList<>();
    private RecyclerView itemViewRecycler;
    private ItemViewAdaptor itemViewAdaptor;
    private RecyclerView.LayoutManager itemViewLManager;

    void changeCost(){
        TotalCost.setText(String.valueOf("Rs. " + totalcost));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocerylist);
        listID = getIntent().getStringExtra("List_ID");
        listName = getIntent().getStringExtra("List_Name");
        TotalCost =findViewById(R.id.List_total_cost);
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
                    totalcost=0;
                    itemViewAdaptor.notifyDataSetChanged();
                    return;
                }else {
                    ItemStruct templist;
                    itemlist.clear();
                    totalcost=0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        itemkey.add(String.valueOf(snapshot1.getKey()));
                        templist = snapshot1.getValue(ItemStruct.class);
                        totalcost = totalcost + Float.parseFloat(templist.getCost());
                        itemlist.add(templist);
                    }
                    Toast.makeText(Grocerylist.this, "List Update Completed", Toast.LENGTH_SHORT).show();
                    itemViewAdaptor.notifyDataSetChanged();
                    changeCost();
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        RecyclerView();
    }

    String collected=null;
    private void RecyclerView(){
        Collections.sort(itemlist);
        itemViewRecycler =findViewById(R.id.item_view_Recycler);
        itemViewRecycler.setHasFixedSize(true);
        itemViewLManager = new LinearLayoutManager(this);
        itemViewAdaptor= new ItemViewAdaptor(itemlist);
        itemViewRecycler.setLayoutManager(itemViewLManager);
        itemViewRecycler.setAdapter(itemViewAdaptor);

        itemViewAdaptor.setOnItemClickListner(new ItemViewAdaptor.OnItemClickListner() {
            @Override
            public void onCheckClick(int position) {
                if(itemkey.size()>0) {
                    DatabaseReference collectref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items").child(itemkey.get(position).toString());
                    if (itemlist.get(position).getCollected().equals("True")) {
                        collectref.child("collected").setValue("False");
                    } else if (itemlist.get(position).getCollected().equals("False")) {
                        collectref.child("collected").setValue("True");
                    }else{
                        collected=null;
                    }
            }
        }});

        itemViewAdaptor.setOnItemLongClickListner(new ItemViewAdaptor.OnItemLongClickListner(){
            @Override
            public void onLCheckClick(int position) {
                String edititem = itemlist.get(position).getItemName();
                Toast.makeText(Grocerylist.this, edititem+" list Selected", Toast.LENGTH_SHORT).show();
                Intent e_intent = new Intent(getApplicationContext(), MeditItem.class);
                e_intent.putExtra("List_ID",listID);
                e_intent.putExtra("Item_Name",itemlist.get(position).getItemName());
                e_intent.putExtra("Item_Cost",itemlist.get(position).getCost());
                e_intent.putExtra("Item_Loc",itemlist.get(position).getItemLocation());
                e_intent.putExtra("Item_Qty",itemlist.get(position).getItemQty());
                e_intent.putExtra("List_Name",listName);
                startActivity(e_intent);
            }
        });

        itemViewAdaptor.setOnCostClickListener(new ItemViewAdaptor.OnCostClickListner() {
            @Override
            public void onCostClick(int position, String text) {
                if(itemlist.get(position).getCollected().equals("True")||itemlist.get(position).getCollected().equals("true")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Grocerylist.this);
                    builder.setTitle("Edit Cost");

                    final EditText input = new EditText(Grocerylist.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    input.setText(text);
                    builder.setView(input);


                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = input.getText().toString().replaceAll("^0+(?!$)", "");
                            DatabaseReference costref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items").child(itemkey.get(position).toString());
                            if(text.equals("")){
                                costref.child("cost").setValue("0");
                            }else{
                                costref.child("cost").setValue(text);
                            }


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }


                else
                    Toast.makeText(Grocerylist.this, "Please collect the item first", Toast.LENGTH_SHORT).show();
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
            case R.id.action_manula_add_item:
            {
                Intent Imadd = new Intent(Grocerylist.this,MaddItem.class);
                Imadd.putExtra("listid",listID);
                Imadd.putExtra("listName",listName);
                startActivity(Imadd);
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}