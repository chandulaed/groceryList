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

public class EditItem {
    private String itemName,listID,newvalue,listname,itemkey;
    private int listavailable=1,itemavailable=1;
    private FirebaseUser user;

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

    public int getItemavailable() {
        return itemavailable;
    }

    public String getItemName() {
        return itemName;
    }

    public int getListavailable() {
        return listavailable;
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



    public void searchList(String Listname) {
        this.listavailable = 1;
        this.listname = Listname;
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

    public void searchitem(Context con){
        itemavailable =1;
        DatabaseReference itemref = FirebaseDatabase.getInstance().getReference().child("List").child(listID).child("Items");
        Toast.makeText(con,itemref.getKey(), Toast.LENGTH_SHORT).show();
        itemref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                  // Toast.makeText(con, (String) snapshot1.child("item").child("itemName").getValue(), Toast.LENGTH_SHORT).show();
                  if (snapshot1.child("itemName").getValue().equals(itemName)) {
                      itemkey=snapshot1.getKey();
                      itemref.removeEventListener(this);
                      itemavailable = 5;
                      return;
                  }
              }
              itemref.removeEventListener(this);
              return;
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
}
