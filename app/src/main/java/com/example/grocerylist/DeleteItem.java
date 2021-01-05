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

public class DeleteItem {
    private String listname,listID,ItemID,ItemName;
    FirebaseUser user;
    private boolean listavailable=false;
    private boolean taskCompleted=false;
    private boolean NoSnapshot = false;
    private boolean itemavailable = false;
    private ArrayList<AddItemSearchList> listarray = new ArrayList<>();



    public DeleteItem() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void Itemdelete(Context context){
        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference reffitemCreate = mDatabase.getInstance().getReference();
            reffitemCreate.child("List").child(listID).child("Items").child(ItemID).setValue(null);
        }
        catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void searchList( Context context) {
        itemavailable = false;
        taskCompleted=false;
        NoSnapshot=true;
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
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
                            ItemID=String.valueOf(snapshot1.getKey());
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



    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
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

    public boolean isItemavailable() {
        return itemavailable;
    }

    public void setItemavailable(boolean itemavailable) {
        this.itemavailable = itemavailable;
    }

    public ArrayList<AddItemSearchList> getListarray() {
        return listarray;
    }

    public void setListarray(ArrayList<AddItemSearchList> listarray) {
        this.listarray = listarray;
    }
}
