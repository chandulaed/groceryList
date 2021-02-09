package com.example.grocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Speech_text extends AppCompatActivity {

    // voice_codes { first command 1   ;
    TextView SpeechTranscript;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_text);
        SpeechTranscript = findViewById(R.id.speech_transcript);
        start();

    }


    private void start(){
        Speak("Hello!..What do you want ?");
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
                    SpeechTranscript.append(commandtokens.getCommand()+" "+commandtokens.getObject() +"\n");
                    if (commandtokens.classifier()==1) {
                        newList = new NewList();
                        if(commandtokens.getObject().equals("")||commandtokens.getObject()==null||commandtokens.getObject()=="List"||commandtokens.getObject()=="list"){
                            listcreate();
                        }else{
                            newList.setNewlistname(commandtokens.getObject());
                            Speak("Do you want to create a grocery list named," + newList.getNewlistname());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("confirm use grocery list", 17);
                                }
                            }, 4000);
                        }
                    }
                    else if (commandtokens.classifier()==4) {
                        newItem = new NewItem();
                        if(commandtokens.getObject()==""||commandtokens.getObject()==null||commandtokens.getObject().contains("item")||commandtokens.getObject().contains("Item")){
                            Speak("What is the item you need to add");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("Say item name", 19);
                                }
                            }, 2500);
                        }
                        else {
                            newItem.setItemName(commandtokens.getObject());
                            this.additemlist();
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                            SpeechTranscript.append("List Found.." + newItem.getlistname() + newItem.getListID()+"\n\n");
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
                    SpeechTranscript.append(result.get(0)+"\n\n");
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
                    SpeechTranscript.append(result.get(0)+"\n\n");
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
                                        Ask("Place you need to buy",6);
                                    }
                                },4000);
                            }
                        },4000);
                    }
                    break;
                }
                case 6:{
                    SpeechTranscript.append(result.get(0)+"\n\n");
                    if (result.get(0).equals("no")||result.get(0).equals("No")||result.get(0).equals("Don't need")||result.get(0).equals("No need")||result.get(0).equals("don't need")||result.get(0).equals("no need")) {
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
                    if (result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(13);}
                    else if(result.get(0).equals("remove")||result.get(0).equals("delete")||result.get(0).equals("clear")||result.get(0).equals("Remove")||result.get(0).equals("Delete")||result.get(0).equals("Clear")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("conform")){
                        editItem.editLoc();
                        Speak("Edit Location Successful");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }else if (result.get(0).equals("no")||result.get(0).equals("cancel")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                                        SpeechTranscript.append("List Found - ListID --"+ deleteList.getListID() +"\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("confirm")){
                        newList =new NewList();
                        newItem.setListID(iddate.format(new Date())+"_"+newItem.getlistname());
                        SpeechTranscript.append(newItem.getListID() + "--" + newItem.getlistname());
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        editItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("name")||result.get(0).equals("Name")){
                        Speak("Please say the new Name");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Please say a name", 9);
                            }
                        }, 2000);
                    }else if(result.get(0).equals("Quantity")||result.get(0).equals("quantity")){
                        Speak("Please say the new Quantity");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Please say a Quantity",11 );
                            }
                        }, 2000);
                    }else if(result.get(0).equals("Location")||result.get(0).equals("location")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                                                        SpeechTranscript.append("Item found" + editItem.getItemkey()+"\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("confirm")){
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
                    }else if (result.get(0).equals("no")){
                        Speak("Please say a another list name ?");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Say another list name",23);
                            }
                        },3000);
                    }
                    else if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    deleteItem.setItemName(result.get(0));
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                                                        SpeechTranscript.append("Item found" + deleteItem.getItemID()+"\n\n");
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("yes")||result.get(0).equals("sure")||result.get(0).equals("confirm")){
                        deleteItem.Itemdelete(getApplicationContext());
                        Speak("Item Successfully deleted ");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }else if (result.get(0).equals("no")||result.get(0).equals("cancel")||result.get(0).equals("close")){
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
                    SpeechTranscript.append(result.get(0) + "\n\n");
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

    private void noname(){
        Speak("Sorry No Name found!, Please Try again");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Ask("try again", 1);

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
        SpeechTranscript.append(text+"\n\n");
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

    private void Ask(String command,int voice_code){
        Intent speech_rec = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speech_rec.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speech_rec.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speech_rec.putExtra(RecognizerIntent.EXTRA_PROMPT,command);
        try{
            startActivityForResult(speech_rec,voice_code);
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void listcreate(String name){
        newList.setListname(name);
        newList.setdate(listdate.format(new Date()));
        SpeechTranscript.append("confirming........\n\n");
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
                        Speak("your last list name is," + newList.getlastListName() + "\n\ndo you want to use," + newList.getNewlistname() + "as your new list name ?");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("confirm use grocery list", 18);
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
        if(text.equals("1")||text.equals("one")||text.equals("One")||text.equals("first")||text.equals("First")){
            newItem.setListID(newItem.getlistarray().get(0).getKey());
        }else
        if(text.equals("2")||text.equals("two")||text.equals("Two")||text.equals("second")||text.equals("Second")){
            newItem.setListID(newItem.getlistarray().get(1).getKey());
        }else
        if(text.equals("3")||text.equals("three")||text.equals("Three")||text.equals("third")||text.equals("Third")){
            newItem.setListID(newItem.getlistarray().get(2).getKey());
        }else
        if(text.equals("4")||text.equals("four")||text.equals("Four")||text.equals("forth")||text.equals("Forth")){
            newItem.setListID(newItem.getlistarray().get(3).getKey());
        }else
        if(text.equals("5")||text.equals("five")||text.equals("Five")||text.equals("fifth")||text.equals("Fifth")){
            newItem.setListID(newItem.getlistarray().get(4).getKey());
        }else
        if(text.equals("last")||text.equals("final")||text.equals("end")||text.equals("recent")){
            newItem.setListID(newItem.getlistarray().get(newItem.getlistarray().size()-1).getKey());
        }else
        {
            commandnotreg(4);
            return;
        }
        SpeechTranscript.append("List Select.. " + newItem.getlistname() + newItem.getListID()+"\n\n");
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
        if(text.equals("1")||text.equals("one")||text.equals("One")||text.equals("first")||text.equals("First")){
            editItem.setListID(editItem.getListarray().get(0).getKey());
        }else
        if(text.equals("2")||text.equals("two")||text.equals("Two")||text.equals("second")||text.equals("Second")){
            editItem.setListID(editItem.getListarray().get(1).getKey());
        }else
        if(text.equals("3")||text.equals("three")||text.equals("Three")||text.equals("third")||text.equals("Third")){
            editItem.setListID(editItem.getListarray().get(2).getKey());
        }else
        if(text.equals("4")||text.equals("four")||text.equals("Four")||text.equals("forth")||text.equals("Forth")){
            editItem.setListID(editItem.getListarray().get(3).getKey());
        }else
        if(text.equals("5")||text.equals("five")||text.equals("Five")||text.equals("fifth")||text.equals("Fifth")){
            editItem.setListID(editItem.getListarray().get(4).getKey());
        }else
        if(text.equals("last")||text.equals("final")||text.equals("end")||text.equals("recent")){
            editItem.setListID(editItem.getListarray().get(editItem.getListarray().size()-1).getKey());
        }else {
            commandnotreg(24);
            return;
        }
        SpeechTranscript.append("List Select.. " + editItem.getListname() +"--"+ editItem.getListID()+"\n\n");
        editItem.searchitem(getApplicationContext());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               if(editItem.isTaskCompleted()){
                   if(editItem.isItemavailable()){
                       SpeechTranscript.append("Item found" + editItem.getItemkey()+"\n\n");
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
        if(text.equals("1")||text.equals("one")||text.equals("One")||text.equals("first")||text.equals("First")){
            deleteList.setListID(deleteList.getListarray().get(0).getKey());
        }else
        if(text.equals("2")||text.equals("two")||text.equals("Two")||text.equals("second")||text.equals("Second")){
            deleteList.setListID(deleteList.getListarray().get(1).getKey());
        }else
        if(text.equals("3")||text.equals("three")||text.equals("Three")||text.equals("third")||text.equals("Third")){
            deleteList.setListID(deleteList.getListarray().get(2).getKey());
        }else
        if(text.equals("4")||text.equals("four")||text.equals("Four")||text.equals("forth")||text.equals("Forth")){
            deleteList.setListID(deleteList.getListarray().get(3).getKey());
        }else
        if(text.equals("5")||text.equals("five")||text.equals("Five")||text.equals("fifth")||text.equals("Fifth")){
            deleteList.setListID(deleteList.getListarray().get(4).getKey());
        }else
        if(text.equals("last")||text.equals("final")||text.equals("end")||text.equals("recent")){
            deleteList.setListID(deleteList.getListarray().get(deleteList.getListarray().size()-1).getKey());
        }else {
            commandnotreg(26);
            return;
        }
        SpeechTranscript.append("List Select.. " + deleteList.getListname() +"--"+ deleteList.getListID()+"\n\n");
        Speak("Do you want to delete, " + deleteList.getListname());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Ask("confirm ", 16);
            }
        }, 2000);
    }

    private void DeleteItemSetlist(String text){
        if(text.equals("1")||text.equals("one")||text.equals("One")||text.equals("first")||text.equals("First")){
            deleteItem.setListID(deleteItem.getListarray().get(0).getKey());
        }else
        if(text.equals("2")||text.equals("two")||text.equals("Two")||text.equals("second")||text.equals("Second")){
            deleteItem.setListID(deleteItem.getListarray().get(1).getKey());
        }else
        if(text.equals("3")||text.equals("three")||text.equals("Three")||text.equals("third")||text.equals("Third")){
            deleteItem.setListID(deleteItem.getListarray().get(2).getKey());
        }else
        if(text.equals("4")||text.equals("four")||text.equals("Four")||text.equals("forth")||text.equals("Forth")){
            deleteItem.setListID(deleteItem.getListarray().get(3).getKey());
        }else
        if(text.equals("5")||text.equals("five")||text.equals("Five")||text.equals("fifth")||text.equals("Fifth")){
            deleteItem.setListID(deleteItem.getListarray().get(4).getKey());
        }else
        if(text.equals("last")||text.equals("final")||text.equals("end")||text.equals("recent")){
            deleteItem.setListID(deleteItem.getListarray().get(deleteItem.getListarray().size()-1).getKey());
        }else {
            commandnotreg(29);
            return;
        }
        SpeechTranscript.append("List Select.. " + deleteItem.getListname() +"--"+ deleteItem.getListID()+"\n\n");
        deleteItem.searchitem(getApplicationContext());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(deleteItem.isTaskCompleted()){
                    if(deleteItem.isItemavailable()){
                        SpeechTranscript.append("Item found" + deleteItem.getItemID()+"\n\n");
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


}