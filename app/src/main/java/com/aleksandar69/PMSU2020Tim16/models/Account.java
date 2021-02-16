package com.aleksandar69.PMSU2020Tim16.models;

import androidx.annotation.NonNull;

public class Account {

    private int _id;
    private String smtpPort;
    private String port;
    private String username;
    private String password;
    private String displayName;
    private String eMail;
    private String smtphost;
    private String imapHost;
    private String imageBitmap;

    public Account() {

    }

    public Account(String smtpAddress, String port, String username, String password, String displayName, String eMail, String smtphost, String imapHost){
        this.eMail = eMail;
        this.smtpPort = smtpAddress;
        this.port = port;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.smtphost = smtphost;
        this.imapHost = imapHost;
    }

    public String geteMail() {
        return eMail;
    }

    public String getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(String imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSmtphost() {
        return smtphost;
    }

    public void setSmtphost(String smtphost) {
        this.smtphost = smtphost;
    }

    public String getImapHost() {
        return imapHost;
    }

    public void setImapHost(String imapHost) {
        this.imapHost = imapHost;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Username:" + username + "E-mail" + eMail + "Password:" + password;
    }
}
