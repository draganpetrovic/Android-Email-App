package com.aleksandar69.PMSU2020Tim16.models;

public class Folder {

    private int id;
    private String name;

    public Folder(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Folder(String name){
        this.name = name;
    }

    public Folder() {
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

}
