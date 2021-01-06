package com.example.grocerylist;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Tokenizer {
    private String voice;
    private String command;
    private String object;

    public Tokenizer(String voice) {
        ArrayList<String> words = new ArrayList<>();
        this.voice = voice;
        StringTokenizer tokens = new StringTokenizer(this.voice);
        command=tokens.nextToken();
        while (tokens.hasMoreTokens()) {
            words.add(tokens.nextToken());
        }
        object = TextUtils.join(" ",words);
    }

    public String getCommand(){
        return command;
    }

    public String getObject() {
        return object;
    }

    public int Sentiment(Context context) {

        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder().setContent(voice).setType(Document.Type.PLAIN_TEXT).build();
            AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
            Sentiment sentiment = response.getDocumentSentiment();
            if (sentiment == null) {
                return 0;
            } else {
                if (sentiment.getScore() < 0) {
                    return 0;
                } else {
                    return 1;
                }
            }
        } catch (IOException e) {
            Toast tff = Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG);
            tff.show();

        }
        return 0;
    }

    public  int classifier(){
        String[] create ={"create","build","conceive","constitute","construct","design","devise","discover","establish","forge","form","found","generate","initiate","invent","make","organize"," plan"," produce","set up","shape","spawn","start"};
        String[] edit = {"edit","alter","different", "change","adjust"," adapt", "turn","amend", "improve","modify", "convert", "revise", "recast", "reform", "reshape", "refashion", "redesign", "restyle", "revamp", "rework", "remake", "remodel", "remould", "redo", "reconstruct", "reorganize", "reorder", "refine", "reorient", "reorientate", "transform", "transfigure", "evolve"};
        String[] delete = {"remove", "cut", "excise", "unpublish","wipe","delete","destroy","Delete"};
        String[] add = {"attach", "put", "append", "adjoin", "join", "affix", "insert","place","push", "load", "fit","add","insert"};

        for (String word: create){
            if(this.command.equals(word)){
                return 1;
            }
        }
        for (String word: edit){
            if(this.command.equals(word)){
                return 2;
            }
        }
        for (String word: delete){
            if(this.command.equals(word)){
                return 3;
            }
        }
        for (String word: add){
            if(this.command.equals(word)){
                return 4;
            }
        }
        return 0;
    }

}
