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

public class DeleteList {
    private String listname,listID;
    FirebaseUser user;
    private boolean listavailable=false;
    private boolean taskCompleted=false;
    private boolean NoSnapshot = false;
    private ArrayList<AddItemSearchList> listarray = new ArrayList<>();


    public DeleteList() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getListID() {
        return listID;
    }

    public void searchList( Context context) {
        taskCompleted=false;
        NoSnapshot=true;
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid()).child("Lists");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    taskCompleted=true;
                    NoSnapshot=true;
                    userref.removeEventListener(this);
                    return;
                }else {
                    ListStruct templist;
                    listarray.clear();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        templist = snapshot1.getValue(ListStruct.class);
                        if (templist.getListName().equals(getListname())) {
                            AddItemSearchList tempsearch =new AddItemSearchList();
                            tempsearch.setName(templist.getListName());
                            tempsearch.setDate(templist.getDateCreated());
                            tempsearch.setKey(templist.getListID());
                            listarray.add(tempsearch);
                        }
                    }
                    Toast.makeText(context, "Current list was successfully analysed", Toast.LENGTH_SHORT).show();
                    userref.removeEventListener(this);
                    NoSnapshot=false;
                    taskCompleted=true;
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return;

    }

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public boolean isListavailable() {
        return listavailable;
    }

    public void setListavailable(boolean listavailable) {
        this.listavailable = listavailable;
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    public boolean isNoSnapshot() {
        return NoSnapshot;
    }

    public void setNoSnapshot(boolean noSnapshot) {
        NoSnapshot = noSnapshot;
    }


    public ArrayList<AddItemSearchList> getListarray() {
        return listarray;
    }

    public void setListarray(ArrayList<AddItemSearchList> listarray) {
        this.listarray = listarray;
    }

    public void listdelete(){
        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference refflistCreate = mDatabase.getInstance().getReference();
            refflistCreate.child("List").child(listID).setValue(null);
            refflistCreate.child("User").child(user.getUid()).child("Lists").child(listID).setValue(null);
        }
        catch (Exception e){
        }
    }




}
