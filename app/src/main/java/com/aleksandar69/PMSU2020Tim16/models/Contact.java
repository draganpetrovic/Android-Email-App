package com.aleksandar69.PMSU2020Tim16.models;

import com.aleksandar69.PMSU2020Tim16.R;
import com.google.gson.annotations.SerializedName;

public class Contact {
    private int _id;
    private String first;
    private String last;
    private String display;
    private String email;
    private Integer imageSourceID;
    private String url;
    private byte[] image;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Contact(String first){
        this.first = first;
    }

    public void setLast(String last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "_id=" + _id +
                ", first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", display='" + display + '\'' +
                ", email='" + email + '\'' +
                ", imageSourceID=" + imageSourceID +
                '}';
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageSourceID(Integer imageSourceID) {
        this.imageSourceID = imageSourceID;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getLast() {
        return last;
    }

    public Integer getImageSourceID() {
        return imageSourceID;
    }

    public String getFirst() {
        return first;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplay() {
        return display;
    }

    public Contact(String first, String last, String display, String email, Integer imageSourceID) {
        this.first = first;
        this.last = last;
        this.display = display;
        this.email = email;
        this.imageSourceID = imageSourceID;
    }

    public Contact(String first, String last, String display, String email, byte[] image) {
        this.first = first;
        this.last = last;
        this.display = display;
        this.email = email;
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Contact(int id, String first, String last, String display, String email, byte[] image) {
        _id = id;
        this.first = first;
        this.last = last;
        this.display = display;
        this.email = email;
        this.image = image;
    }


    public Contact(int id, String first, String last, String display, String email) {
        this._id = id;
        this.first = first;
        this.last = last;
        this.display = display;
        this.email = email;
       // this.imageSourceID = imageSourceID;
    }


    public Contact(String first, String last, String display, String email) {
        this.first = first;
        this.last = last;
        this.display = display;
        this.email = email;
    }

    public Contact(){

    }
/*
    public static final Contact[] contacts = {
      new Contact("Elena", "Krunic", "Elena Krunic", "elenakrunic@gmail.com", R.mipmap.ic_launcher_round),
      new Contact("Vincent", "Andolini", "Vincent Andolini", "vincent@gmail.com", R.mipmap.ic_launcher_round),
      new Contact("Mico", "Micic", "Mico Micic", "mico@gmail.com",  R.mipmap.ic_launcher_round)
    };

*/
}
