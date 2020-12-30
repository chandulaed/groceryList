package com.example.grocerylist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Speech_text extends AppCompatActivity {

    // voice_codes { first command 1   ;
    TextView SpeechTranscript;
    NewList newList;
    private TextToSpeech tts;
    private String transtript;
    private boolean ttsIsInitialized;
    Handler handler = new Handler();
    NewItem newItem;
    EditItem editItem;



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
        }, 3000);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            switch (requestCode) {
            //first command
                case 1: {
                    Tokenizer commandOne = new Tokenizer(result.get(0));
                    if (commandOne.getCommand().equals("create") || commandOne.getCommand().equals("Create")) {
                        if(commandOne.getObject()==""||commandOne.getObject()==null){
                            noname();
                        }else {
                            newList = new NewList(commandOne.getObject());
                            SpeechTranscript.append("Please wait... \n\n");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int avail = newList.getisListavailable();
                                    if(avail==0){
                                        cantconetdatabase(1);
                                    }else
                                    if (avail == 5) {
                                        transtript = "Sorry!, List is already available, please try another name..";
                                        Speak(transtript);
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                Ask("try again", 1);
                                            }
                                        }, 4000);
                                    } else {
                                        transtript = "Do you want to create a list named," + newList.getListName();
                                        Speak(transtript);
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                Ask("Confirm the create", 2);
                                            }
                                        }, 3000);
                                    }
                                }
                            }, 5000);
                        }
                    } else if (commandOne.getCommand().equals("add") || commandOne.getCommand().equals("Add")) {
                        if(commandOne.getObject()==""||commandOne.getObject()==null){
                            noname();
                        }else {
                            newItem = new NewItem(commandOne.getObject());
                            Speak("What is the list name you need to add, " + commandOne.getObject());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("What the list name you need to add", 3);
                                }
                            }, 2500);
                        }
                    }else if(commandOne.getCommand().equals("edit") || commandOne.getCommand().equals("Edit")){
                        if(commandOne.getObject()==""||commandOne.getObject()==null){
                            noname();
                        }else {
                            editItem = new EditItem(commandOne.getObject());
                            Speak("What is the list you need edit, " + editItem.getItemName());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Ask("What the list you need to edit", 7);
                                }
                            }, 2500);
                        }
                    }else if(commandOne.getCommand().equals("cancel") || commandOne.getCommand().equals("close")){
                        newList=null;
                        cancelProcess("Thank you, cancelling the process");
                    }else{
                        commandnotreg(1);
                    }
                    break;
                }


                case 2: {
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    Tokenizer listCreateConfirm = new Tokenizer(result.get(0));
                    if (listCreateConfirm.getCommand().equals("yes")||listCreateConfirm.getCommand().equals("ok")) {
                        Date currentTime = new Date();
                        SpeechTranscript.append("confirming........\n\n");
                        newList.setListID(currentTime.toString());
                        newList.addList(getApplicationContext());
                        Speak("list is Successfully created");
                        newList=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }
                    else
                        if (listCreateConfirm.getCommand().equals("no")||listCreateConfirm.getCommand().equals("cancel")||listCreateConfirm.getCommand().equals("close")){
                            newList=null;
                            cancelProcess("Cancelling Create list\n Thank You");
                        }
                        else {
                            commandnotreg(2);
                        }
                    break;
                }

                case 3: {
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(3);
                    }
                    else {
                        newItem.searchList(result.get(0));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(newItem.getListavailable() == 0){
                                    cantconetdatabase(3);
                                } else
                                if (newItem.getListavailable() == 5) {
                                    SpeechTranscript.append("List Found - list-Id ---\n" + newItem.getListID() + "\n\n");
                                    newItem.searchitem();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!!(newItem.getItemavailable()==5)){
                                                Speak("How much you need to add ?");
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Ask("How much you need to add ?", 4);
                                                    }
                                                }, 3000);
                                            }else{
                                                cancelProcess("Item is Already Available !");
                                            }
                                        }
                                    },5000);
                                } else {
                                    Speak("Sorry list cannot be found please try again");
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Ask("Sorry list cannot be fond please try again", 3);
                                        }
                                    }, 2000);
                                }
                            }
                        }, 5000);
                    }
                    break;
                }

                case 4: {
                    SpeechTranscript.append(result.get(0)+"\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(4);
                    }else {
                        newItem.setItemQty(result.get(0));
                        Speak("Is there any special place to buy ?");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Ask("Is there any special place to buy", 5);
                            }
                        }, 2500);
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
                    }else if(!(result.get(0).equals("no")||result.get(0).equals("not"))) {
                        newItem.setItemLoc(result.get(0));
                        Speak("do you need to add," + newItem.getItemQty() +", "+ newItem.getItemName() + ", from " + newItem.getItemLoc() + ", to list " + newItem.getlistname());
                    }else{
                        newItem.setItemLoc("no");
                        Speak("do you need to add," + newItem.getItemQty() +", "+ newItem.getItemName() + ", to list" + newItem.getlistname());
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Ask("Confirm Item",6);
                        }
                    },4000);
                    break;
                }

                case 6:{
                    SpeechTranscript.append(result.get(0)+"\n\n");
                    if (result.get(0).equals("yes")||result.get(0).equals("ok")) {
                        SpeechTranscript.append("confirming........\n\n");
                        newItem.additem(getApplicationContext());
                        Speak("Item Successfully added");
                        newList=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }
                    else
                    if (result.get(0).equals("no")||result.get(0).equals("cancel")){
                        Speak("Cancelling add item\n Thank You");
                        newList=null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }
                    else {
                        commandnotreg(6);
                    }
                    break;
                }

                case 7:{
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        editItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")) {
                        commandnotreg(7);
                    }else {
                        editItem.searchList(result.get(0));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(editItem.getListavailable()==0){
                                    cantconetdatabase(7);
                                }
                                else if (editItem.getListavailable() == 5) {
                                    SpeechTranscript.append("List Found - listid --- " + editItem.getListID() + "\n\n");
                                    editItem.searchitem();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (editItem.getItemavailable() == 5) {
                                                SpeechTranscript.append("Item Found - ItemKey --- " + editItem.getItemkey() + "\n\n");
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Speak("What you need to edit ?");
                                                        Ask("What you need to edit ?", 8);
                                                    }
                                                }, 2000);
                                            } else {
                                                Speak("Sorry item cannot be found please try again");
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Ask("Sorry item cannot be found please add the item", 1);
                                                    }
                                                }, 2000);
                                            }
                                        }
                                    }, 5000);
                                } else {
                                    Speak("Sorry list cannot be find please try again");
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Ask("Sorry list cannot be find please try again", 7);
                                        }
                                    }, 2000);
                                }
                            }
                        }, 5000);
                    }
                    break;
                }
                case 8: {
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
                        commandnotreg(4);}
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
                        commandnotreg(4);}
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

                case 13:{
                    SpeechTranscript.append(result.get(0) + "\n\n");
                    if(result.get(0).equals("cancel")||result.get(0).equals("close")){
                        newItem = null;
                        cancelProcess("Cancelling Add Item\n Thank You");
                    }
                    else if(result.get(0).equals("")){
                        commandnotreg(4);}
                    else {
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


            }

        }


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
        Speak("Cannot recognize the command, try again");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Ask("Cannot connect to the Database, try again", commandno);
            }
        }, 2500);
    }



}