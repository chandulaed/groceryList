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


public class NewList {
    private ListStruct listStruct = new ListStruct();

    private String UserID;
    private boolean listavailable=false;
    private boolean taskCompleted=false;
    private boolean NoSnapshot = false;
    private String lastListName =null;
    private String newlistname;
    FirebaseUser user;

    public boolean isNoSnapshot() {
        return NoSnapshot;
    }

    public String getNewlistname() {
        return newlistname;
    }

    public void setNewlistname(String newlistname) {
        this.newlistname = newlistname;
    }

    public NewList() {
        setUserID();
    }

    public boolean getisListavailable() {
        return this.listavailable;
    }

    public void setUserID() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.UserID = user.getUid();
    }

    public void setListname(String name){
        this.listStruct.setListName(name);
    }



    public String getListName() {
        return listStruct.getListName();
    }

    public void setListID(String timestamp){
        this.listStruct.setListID(timestamp +"_"+this.listStruct.getListName());
    }

    public void setdate(String date){
        listStruct.setDateCreated(date);
    }

    public void addList(Context context){
        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            // DatabaseReference reffUserlistCreate = mDatabase.getInstance().getReference();
            DatabaseReference refflistCreate = mDatabase.getInstance().getReference();

            refflistCreate.child("List").child(listStruct.getListID()).setValue(listStruct);
            refflistCreate.child("User").child(UserID).child("Lists").child(listStruct.getListID()).setValue(listStruct);
            refflistCreate.child("User").child(UserID).child("email").setValue(user.getEmail());
            refflistCreate.child("List").child(listStruct.getListID()).setValue(listStruct);
            refflistCreate.child("List").child(listStruct.getListID()).child("users").push().setValue(user.getEmail());
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void searchLastList() {
        taskCompleted = false;
        NoSnapshot=true;
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("User").child(UserID).child("Lists");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() != true) {
                    NoSnapshot=true;
                    taskCompleted = true;
                    userref.removeEventListener(this);
                    return;
                } else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        lastListName = snapshot1.getValue(ListStruct.class).getListName();
                        NoSnapshot=false;
                    }
                    userref.removeEventListener(this);
                    taskCompleted = true;

                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public String getlastListName() {
        return lastListName;
    }
}
