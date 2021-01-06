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


public class NewItem {
    private boolean listavailable=false;
    private boolean taskCompleted=false;
    private boolean NoSnapshot = false;
    private boolean itemavailable = false;
    private FirebaseUser user;
    private String listID, CurrentItemID,CurrentItemName,CurrentItemQuantity,CurrentItemLoc;
    private ItemStruct itemStruct;
    private String listname;
    private ArrayList <AddItemSearchList> listarray = new ArrayList<>();


    public ArrayList<AddItemSearchList> getlistarray() {
        return listarray;
    }

    public boolean getListavailable() {
        return listavailable;
    }

    public String getlistname(){
        return listname;
    }


    public String getListID() {
        return listID;
    }

    public NewItem() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setItemName(String name){
        itemStruct=new ItemStruct(name);
    }

    public void searchList(String listName,Context context) {
        itemavailable = false;
        taskCompleted=false;
        NoSnapshot=true;
        this.listname =listName;
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
                    ListStruct templist=null;
                    listarray.clear();
                    int i=0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        templist = snapshot1.getValue(ListStruct.class);
                        if (templist.getListName().equals(getlistname())) {
                            AddItemSearchList tempsearch =new AddItemSearchList();
                            tempsearch.setName(templist.getListName());
                            tempsearch.setDate(templist.getDateCreated());
                            tempsearch.setKey(templist.getListID());
                            listarray.add(tempsearch);
                            Toast.makeText(context, String.valueOf(listarray.get(i).getDate()), Toast.LENGTH_SHORT).show();
                            i++;
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


    public String getItemName() {
        return itemStruct.getItemName();
    }

    public String getItemQty() {
        return itemStruct.getItemQty();
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
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

    public void additem(Context context){
        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference reffitemCreate = mDatabase.getInstance().getReference().child("List").child(listID).child("Items");
            reffitemCreate.push().setValue(itemStruct);
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void searchitem(Context context){
        itemavailable = false;
        taskCompleted=false;
        NoSnapshot=true;
        DatabaseReference itemref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items");
        itemref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()!=true){
                    itemavailable= false;
                    taskCompleted=true;
                    NoSnapshot=true;
                    itemref.removeEventListener(this);
                    return;
                }else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.child("itemName").getValue().equals(itemStruct.getItemName()))  {
                            CurrentItemName=String.valueOf(snapshot1.child("itemName").getValue());
                            CurrentItemQuantity=String.valueOf(snapshot1.child("itemQty").getValue());
                            CurrentItemLoc=String.valueOf(snapshot1.child("itemLocation").getValue());
                            CurrentItemID=String.valueOf(snapshot1.getKey());
                            itemref.removeEventListener(this);
                            Toast.makeText(context, "List analyzed completed", Toast.LENGTH_SHORT).show();
                            itemavailable = true;
                            taskCompleted=true;
                            NoSnapshot=false;
                            return;
                        }
                    }
                    Toast.makeText(context, "List analyzed completed", Toast.LENGTH_SHORT).show();
                    itemref.removeEventListener(this);
                    itemavailable = false;
                    taskCompleted=true;
                    NoSnapshot=false;
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return;
    }

    public boolean isListavailable() {
        return listavailable;
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public boolean isItemavailable() {
        return itemavailable;
    }

    public String getCurrentItemID() {
        return CurrentItemID;
    }

    public void setCurrentItemID(String currentItemID) {
        CurrentItemID = currentItemID;
    }

    public String getCurrentItemName() {
        return CurrentItemName;
    }

    public void setCurrentItemName(String currentItemName) {
        CurrentItemName = currentItemName;
    }

    public String getCurrentItemQuantity() {
        return CurrentItemQuantity;
    }

    public void setCurrentItemQuantity(String currentItemQuantity) {
        CurrentItemQuantity = currentItemQuantity;
    }

    public String getCurrentItemLoc() {
        return CurrentItemLoc;
    }

    public void setCurrentItemLoc(String currentItemLoc) {
        CurrentItemLoc = currentItemLoc;
    }
}


