package com.example.grocerylist;
import android.text.TextUtils;

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


    public  int classifier(){
        String[] create ={"create","build","conceive","constitute","construct","design","devise","discover","establish","forge","form","found","generate","initiate","invent","make","organize"," plan"," produce","set up","shape","spawn","start"};
        String[] edit = {"edit","alter","different", "change","adjust"," adapt", "turn","amend", "improve","modify", "convert", "revise", "recast", "reform", "reshape", "refashion", "redesign", "restyle", "revamp", "rework", "remake", "remodel", "remould", "redo", "reconstruct", "reorganize", "reorder", "refine", "reorient", "reorientate", "transform", "transfigure", "evolve"};
        String[] delete = {"remove", "cut", "excise", "unpublish","wipe","delete","destroy","Delete"};
        String[] add = {"attach", "put", "append", "adjoin", "join", "affix", "insert","place","push", "load", "fit","add","insert"};

        for (String word: create){
            if(this.command.matches(word)){
                return 1;
            }
        }
        for (String word: edit){
            if(this.command.matches(word)){
                return 2;
            }
        }
        for (String word: delete){
            if(this.command.matches(word)){
                return 3;
            }
        }
        for (String word: add){
            if(this.command.matches(word)){
                return 4;
            }
        }
        return 0;
    }

}
