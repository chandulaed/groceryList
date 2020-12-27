package com.example.grocerylist;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewItem {
    private int listavailable = 1;
    private FirebaseUser user;
    private String listID;
    private ItemStruct itemStruct;
    private String listname;

    public int getListavailable() {
        return listavailable;
    }

    public String getlistname(){
        return listname;
    }



    public String getListID() {
        return listID;
    }

    public NewItem(String name) {
        itemStruct=new ItemStruct(name);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void searchList(String Listname) {
        this.listname =Listname;
        listavailable = 1;
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListStruct templist;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    templist = snapshot1.getValue(ListStruct.class);
                    if(templist.getListName().equals(Listname)){
                        listID=snapshot1.getKey();
                        userref.removeEventListener(this);
                        listavailable =5;
                        return;
                    };
                }
                userref.removeEventListener(this);
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return;
    }


    public String getItemName() {
        return itemStruct.getItemName();
    }

    public String getItemQty() {
        return itemStruct.getItemQty();
    }

    public String getItemLoc() {
        return itemStruct.getItemLocation();
    }

    public void setItemQty(String Qty) {
        itemStruct.setItemQty(Qty);
    }

    public void setItemLoc(String Loc) {
        itemStruct.setItemLocation(Loc);
    }

}


