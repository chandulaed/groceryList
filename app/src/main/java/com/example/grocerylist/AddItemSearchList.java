package com.example.grocerylist;

public class AddItemSearchList {

    private String name;
    private String date;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AddItemSearchList() {
    }

    public AddItemSearchList(String name, String date, String key) {
        this.name = name;
        this.date = date;
        this.key = key;
    }
}
