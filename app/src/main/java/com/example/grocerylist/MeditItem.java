package com.example.grocerylist;

import android.content.DialogInterface;
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

public class MeditItem extends AppCompatActivity {

    String listID,itemName,itemLoc,itemCost,itemQty,listName,NewIname,NewIloc,NewIqty;
    TextView olditemName,olditemLoc,olditemQty,olistName;
    EditText newitemName,newitemLoc,newitemQty;
    Button editItem,deleteItem;
    ItemStruct newItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medit_item);
        listID = getIntent().getStringExtra("List_ID");
        itemName = getIntent().getStringExtra("Item_Name");
        itemLoc = getIntent().getStringExtra("Item_Loc");
        itemQty = getIntent().getStringExtra("Item_Qty");
        listName = getIntent().getStringExtra("List_Name");

        olditemName=findViewById(R.id.old_item_name);
        olditemLoc=findViewById(R.id.old_item_location);
        olditemQty=findViewById(R.id.old_item_quantity);
        olistName=findViewById(R.id.edit_list_name);
        newitemName=findViewById(R.id.edit_item_name);
        newitemLoc=findViewById(R.id.edit_item_location);
        newitemQty=findViewById(R.id.edit_item_quantity);
        deleteItem = findViewById(R.id.btn_deleteitem);
        olistName.setText(listName);
        olditemName.setText(itemName);

        olditemQty.setText(itemQty);
        if(itemLoc.equals("false"))
            olditemLoc.setText("");
        else
            olditemLoc.setText(itemLoc);

        editItem=findViewById(R.id.btn_edtitem);

        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditItem editItem = new EditItem(itemName);
                editItem.setListID(listID);
                editItem.searchitem(getApplicationContext());
                Toast.makeText(MeditItem.this, newitemName.getText(), Toast.LENGTH_SHORT).show();
                if(newitemName.getText().toString().trim().length()!=0)
                    itemName = (newitemName.getText().toString().trim());
                if(newitemQty.getText().toString().trim().length()!=0){
                    if(collective(newitemQty.getText().toString().trim())|| newitemQty.getText().toString().trim().matches("\\d+(?:\\.\\d+)?"))
                        itemQty=(newitemQty.getText().toString().trim());
                }
                if(newitemLoc.getText().toString().trim().length()!=0)
                    itemLoc=(newitemLoc.getText().toString().trim());
                if(collective(newitemQty.getText().toString().trim())|| newitemQty.getText().toString().trim().matches("\\d+(?:\\.\\d+)?"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure want to edit " + itemName)
                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editItem.editall(itemName, itemLoc, itemQty);
                            Toast.makeText(MeditItem.this, "Successfully Edited ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                AlertDialog alertDialog = builder.create();
                new java.util.Timer().schedule(
                        new java.util.TimerTask(){
                            @Override
                            public  void run() {
                if(editItem.getItemavailable()==true&&editItem.isTaskCompleted()==true&&editItem.isNoSnapshot()==false) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.show();
                        }
                    });

                }else if(editItem.isTaskCompleted()==false){
                    Toast.makeText(MeditItem.this, "Sorry can't connect to the database", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MeditItem.this, "Error please refresh the application", Toast.LENGTH_SHORT).show();
                } }},3000);
                } else{
                    newitemQty.setError("Wrong Item Quantity Format");
                }

            }

            }
        );

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteItem deleteItem = new DeleteItem();
                deleteItem.setListID(listID);
                deleteItem.setItemName(itemName);
                deleteItem.searchitem(getApplicationContext());

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setMessage("Are you sure want to delete " + itemName )
                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem.Itemdelete(getApplicationContext());
                        finish();
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                new java.util.Timer().schedule(
                        new java.util.TimerTask(){
                            @Override
                            public  void run() {
                                if(deleteItem.isItemavailable()==true&&deleteItem.isTaskCompleted()==true&&deleteItem.isNoSnapshot()==false) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.show();
                                        }
                                    });

                                }else if(deleteItem.isTaskCompleted()==false){
                                    Toast.makeText(MeditItem.this, "Sorry can't connect to the database", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MeditItem.this, "Error please refresh the application", Toast.LENGTH_SHORT).show();
                                } }},3000);
            }
        });


    }
}