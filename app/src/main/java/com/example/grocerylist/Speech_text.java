package com.example.grocerylist;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Speech_text extends AppCompatActivity {

    // voice_codes { first command 1   ;
    TextView SpeechTranscript;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_text);
        SpeechTranscript=findViewById(R.id.speech_transcript);
        speech("Hello what do you want to do ?",1);


    }

    private void speech(String command,int voice_code){
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
        switch (requestCode){
            //first command
            case 1:{
                if(resultCode==RESULT_OK && data!=null){
                    ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    SpeechTranscript.append(result.get(0)+"\n\n");
                    Tokenizer commandOne = new Tokenizer(result.get(0));
                    if(commandOne.getWords().get(0).equals("create")||commandOne.getWords().get(0).equals("Create")){
                        SpeechTranscript.append("Do you want to create a list named " + commandOne.getWords().get(1)+"\n\n");
                        //speech("confirm the create",2);
                    }
                }

            }
        }
    }
}