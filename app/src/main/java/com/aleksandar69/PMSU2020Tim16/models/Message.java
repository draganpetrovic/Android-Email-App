package com.aleksandar69.PMSU2020Tim16.models;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Message {

    private int _id;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String dateTime;
    private String subject;
    private String content;
    private int logged_user_id;
    private int folder_id;
    private boolean unread;
    private List<Attachment> attachments;
    private List<Tag> tags;
    private int idOnServer;
    /*    private int attachmentId;*/

    public Message() {
        // Calendar calendar = Calendar.getInstance();
        /*Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat();
        String dateToString = format.format(curDate);
        format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        dateToString = format.format(curDate);
        dateTime = dateToString;*/
        attachments = new ArrayList<>();
        tags = new ArrayList<>();
    }



    public Message(String from, String to, String cc, String bcc, String subject, String content) {
/*
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat();
        String dateToString = format.format(curDate);
        format = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
        dateToString = format.format(curDate);
        dateTime = dateToString;
*/
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.content = content;
        attachments = new ArrayList<>();
        tags = new ArrayList<>();

    }


    public Message(String to, String cc, String bcc, String subject, String content) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.content = content;
        attachments = new ArrayList<>();
        tags = new ArrayList<>();
    }

    class SortByDateAsc implements Comparator<Message> {
        public int compare(Message a, Message b) {
            Date date1 = fromUTC(a.getDateTime());
            Date date2 = fromUTC(b.getDateTime());
            return date1.compareTo(date2);
        }
    }

    class SortByDateDesc implements Comparator<Message> {
        public int compare(Message a, Message b) {
            Date date1 = fromUTC(a.getDateTime());
            Date date2 = fromUTC(b.getDateTime());
            return date2.compareTo(date1);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "\nTO: " + to + "\nFROM: " + from + "\nCC: " + cc + "\nBCC: " + bcc + "\nSUBJECT: " + subject + "\nCONTENT: " + content + "\n Date: " + dateTime;
    }

    public int getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(int idOnServer) {
        this.idOnServer = idOnServer;
    }

    public static Date fromUTC(String dateStr) {
        //TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        //df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void addAttachments(Attachment attachment) {
        this.attachments.add(attachment);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

/*    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }*/

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubject() {
        return subject;
    }

    public int getLogged_user_id() {
        return logged_user_id;
    }

    public void setLogged_user_id(int logged_user_id) {
        this.logged_user_id = logged_user_id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFolder_id() { return folder_id;
    }

    public void setFolder_id(int folder_id) { this.folder_id = folder_id;}
}
