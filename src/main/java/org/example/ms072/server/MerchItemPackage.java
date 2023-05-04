package org.example.ms072.server;

import java.util.List;
import java.util.ArrayList;

import  org.example.ms072.client.inventory.Item;

public class MerchItemPackage {

    private long lastsaved;
    private int mesos = 0;
    private int packageid;
    private List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setSavedTime(long lastsaved) {
        this.lastsaved = lastsaved;
    }

    public long getSavedTime() {
        return lastsaved;
    }

    public int getMesos() {
        return mesos;
    }

    public void setMesos(int set) {
        mesos = set;
    }

    public int getPackageid() {
        return packageid;
    }

    public void setPackageid(int packageid) {
        this.packageid = packageid;
    }
}
