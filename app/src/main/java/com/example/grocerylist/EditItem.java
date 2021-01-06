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

public class EditItem {
    private String itemName,listID,newvalue,listname,itemkey;
    private FirebaseUser user;
    private boolean listavailable=false;
    private boolean taskCompleted=false;
    private boolean NoSnapshot = false;
    private boolean itemavailable = false;
    private ArrayList<AddItemSearchList> listarray = new ArrayList<>();

    public String getListname() {
        return listname;
    }

    public String getNewvalue() {
        return newvalue;
    }

    public void setNewvalue(String newvalue) {
        this.newvalue = newvalue;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean getItemavailable() {
        return itemavailable;
    }

    public String getItemName() {
        return itemName;
    }

    public boolean getListavailable() {
        return listavailable;
    }

    public boolean isListavailable() {
        return listavailable;
    }

    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    public boolean isNoSnapshot() {
        return NoSnapshot;
    }

    public boolean isItemavailable() {
        return itemavailable;
    }

    public ArrayList<AddItemSearchList> getListarray() {
        return listarray;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public String getListID() {
        return listID;
    }

    public EditItem(String itemName) {
        this.itemName = itemName;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public void setItemkey(String itemkey) {
        this.itemkey = itemkey;
    }



    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public void searchList(Context context) {
        itemavailable = false;
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
                        if (snapshot1.child("itemName").getValue().equals(getItemName()))  {
                            itemkey=String.valueOf(snapshot1.getKey());
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

    public void editname(){
        DatabaseReference editnameref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items").child(itemkey);
        editnameref.child("itemName").setValue(newvalue);
    }
    public void editLoc(){
        DatabaseReference editnameref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items").child(itemkey);
        editnameref.child("itemLocation").setValue(newvalue);
    }
    public void editQty(){
        DatabaseReference editnameref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items").child(itemkey);
        editnameref.child("itemQty").setValue(newvalue);
    }

    public String getItemkey() {
        return itemkey;
    }

    public EditItem() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}
