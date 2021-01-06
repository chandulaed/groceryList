package com.example.grocerylist;

public class ListStruct {
    private String ListName;
    private  String ListID;
    private String dateCreated;

    public String getListName() {
        return ListName;
    }

    public void setListName(String listName) {
        ListName = listName;
    }

    public String getListID() {
        return ListID;
    }

    public void setListID(String listid) {
        ListID = listid;
    }

    public ListStruct() {
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ListStruct(String listName, String listID, String dateCreated) {
        ListName = listName;
        ListID = listID;
        this.dateCreated = dateCreated;
    }
}
