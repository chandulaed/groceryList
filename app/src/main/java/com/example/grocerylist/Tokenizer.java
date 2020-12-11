package com.example.grocerylist;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Tokenizer {
        private String voice;
        private ArrayList<String> words= new ArrayList<>();

    public Tokenizer(String voice) {
        this.voice = voice;
        StringTokenizer tokens = new StringTokenizer(this.voice);
        while(tokens.hasMoreTokens()){
            words.add(tokens.nextToken());
        }
    }

    public ArrayList<String> getWords() {
        return words;
    }
}
