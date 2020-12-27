package com.example.grocerylist;
import android.app.usage.NetworkStats;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.api.client.util.Lists;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.io.FileInputStream;
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
}
