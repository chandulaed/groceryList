package com.example.grocerylist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Speech_text extends AppCompatActivity {

    // voice_codes { first command 1   ;
    NewList newList;
    private TextToSpeech tts;
    Handler handler = new Handler();
    NewItem newItem;
    EditItem editItem;
    DeleteList deleteList;
    DeleteItem deleteItem;
    SimpleDateFormat listdate = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    SimpleDateFormat iddate = new SimpleDateFormat("ddMMyyyyhhmmss");
    int i=0;
    private ArrayList<TranscriptItem> LSpeeshts=new ArrayList<>();
    private TranscriptItem Speeshts =new TranscriptItem();
    private RecyclerView Srecycler;
    private RecyclerView.Adapter SAdaptor;
    private  RecyclerView.LayoutManager SLayoutManager;
    androidx.appcompat.widget.Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_text);
        toolbar = findViewById(R.id.voicetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Voice Commands");
        Srecycler=findViewById(R.id.sp_recycleView);
        Srecycler.setHasFixedSize(true);
        SLayoutManager=new LinearLayoutManager(this);
        SAdaptor =new SpeechAdaptor(LSpeeshts);
        Srecycler.setLayoutManager(SLayoutManager);
        Srecycler.setAdapter(SAdaptor);
        Srecycler.smoothScrollToPosition(SAdaptor.getItemCount());
        start();
    }

    private void start(){
        Speak("Hello!\nWhat do you want ?");
        handler.postDelayed(new Runnable() {
            public void run() {

                Ask("Hello!..What do you want ?", 1);
            }
        }, 4000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tokenizer commandtokens;
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            switch (requestCode) {
            //first command
                case 1: {
                    commandtokens = new Tokenizer(result.get(0));
                    Utrans(commandtokens.getCommand()+" "+commandtokens.getObject());
                    if (commandtokens.classifier()==1) {
                        newList = new NewList();
                        if(commandtokens.getObject().contains("List")||commandtokens.getObject().contains("list")) {
                            newList = new NewList();
                            listcreate();
                        }else if(commandtokens.getObject().contains("item")||commandtokens.getObject().contains("Item")){
                            newItem = new NewItem();
                            Speak("What is the item you need to add");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("Say item name", 19);
                                }
                            }, 2500);
                        }else if(commandtokens.getObject().equals("")||commandtokens.getObject()==null) {
                            Speak("What you need to add, List or Item");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("List/Item", 32);
                                }
                            }, 2500);
                        }
                        else{
                            Tokenizer tocken = new Tokenizer(commandtokens.getObject());
                            if(tocken.getCommand().contains("List")||tocken.getCommand().contains("list")||tocken.getCommand().contains("Lists")||tocken.getCommand().contains("lists")){
                                newList = new NewList();
                                newList.setNewlistname(tocken.getObject());
                                Speak("Do you want to create a grocery list named," + newList.getNewlistname());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Ask("confirm grocery list", 17);
                                    }
                                }, 4000);
                            }else if(tocken.getCommand().contains("Item")||tocken.getCommand().contains("item")||tocken.getCommand().contains("Items")||tocken.getCommand().contains("items")){
                                newItem = new NewItem();
                                newItem.setItemName(tocken.getObject());
                                this.additemlist();
                            }
                        }
                    }
                    else if(commandtokens.classifier()==2){
                        if(commandtokens.getObject()==""||commandtokens.getObject()==null||commandtokens.getObject().contains("item")||commandtokens.getObject().contains("Item")){
                            editItem = new EditItem();
                            edititem();
                        }else {
                            editItem = new EditItem(commandtokens.getObject());
                            Speak("What is the list you need edit, " + editItem.getItemName());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("list name", 23);
                                }
                            }, 2500);
                        }
                    }
                    else if(commandtokens.classifier()==3) {
                        if(commandtokens.getObject().contains("item")||commandtokens.getObject().contains("Item")){
                            deleteItem();
                        }else if(commandtokens.getObject().contains("list")||commandtokens.getObject().contains("List")) {
                            deleteList();
                        }else if(commandtokens.getObject().equals("")||commandtokens.getObject()==null){
                            Speak("What you need to delete, List or Item");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("List/Item", 31);
                                }
                            }, 2500);
                        }
                    }
                    else if(commandtokens.getObject().contains("close")||commandtokens.getObject().contains("cancel")){
                        cancelProcess("Thank you, cancelling the process");
                    }
                    else{
                        commandnotreg(1);
                    }
                    break;
                }

                case 2:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(2);
                    }
                    else {
                        newList.setNewlistname(result.get(0));
                        Speak("Do you need to create a List Named," + result.get(0));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("confirm use grocery list", 17);
                            }
                        }, 3000);
                    }
                    break;
                }
                case 3: {
                    Utrans(result.get(0));
                    commandtokens = new Tokenizer(result.get(0));
                    if(commandtokens.getCommand().equals("cancel")||commandtokens.getCommand().equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(3);
                    }
                    else {
                        newItem.searchList(result.get(0), getApplicationContext());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                        if (newItem.getlistarray().size() == 0) {
                            Speak("No List Found Do you want to create a list named, ?" + newItem.getlistname());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("confirm create Grocery list", 20);
                                }
                            }, 3000);
                        } else if (newItem.getlistarray().size() == 1){
                            newItem.setListID(newItem.getlistarray().get(0).getKey());
                            Toast.makeText(Speech_text.this, "List Found", Toast.LENGTH_SHORT).show();
                            newItem.searchitem(getApplicationContext());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(newItem.isTaskCompleted()){
                                        if(newItem.isItemavailable()){
                                            Speak("Item already available, "+newItem.getCurrentItemQuantity()+" of "+ newItem.getCurrentItemName()+ "\n Do you want to Edit ?");
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Ask("Do you need to edit this item", 8);
                                                }
                                            }, 3000);
                                        }else{
                                            itemQuntity();
                                        }
                                    }else{
                                        cantconetdatabase(3);
                                    }
                                }
                            },5000);
                        }
                        else{
                            Speak(newItem.getlistarray().size() +" lists found.. Please Select one");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                            while (i<newItem.getlistarray().size()){
                                   String text = String.valueOf(i+1)+"-" + newItem.getlistarray().get(i).getName() +" in " + newItem.getlistarray().get(i).getDate();
                                   Speak( text);
                                   i++;
                            }
                            Speak("What is the list number you need to add, " + newItem.getItemName());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Ask("List Number",4);
                                        }
                                    }, 7000);
                                }
                                }, 3000);
                        }
                            }
                            }, 5000);

                    }
                    break;
                }
                case 4: {
                    Utrans(result.get(0));
                    commandtokens=new Tokenizer(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(4);
                    }else {
                        addItemSetlist(commandtokens.getCommand());
                    }
                break;
                }
                case 5:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")) {
                        commandnotreg(5);
                    }else {
                        Speak("Adding.., " + result.get(0));
                        newItem.setItemQty(result.get(0));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Speak("the place you need to buy ?");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Ask("Name of the place you need to buy",6);
                                    }
                                },4000);
                            }
                        },4000);
                    }
                    break;
                }
                case 6:{
                    Utrans(result.get(0));
                    if (result.get(0).contains("no")||result.get(0).contains("No")||result.get(0).contains("Don't need")||result.get(0).contains("No need")||result.get(0).contains("don't need")||result.get(0).contains("no need")) {
                        Speak("Do you want to add "+newItem.getItemQty()+" of "+newItem.getItemName()+ " to "+newItem.getlistname());
                        newItem.setItemLoc("false");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Confirm Add Item",7);
                            }
                        },5000);

                    }
                    else
                    if (result.get(0).contains("cancel")||result.get(0).contains("close")){
                        Speak("Cancelling add item\n Thank You");
                        newList=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }
                    else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(6);
                    }else{
                        newItem.setItemLoc(result.get(0));
                        Speak("Do you want to add "+newItem.getItemQty()+" of "+newItem.getItemName()+" from "+newItem.getItemLoc()+ " to "+newItem.getlistname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Confirm Add Item",7);
                            }
                        },5000);
                    }
                    break;
                }
                case 7:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")||result.get(0).equals("no")||result.get(0).equals("No")){
                        editItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("yes")||result.get(0).equals("Yes")||result.get(0).equals("confirm")||result.get(0).equals("yep")){
                        newItem.additem(getApplicationContext());
                        Speak("Item Addition completed");
                    }else {
                        commandnotreg(7);
                    }

                    break;
                }
                case 8: {
                    Utrans(result.get(0));
                    if(result.get(0).equals("yes")||result.get(0).equals("confirm")||result.get(0).equals("yep")){
                        editItem=new EditItem(newItem.getCurrentItemName());
                        editItem.setListID(newItem.getListID());
                        editItem.setItemkey(newItem.getCurrentItemID());
                        editItem.setListname(newItem.getListname());
                        newItem=null;
                        Speak("What you need to edit ?");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("name, location, quantity",21);
                            }
                        },2000);
                    }else
                    if(result.get(0).equals("no")||result.get(0).equals("close")||result.get(0).equals("cancel")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }else{
                        commandnotreg(8);
                    }
                    break;
                }
                case 9:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(9);
                    }
                    else {
                        editItem.setNewvalue(result.get(0));
                        Speak("do you need edit name of the item, " + editItem.getItemName() + " to, " + editItem.getNewvalue() + " in, " + editItem.getListname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("confirm ", 10);
                            }
                        }, 2000);
                    }
                    break;

                }
                case 10:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("conform")){
                        editItem.editname();
                        Speak("Edit Name Successful");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }else if (result.get(0).equals("no")||result.get(0).equals("cancel")){
                        Speak("Cancelling edit name of the item\n Thank You");
                        editItem=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);

                    }
                    else{
                        commandnotreg(10);
                    }
                    break;
                }
                case 11:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(11);}
                    else {
                        editItem.setNewvalue(result.get(0));
                        Speak("do you need edit Quantity of the item, " + editItem.getItemName() + " to, " + editItem.getNewvalue() + " in, " + editItem.getListname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("confirm ", 12);
                            }
                        }, 2000);
                    }
                    break;
                }
                case 12:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("conform")){
                        editItem.editQty();
                        Speak("Edit Quantity Successful");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }else if (result.get(0).equals("no")||result.get(0).equals("cancel")){
                        Speak("Cancelling edit quantity of the item\n Thank You");
                        editItem=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);

                    }
                    else{
                        commandnotreg(12);
                    }
                    break;
                }
                case 13:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(13);}
                    else if(result.get(0).contains("remove")||result.get(0).contains("delete")||result.get(0).contains("clear")||result.get(0).contains("Remove")||result.get(0).contains("Delete")||result.get(0).contains("Clear")){
                        editItem.setNewvalue("false");
                        Speak("do you need Remove The Location of the item, " + editItem.getItemName() + " in, " + editItem.getListname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("confirm ", 14);
                            }
                        }, 2000);
                    }else {
                        editItem.setNewvalue(result.get(0));
                        Speak("do you need edit Location of the item, " + editItem.getItemName() + " to, " + editItem.getNewvalue() + " in, " + editItem.getListname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("confirm ", 14);
                            }
                        }, 2000);
                    }
                    break;
                }
                case 14:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("yes")||result.get(0).contains("sure")||result.get(0).contains("conform")){
                        editItem.editLoc();
                        Speak("Edit Location Successful");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }else if (result.get(0).contains("no")||result.get(0).contains("cancel")){
                        Speak("Cancelling edit location of the item\n Thank You");
                        editItem=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);

                    }
                    else{
                        commandnotreg(14);
                    }
                    break;
                }
                case 15:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        deleteList = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(15);
                    }
                    else{
                        deleteList=new DeleteList();
                        deleteList.setListname(result.get(0));
                        deleteList.searchList(getApplicationContext());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(!deleteList.isTaskCompleted()){
                                    cantconetdatabase(15);
                                }else{
                                    if(deleteList.getListarray().size()==1){
                                        deleteList.setListID(deleteList.getListarray().get(0).getKey());
                                        Toast.makeText(Speech_text.this, "List Found", Toast.LENGTH_SHORT).show();
                                        Speak("Do you want to delete, " + deleteList.getListname());
                                        handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Ask("confirm ", 16);
                                        }
                                        }, 2000);
                                    }else if(deleteList.getListarray().size()==0) {
                                        Speak("Sorry, Can't find the list, Try Again  ");
                                        handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Ask("List Name ", 15);
                                        }
                                    }, 2000);
                                    } else {
                                        Speak(deleteList.getListarray().size() +" lists found.. Please Select one");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                while (i<deleteList.getListarray().size()){
                                                    String text = String.valueOf(i+1)+"-" + deleteList.getListarray().get(i).getName() +" in " + deleteList.getListarray().get(i).getDate();
                                                    Speak( text);
                                                    i++;
                                                }
                                                Speak("What is the list number you need to delete, ");
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Ask("List Number",26);
                                                    }
                                                }, 7000);
                                            }
                                        }, 3000);
                                    }
                                }
                            }
                        },5000);
                    }
                    break;
                }
                case 16:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("conform")){
                        deleteList.listdelete();
                        Speak("List is successfully deleted");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }else if (result.get(0).equals("no")||result.get(0).equals("cancel")){
                        Speak("Canceling delete list\n Thank You");
                        deleteList=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);

                    }
                    else{
                        commandnotreg(16);
                    }
                    break;
                }
                case 17:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("confirm")){
                       listcreate(newList.getNewlistname());
                    }else if (result.get(0).equals("no")||result.get(0).equals("cancel")){
                        Speak("Canceling list\n Thank You");
                        newList=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);

                    }
                    else{
                        commandnotreg(17);
                    }
                    break;
                }
                case 18:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("confirm")){
                        Speak("Do you want to create a list named,"+newList.getNewlistname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("confirm use grocery list", 17);
                            }
                        }, 6000);
                    }else if (result.get(0).equals("no")||result.get(0).equals("cancel")){
                        Speak("Please say a name for the list");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("List Name", 2);
                            }
                        }, 3000);

                    }
                    else{
                        commandnotreg(18);
                    }
                    break;
                }
                case 19:{
                    Utrans(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(19);}
                    else {
                        newItem.setItemName(result.get(0));
                        additemlist();
                    }
                    break;
                }
                case 20:{
                   Utrans(result.get(0));
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("confirm")){
                        newList =new NewList();
                        newItem.setListID(iddate.format(new Date())+"_"+newItem.getlistname());
                        Strans(newItem.getListID() + "--" + newItem.getlistname());
                        listcreate(newItem.getlistname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                itemQuntity();
                            }
                        },3000);
                    }else if (result.get(0).equals("no")){
                        Speak("Please say a another list name ?");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Say another list name",3);
                            }
                        },3000);
                    }
                    else if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        Speak("Cancelling the add item/n Thank you ");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },3000);
                    }
                    else{
                        commandnotreg(20);
                    }
                    break;
                }
                case 21:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        editItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).contains("name")||result.get(0).contains("Name")){
                        Speak("Please say the new Name");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Please say a name", 9);
                            }
                        }, 2000);
                    }else if(result.get(0).contains("Quantity")||result.get(0).contains("quantity")){
                        Speak("Please say the new Quantity");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Please say a Quantity",11 );
                            }
                        }, 2000);
                    }else if(result.get(0).contains("Location")||result.get(0).contains("location")){
                        Speak("Please say the new Location");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Please say a Location",13 );
                            }
                        }, 2000);
                    }else {
                        commandnotreg(21);
                    }
                    break;
                }
                case 22:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        editItem = null;
                        cancelProcess("Cancelling Edit Item\n Thank You");
                    }else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(22);
                    }else{
                        editItem.setItemName(result.get(0));
                        Speak("What is the list you need to edit"+ editItem.getItemName());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("List Name",23);
                            }
                        },3000);
                    }
                    break;
                }
                case 23:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        editItem = null;
                        cancelProcess("Cancelling Edit Item\n Thank You");
                    }else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(23);
                    }else{
                        editItem.setListname(result.get(0));
                        editItem.searchList(getApplicationContext());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(editItem.isTaskCompleted()){
                                    if(editItem.getListarray().size()==0)
                                    {
                                        Speak("List is not available please say a new list name");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Ask("new list name",23);
                                            }
                                        },2500);
                                    }else
                                    if(editItem.getListarray().size()==1){
                                        editItem.setListID(editItem.getListarray().get(0).getKey());
                                        editItem.searchitem(getApplicationContext());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(editItem.isTaskCompleted()){
                                                    if(editItem.isItemavailable()){
                                                        Speak("Duplicate Item found, What you need to edit ?");
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Ask("name, location, quantity",21);
                                                            }
                                                        }, 4000);
                                                    }else{
                                                        Speak("Item not available, do you need to add ?");
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Ask("confirm add",25);
                                                            }
                                                        }, 3000);
                                                    }
                                                }else{}
                                            }
                                        },5000);
                                    }else{
                                        Speak(editItem.getListarray().size() +" lists found.. Please Select one");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                while (i<editItem.getListarray().size()){
                                                    String text = String.valueOf(i+1)+"-" + editItem.getListarray().get(i).getName() +" in " + editItem.getListarray().get(i).getDate();
                                                    Speak( text);
                                                    i++;
                                                }
                                                Speak("What is the list number you need to Edit, " + editItem.getItemName());
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Ask("List Number",24);
                                                    }
                                                }, 7000);
                                            }
                                        }, 3000);
                                    }
                                }else
                                    {
                                    cantconetdatabase(23);
                                }
                            }
                        },5000);
                    }
                    break;
                }
                case 24:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        editItem = null;
                        cancelProcess("Cancelling Edit Item\n Thank You");
                    }else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(24);
                    }else{
                        EditItemSetlist(result.get(0));
                    }
                    break;
                }
                case 25:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("yes")||result.get(0).contains("sure")||result.get(0).contains("confirm")){
                        newItem =new NewItem();
                        newItem.setListID(editItem.getListID());
                        newItem.setItemName(editItem.getItemName());
                        newItem.setListname(editItem.getListname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                itemQuntity();
                            }
                        },3000);
                    }else if (result.get(0).contains("no")){
                        Speak("Please say a another list name ?");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Say another list name",23);
                            }
                        },3000);
                    }
                    else if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        Speak("Cancelling the add item/n Thank you ");
                        editItem=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },3000);
                    }
                    else{
                        commandnotreg(25);
                    }
                    break;





                }
                case 26:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        editItem = null;
                        cancelProcess("Cancelling Delete List\n Thank You");
                    }else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(26);
                    }else{
                       DeleteListSetlist(result.get(0));
                    }
                    break;
                }
                case 27 :{
                    Utrans(result.get(0));
                    deleteItem.setItemName(result.get(0));
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        deleteItem= null;
                        cancelProcess("Cancelling Delete Item\n Thank You");
                    }else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(27);
                    }else{
                        Speak("What is the list you need to delete " + deleteItem.getItemName());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("list Name",28);
                            }
                        },2000);
                    }
                    break;
                }
                case 28:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        deleteItem = null;
                        cancelProcess("Cancelling Delete Item\n Thank You");
                    }else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(28);
                    }else{
                        deleteItem.setListname(result.get(0));
                        deleteItem.searchList(getApplicationContext());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(deleteItem.isTaskCompleted()){
                                    if(deleteItem.getListarray().size()==0){
                                        Speak("List cannot be found\n Try again");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                             Ask("List Name",28);
                                            }
                                        },2000);
                                    }else if(deleteItem.getListarray().size()==1){
                                        deleteItem.setListID(deleteItem.getListarray().get(0).getKey());
                                        deleteItem.searchitem(getApplicationContext());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(deleteItem.isTaskCompleted()){
                                                    if(deleteItem.isItemavailable()){
                                                        Toast.makeText(Speech_text.this, "Item found", Toast.LENGTH_SHORT).show();
                                                        Speak("Do you need to delete " + deleteItem.getItemName() +" in " + deleteItem.getListname());
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Ask("Yes/No",30);
                                                            }
                                                        }, 3000);
                                                    }else{
                                                        Speak("Item Cannot be found Please Try again");
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Ask("new Item Name",27);
                                                            }
                                                        }, 3000);
                                                    }
                                                }
                                                else
                                                    {
                                                    cantconetdatabase(28);
                                                }
                                            }
                                            },5000);

                                    }else{
                                        Speak(deleteItem.getListarray().size() +" lists found.. Please Select one");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                while (i<deleteItem.getListarray().size()){
                                                    String text = String.valueOf(i+1)+"-" + deleteItem.getListarray().get(i).getName() +" in " + deleteItem.getListarray().get(i).getDate();
                                                    Speak( text);
                                                    i++;
                                                }
                                                Speak("What is the list number you need to delete, " + deleteItem.getItemName());
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Ask("List Number",29);
                                                    }
                                                }, 7000);
                                            }
                                        }, 3000);

                                    }
                                }
                                else{
                                    cantconetdatabase(28);
                                }
                            }
                        },5000);
                    }
                    break;
                }
                case 29:{
                    Utrans(result.get(0) );
                    if(result.get(0).contains("cancel")||result.get(0).contains("close")){
                        deleteItem = null;
                        cancelProcess("Cancelling delete Item\n Thank You");
                    }else if(result.get(0).equals("")||result.get(0)==null) {
                        commandnotreg(29);
                    }else{
                        DeleteItemSetlist(result.get(0));
                    }
                    break;
                }
                case 30:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("yes")||result.get(0).contains("sure")||result.get(0).contains("confirm")){
                        deleteItem.Itemdelete(getApplicationContext());
                        Speak("Item Successfully deleted ");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }else if (result.get(0).contains("no")||result.get(0).contains("cancel")||result.get(0).contains("close")){
                        Speak("Cancelling the delete item/n Thank you ");
                        deleteItem=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }
                    else{
                        commandnotreg(30);
                    }
                    break;
                }
                case 31:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("List")||result.get(0).contains("list")){
                        deleteList();
                    }else if(result.get(0).contains("Item")||result.get(0).contains("item")) {
                        deleteItem();
                    }else if(result.get(0).contains("close")||result.get(0).contains("cancel")){
                        cancelProcess("cancelling delete process, Thank you");
                    }else{
                        commandnotreg(31);
                    }
                    break;
                }
                case 32:{
                    Utrans(result.get(0));
                    if(result.get(0).contains("List")||result.get(0).contains("list")) {
                        newList = new NewList();
                        listcreate();
                    }else if(result.get(0).contains("item")||result.get(0).contains("Item")){
                        newItem = new NewItem();
                        Speak("What is the item you need to add");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Name of the item", 19);
                            }
                        }, 2500);
                }else if(result.get(0).contains("close")||result.get(0).contains("cancel")){
                        cancelProcess("Thank you, cancelling the process");
                    }
                    else{
                        commandnotreg(1);
                    }
                    break;
                }
            }
        }
    }

    private void deleteItem(){
        deleteItem = new DeleteItem();
        Speak("What is the name Item you need to delete");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Ask("Item Name", 27);
            }
        }, 2500);
    }

    private void deleteList(){
        Speak("What is the name list you need to delete");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Ask("What is the name list you need to delete", 15);
            }
        }, 2500);

    }
    private void cancelProcess(String speak){
        Speak(speak);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2500);
    }


    private void commandnotreg(int commandno){
        Speak("Cannot recognize the command, try again");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Ask("Cannot recognize the command, try again", commandno);
            }
        }, 2500);
    }

    private void cantconetdatabase(int commandno){
        Speak("Sorry.. Your Internet speed is low, ..Cannot to the database..! ");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Ask("Cannot connect to the Database, try again", commandno);
            }
        }, 2500);
    }

    public void Speak (String text){
        TranscriptItem Speeshts =new TranscriptItem();
        Speeshts.from="S";
        Speeshts.message=text;
        LSpeeshts.add(Speeshts);
        SAdaptor.notifyDataSetChanged();
        Srecycler.smoothScrollToPosition(SAdaptor.getItemCount());
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Log.e("TTS", "Failed");
                }
            }
        });
    }

    private void Ask (String command,int voice_code){
        Intent speech_rec = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speech_rec.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speech_rec.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speech_rec.putExtra(RecognizerIntent.EXTRA_PROMPT,command);
        try{
            startActivityForResult(speech_rec,voice_code);
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,   Uri.parse("https://market.android.com/details?id=APP_PACKAGE_NAME"));
            startActivity(browserIntent);
        }
    }

    private void listcreate(String name){
        newList.setListname(name);
        newList.setdate(listdate.format(new Date()));
        Strans("confirming........\n\n");
        newList.setListID(iddate.format(new Date()));
        newList.addList(getApplicationContext());
        Speak("list is Successfully created");
    }

    private void listcreate() {
        newList.searchLastList();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (newList.isTaskCompleted() == true) {
                    if (newList.getlastListName() != null) {
                        newList.setNewlistname(newList.getlastListName() + " new");
                        Speak("your last list name is " + newList.getlastListName() + "\nDo you want to use " + newList.getNewlistname() + " as your new list name ?");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Confirm use grocery list", 18);
                            }
                            }, 6000);
                    }else{
                        Speak("Please say a name for the list");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("List Name", 2);
                            }
                        }, 3000);
                    }
                }else{
                    cantconetdatabase(1);
                }
            }

        }, 5000);


    }

    private void additemlist(){
        Speak("What is the list name you need to add, " + newItem.getItemName());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Ask("What the list name you need to add", 3);
            }
        }, 2500);
    }

    private void addItemSetlist(String text){
        if(text.contains("1")||text.contains("one")||text.contains("One")||text.contains("first")||text.contains("First")){
            newItem.setListID(newItem.getlistarray().get(0).getKey());
        }else
        if(text.contains("2")||text.contains("two")||text.contains("Two")||text.contains("second")||text.contains("Second")){
            newItem.setListID(newItem.getlistarray().get(1).getKey());
        }else
        if(text.contains("3")||text.contains("three")||text.contains("Three")||text.contains("third")||text.contains("Third")){
            newItem.setListID(newItem.getlistarray().get(2).getKey());
        }else
        if(text.contains("4")||text.contains("four")||text.contains("Four")||text.contains("forth")||text.contains("Forth")){
            newItem.setListID(newItem.getlistarray().get(3).getKey());
        }else
        if(text.contains("5")||text.contains("five")||text.contains("Five")||text.contains("fifth")||text.contains("Fifth")){
            newItem.setListID(newItem.getlistarray().get(4).getKey());
        }else
        if(text.contains("last")||text.contains("final")||text.contains("end")||text.contains("recent")){
            newItem.setListID(newItem.getlistarray().get(newItem.getlistarray().size()-1).getKey());
        }else
        {
            commandnotreg(4);
            return;
        }
        Strans("List Select.. " + newItem.getlistname() + newItem.getListID()+"\n\n");
        newItem.searchitem(getApplicationContext());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if(newItem.isTaskCompleted()){
                   if(newItem.isItemavailable()){
                       Speak("Item already available, "+newItem.getCurrentItemQuantity()+" of "+ newItem.getCurrentItemName());
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               Ask("Do you need to edit this item", 8);
                           }
                       }, 3000);
                   }else{
                       itemQuntity();
                   }
               }else{
                   cantconetdatabase(4);
               }
            }
        },5000);
    }

    private void EditItemSetlist(String text){
        if(text.contains("1")||text.contains("one")||text.contains("One")||text.contains("first")||text.contains("First")){
            editItem.setListID(editItem.getListarray().get(0).getKey());
        }else
        if(text.contains("2")||text.contains("two")||text.contains("Two")||text.contains("second")||text.contains("Second")){
            editItem.setListID(editItem.getListarray().get(1).getKey());
        }else
        if(text.contains("3")||text.contains("three")||text.contains("Three")||text.contains("third")||text.contains("Third")){
            editItem.setListID(editItem.getListarray().get(2).getKey());
        }else
        if(text.contains("4")||text.contains("four")||text.contains("Four")||text.contains("forth")||text.contains("Forth")){
            editItem.setListID(editItem.getListarray().get(3).getKey());
        }else
        if(text.contains("5")||text.contains("five")||text.contains("Five")||text.contains("fifth")||text.contains("Fifth")){
            editItem.setListID(editItem.getListarray().get(4).getKey());
        }else
        if(text.contains("last")||text.contains("final")||text.contains("end")||text.contains("recent")){
            editItem.setListID(editItem.getListarray().get(editItem.getListarray().size()-1).getKey());
        }else {
            commandnotreg(24);
            return;
        }
        Strans("List Select.. " + editItem.getListname() +"--"+ editItem.getListID()+"\n\n");
        editItem.searchitem(getApplicationContext());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if(editItem.isTaskCompleted()){
                   if(editItem.isItemavailable()){
                       Toast.makeText(Speech_text.this, "Item found", Toast.LENGTH_SHORT).show();
                       Speak("What you need to edit ?");
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               Ask("name, location, quantity",21);
                           }
                       }, 3000);
                   }else{
                       Speak("Item not available, do you need to add ?");
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               Ask("confirm add",25);
                           }
                       }, 3000);
                   }
               }else{
                   cantconetdatabase(4);
               }
            }
        },5000);
    }

    private void DeleteListSetlist(String text){
        if(text.contains("1")||text.contains("one")||text.contains("One")||text.contains("first")||text.contains("First")){
            deleteList.setListID(deleteList.getListarray().get(0).getKey());
        }else
        if(text.contains("2")||text.contains("two")||text.contains("Two")||text.contains("second")||text.contains("Second")){
            deleteList.setListID(deleteList.getListarray().get(1).getKey());
        }else
        if(text.contains("3")||text.contains("three")||text.contains("Three")||text.contains("third")||text.contains("Third")){
            deleteList.setListID(deleteList.getListarray().get(2).getKey());
        }else
        if(text.contains("4")||text.contains("four")||text.contains("Four")||text.contains("forth")||text.contains("Forth")){
            deleteList.setListID(deleteList.getListarray().get(3).getKey());
        }else
        if(text.contains("5")||text.contains("five")||text.contains("Five")||text.contains("fifth")||text.contains("Fifth")){
            deleteList.setListID(deleteList.getListarray().get(4).getKey());
        }else
        if(text.contains("last")||text.contains("final")||text.contains("end")||text.contains("recent")){
            deleteList.setListID(deleteList.getListarray().get(deleteList.getListarray().size()-1).getKey());
        }else {
            commandnotreg(26);
            return;
        }
        Strans("List Select.. " + deleteList.getListname() +"--"+ deleteList.getListID()+"\n\n");
        Speak("Do you want to delete, " + deleteList.getListname());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Ask("confirm ", 16);
            }
        }, 2000);
    }

    private void DeleteItemSetlist(String text){
        if(text.contains("1")||text.contains("one")||text.contains("One")||text.contains("first")||text.contains("First")){
            deleteItem.setListID(deleteItem.getListarray().get(0).getKey());
        }else
        if(text.contains("2")||text.contains("two")||text.contains("Two")||text.contains("second")||text.contains("Second")){
            deleteItem.setListID(deleteItem.getListarray().get(1).getKey());
        }else
        if(text.contains("3")||text.contains("three")||text.contains("Three")||text.contains("third")||text.contains("Third")){
            deleteItem.setListID(deleteItem.getListarray().get(2).getKey());
        }else
        if(text.contains("4")||text.contains("four")||text.contains("Four")||text.contains("forth")||text.contains("Forth")){
            deleteItem.setListID(deleteItem.getListarray().get(3).getKey());
        }else
        if(text.contains("5")||text.contains("five")||text.contains("Five")||text.contains("fifth")||text.contains("Fifth")){
            deleteItem.setListID(deleteItem.getListarray().get(4).getKey());
        }else
        if(text.contains("last")||text.contains("final")||text.contains("end")||text.contains("recent")){
            deleteItem.setListID(deleteItem.getListarray().get(deleteItem.getListarray().size()-1).getKey());
        }else {
            commandnotreg(29);
            return;
        }
        Strans("List Select.. " + deleteItem.getListname() +"--"+ deleteItem.getListID()+"\n\n");
        deleteItem.searchitem(getApplicationContext());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(deleteItem.isTaskCompleted()){
                    if(deleteItem.isItemavailable()){
                        Toast.makeText(Speech_text.this, "Item found", Toast.LENGTH_SHORT).show();
                        Speak("Do you need to delete" + deleteItem.getItemName() +" in " + deleteItem.getListname());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Yes/No",30);
                            }
                        }, 3000);
                    }else{
                        Speak("Item Cannot be found Please Try again");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("new Item Name",29);
                            }
                        }, 3000);
                    }
                }else{
                    cantconetdatabase(29);
                }
            }
        },5000);
    }

    void itemQuntity(){
        Speak("How much you need to add");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
             Ask("How much you need to add",5);
            }
        },5000);
    }

    void edititem(){
        Speak("What is the item name you need to edit ?");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Ask("Item Name", 22);
            }
        },2000);
    }

    void Utrans (String message){
        TranscriptItem Speeshts =new TranscriptItem();
        Speeshts.from="U";
        Speeshts.message=message;
        LSpeeshts.add(Speeshts);
        SAdaptor.notifyDataSetChanged();
        Srecycler.smoothScrollToPosition(SAdaptor.getItemCount());
    }

    void Strans (String message){
        TranscriptItem Speeshts =new TranscriptItem();
        Speeshts.from="S";
        Speeshts.message=message;
        LSpeeshts.add(Speeshts);
        SAdaptor.notifyDataSetChanged();
        Srecycler.smoothScrollToPosition(SAdaptor.getItemCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.voicemenue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit: {
                this.finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}