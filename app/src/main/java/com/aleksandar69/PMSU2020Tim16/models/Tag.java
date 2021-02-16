package com.aleksandar69.PMSU2020Tim16.models;

public class Tag {

    private int _id;
    private String name;
    private int messageId;

    public Tag() {
    }

    public Tag(int id, String name) {
        this._id = id;
        this.name = name;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
