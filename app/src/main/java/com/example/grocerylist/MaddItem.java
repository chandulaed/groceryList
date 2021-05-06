package com.example.grocerylist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.grocerylist.Tokenizer.collective;

public class MaddItem extends AppCompatActivity {

    private String listID,listName;
    private TextView listname;
    private EditText itemname,itemqty,itemloc;
    private Button additem;
    NewItem newItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madd_item);

        listID = getIntent().getStringExtra("listid");
        listName = getIntent().getStringExtra("listName");


        listname = findViewById(R.id.add_list_name);
        listname.setText(listName);
        additem=findViewById(R.id.btn_add_item);
        itemname =findViewById(R.id.add_item_name);
        itemqty =findViewById(R.id.add_item_quantity);
        itemloc =findViewById(R.id.add_item_location);

        AlertDialog.Builder builder = new AlertDialog.Builder(MaddItem.this);

        builder.setMessage(itemname.getText().toString()+" is already exist do you want to edit it ?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent e_intent = new Intent(getApplicationContext(), MeditItem.class);
                e_intent.putExtra("List_ID",listID);
                e_intent.putExtra("Item_Name",newItem.getCurrentItemName());
                e_intent.putExtra("Item_Loc",newItem.getCurrentItemLoc());
                e_intent.putExtra("Item_Qty",newItem.getCurrentItemQuantity());
                e_intent.putExtra("List_Name",listName);
                startActivity(e_intent);
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItem = new NewItem();
                newItem.setListID(listID);
                newItem.setItemName(itemname.getText().toString().trim());
                newItem.setItemQty(itemqty.getText().toString().trim());
                newItem.setItemLoc(itemloc.getText().toString().trim());

                if(itemname.getText().toString().trim().length()!=0){
                    if(collective(itemqty.getText().toString().trim())|| itemqty.getText().toString().trim().matches("\\d+(?:\\.\\d+)?"))
                    {
                newItem.searchitem(getApplicationContext());
                new java.util.Timer().schedule(
                        new java.util.TimerTask(){
                            @Override
                            public  void run() {
                                if(newItem.isItemavailable()==true&&newItem.isTaskCompleted()==true&&newItem.isNoSnapshot()==false) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.show();
                                        }
                                    });
                                }else if(newItem.isTaskCompleted()==false){
                                    Toast.makeText(MaddItem.this, "Sorry can't connect to the database", Toast.LENGTH_SHORT).show();
                                }else{

                                        newItem.setItemName(itemname.getText().toString());
                                        newItem.setItemQty(itemqty.getText().toString());
                                        if(itemloc.getText().toString().equals(""))
                                            newItem.setItemLoc("false");
                                            else {
                                            newItem.setItemLoc(itemloc.getText().toString());
                                        }
                                    newItem.additem(getApplicationContext());
                                        finish();

                                }
                            }},3000);


                            }else{

                                itemqty.setError("Wrong Quantity format ");
                            }
                        }else {
                    itemname.setError("Name Cannot Be Empty");
                }

            }
        });


    }
}