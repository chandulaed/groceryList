package com.example.grocerylist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MCreatList extends AppCompatActivity {
    private EditText listName;
    private Button createList;
    SimpleDateFormat listdate = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    SimpleDateFormat iddate = new SimpleDateFormat("ddMMyyyyhhmmss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_creat_list);

        createList = findViewById(R.id.btn_CreateList);
        listName= findViewById(R.id.mlist_name);

        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SlistName=listName.getText().toString();
                if(SlistName.equals("")||SlistName==null){
                    Toast.makeText(MCreatList.this, "List Name Cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    NewList newList = new NewList();
                    newList.setListname(SlistName);
                    newList.setdate(listdate.format(new Date()));
                    newList.setListID(iddate.format(new Date()));
                    newList.addList(getApplicationContext());
                    finish();
                };

            }

            }
        );
    }


}