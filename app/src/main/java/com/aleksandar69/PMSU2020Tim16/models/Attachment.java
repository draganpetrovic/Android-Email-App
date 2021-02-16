package com.aleksandar69.PMSU2020Tim16.models;

public class Attachment {
    private int _id;
    private String content;
    private String fileName;
    private int messageId;


    public Attachment(String content, String fileName){
        this.content = content;
        this.fileName = fileName;
    }

    public Attachment(String content, String fileName, int messageId){
        this.content = content;
        this.fileName = fileName;
        this.messageId = messageId;
    }

    public  Attachment(){}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
