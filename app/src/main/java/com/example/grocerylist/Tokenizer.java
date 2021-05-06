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
        String[] create ={"attach", "put", "append", "adjoin", "join", "affix", "insert","place","push", "load", "fit","add","insert","create","build","conceive","constitute","construct","design","devise","discover","establish","forge","form","found","generate","initiate","invent","make","organize"," plan"," produce","set up","shape","spawn","start"};
        String[] edit = {"edit","alter","different", "change","adjust"," adapt", "turn","amend", "improve","modify", "convert", "revise", "recast", "reform", "reshape", "refashion", "redesign", "restyle", "revamp", "rework", "remake", "remodel", "remould", "redo", "reconstruct", "reorganize", "reorder", "refine", "reorient", "reorientate", "transform", "transfigure", "evolve"};
        String[] delete = {"remove", "cut", "excise", "unpublish","wipe","delete","destroy","Delete"};

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
        return 0;
    }

    public static boolean collective(String word){

        String[] cnoun = {"assortment","basket","block","bed","bowl","box","bunch","bushel","bushel","cast","clutch","cluster","comb","cube","jam","jar","loaf","kilo","gram","kilogram","milligram","piece","piece","packet","pack","plater","punnet","sheaf","slice","shock","shoulder","pods","lot of","bottle","can","carton","cup","glass","jug","liter","milliliter","bag","bushel of","bundle of","hill of","packet of","pod of","rope of","sheaf of","troop of"};

        for(String noun:cnoun){
            if(word.contains(noun)){
                return true;
            }
        }
        return false;
    }

}
