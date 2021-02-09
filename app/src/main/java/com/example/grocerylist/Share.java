package com.example.grocerylist;
import android.app.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Share extends Activity {
    TextView message;
    EditText email;
    Button share,validate;
    String listID;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_list);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width =dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.7));
        listID=getIntent().getStringExtra("listId");
        email = findViewById(R.id.share_email);
        message = findViewById(R.id.txt_message);
        share= findViewById(R.id.btn_share);
        validate=findViewById(R.id.btn_checkAvailability);
        FirebaseUser user;

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText()!=null){
                    searchList(email.getText().toString());

                    new java.util.Timer().schedule(
                            new java.util.TimerTask(){
                                @Override
                                public  void run() {
                                    if(taskCompleted){
                                        if(!NoSnapshot){
                                            if(itemavailable){
                                                message.setText("User Available");
                                                message.setTextColor(Color.GREEN);
                                            }else{
                                                message.setText("Sorry no user available");
                                                message.setTextColor(Color.RED);
                                            }
                                        }else{
                                                message.setText("No data Available");
                                            message.setTextColor(Color.RED);
                                        }
                                    }else{
                                        message.setText("Sorry can't connect to the database");
                                        message.setTextColor(Color.RED);
                                    }
                                    }},3000);

                }
            }
        });




    }
    boolean itemavailable = false;
    boolean taskCompleted=false;
    boolean NoSnapshot=true;
    String uid;

    private void searchList(String email){
        itemavailable = false;
        taskCompleted=false;
        NoSnapshot=true;
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("User");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    taskCompleted=true;
                    NoSnapshot=true;
                    userref.removeEventListener(this);
                    return;
                }else {
                    int i=0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.child("email").getValue().equals(email)) {
                           itemavailable=true;
                           uid= snapshot1.getKey();
                           Toast.makeText(Share.this, uid, Toast.LENGTH_SHORT).show();
                           userref.removeEventListener(this);
                        }
                    }
                    Toast.makeText(Share.this, "Users Successfully analysed", Toast.LENGTH_SHORT).show();
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
}
