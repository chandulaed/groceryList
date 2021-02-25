package com.example.grocerylist;

public class ItemStruct {
    private String itemName;
    private String itemQty;
    private String itemLocation;
    private String cost;
    public String collected;


    public ItemStruct() {
    }

    public ItemStruct(String name) {
        this.itemName=name;
        this.collected= "False";
        this.cost="0";
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQty() {
        return itemQty;
    }

    public void setItemQty(String itemQty) {
        this.itemQty = itemQty;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCollected() {

        return collected;
    }

    public void setCollected(String collected) {
        this.collected = collected;
    }
}
