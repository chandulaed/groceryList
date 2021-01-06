package com.example.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    TextView mail;
    androidx.appcompat.widget.Toolbar toolbar;
    private FloatingActionButton voiceRecognition;
    private ArrayList<ListStruct> showlist=new ArrayList<>();;
    private RecyclerView listViewRecycler;
    private ListViewAdaptor listViewAdaptor;
    private RecyclerView.LayoutManager listViewLManager;
    private DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);
        mail = findViewById(R.id.mail);
        toolbar = findViewById(R.id.home_tool_bar);
        voiceRecognition = findViewById(R.id.voice_btn);
        setSupportActionBar(toolbar);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            getSupportActionBar().setTitle("HELLO " + signInAccount.getGivenName());
            mail.setText("UserID -- " + signInAccount.getEmail());
        }
        voiceRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()==true){
                Intent v_intent = new Intent(getApplicationContext(), Speech_text.class);
                startActivity(v_intent);}
                else{
                    Toast.makeText(Dashboard.this, "No Internet Connection Please Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userref = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid()).child("Lists");
        userref.keepSynced(true);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(Dashboard.this, "No List available", Toast.LENGTH_SHORT).show();
                    showlist.clear();
                    listViewAdaptor.notifyDataSetChanged();
                    return;
                }else {
                    ListStruct templist;
                    showlist.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        templist = snapshot1.getValue(ListStruct.class);
                        showlist.add(templist);
                    }
                    Toast.makeText(Dashboard.this, "List Update Completed", Toast.LENGTH_SHORT).show();
                    listViewAdaptor.notifyDataSetChanged();
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        RecyclerView();
    }

    private void RecyclerView(){
        listViewRecycler =findViewById(R.id.list_view_Recycler);
        listViewRecycler.setHasFixedSize(true);
        listViewLManager = new LinearLayoutManager(this);
        listViewAdaptor= new ListViewAdaptor(showlist);
        listViewRecycler.setLayoutManager(listViewLManager);
        listViewRecycler.setAdapter(listViewAdaptor);

        listViewAdaptor.setOnListClickListener(new ListViewAdaptor.OnListClickListener() {
            @Override
            public void onListClick(int Position) {
                Toast.makeText(Dashboard.this, String.valueOf(Position+1)+" list Selected", Toast.LENGTH_SHORT).show();
                Intent v_intent = new Intent(getApplicationContext(), Grocerylist.class);
                v_intent.putExtra("List_ID",showlist.get(Position).getListID());
                v_intent.putExtra("List_Name",showlist.get(Position).getListName());
                startActivity(v_intent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                return true;
            case R.id.logout:
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}