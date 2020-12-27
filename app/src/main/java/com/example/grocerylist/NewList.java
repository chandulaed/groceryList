package com.example.grocerylist;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NewList extends Thread {
    private ListStruct listStruct = new ListStruct();

    private String UserID;
    private int listavailable=0;

    public NewList() {

    }

    public int getisListavailable() {
        return this.listavailable;
    }

    public void setUserID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.UserID = user.getUid();
    }

    public NewList(String name) {
        setUserID();
        this.listStruct.setListName(name);
        isListAvailable();
    }

    public String getListName() {
        return listStruct.getListName();
    }

    public void setListID(String timestamp){
        this.listStruct.setListID(timestamp +this.listStruct.getListName());
    }



    public void addList(Context context){
        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            // DatabaseReference reffUserlistCreate = mDatabase.getInstance().getReference();
            DatabaseReference refflistCreate = mDatabase.getInstance().getReference();

            refflistCreate.child("List").child(listStruct.getListID()).setValue(listStruct);
            refflistCreate.child("User").child(UserID).child(listStruct.getListID()).setValue(listStruct);
            refflistCreate.child("List").child(listStruct.getListID()).setValue(listStruct);
            refflistCreate.child("List").child(listStruct.getListID()).child("users").child("user").setValue(UserID);
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void isListAvailable() {
        listavailable = 1;
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("User").child(UserID);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListStruct templist;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    templist = snapshot1.getValue(ListStruct.class);
                    if(templist.getListName().equals(listStruct.getListName())){
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






}
