package com.aleksandar69.PMSU2020Tim16.models;

public class Photo {
    private int _id;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //dodat parametar name zbog ucitavanja slike,obrisati poslije
    private String name;

    public Photo() {
    }

    public Photo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Photo(int id, String path) {
        this._id = id;
        this.path = path;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "_id=" + _id +
                ", path='" + path + '\'' +
                '}';
    }
}
