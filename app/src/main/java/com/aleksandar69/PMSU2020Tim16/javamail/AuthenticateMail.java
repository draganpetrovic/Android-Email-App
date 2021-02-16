package com.aleksandar69.PMSU2020Tim16.javamail;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aleksandar69.PMSU2020Tim16.models.Contact;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class AuthenticateMail extends AsyncTask<Void, Void, Boolean> {

    String imapHOst;
    String eMail;
    String password;
    Store store;
    public Boolean isconn;
    public interface AsyncResponse {
        void processFinish(Boolean isConnected);
    }

    public AsyncResponse delegate = null;


    public AuthenticateMail(String imapHost, String email, String password, AsyncResponse delegate) {
        this.imapHOst = imapHost;
        this.eMail = email;
        this.password = password;
        this.delegate = delegate;

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        if (android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.host", imapHOst);
        props.setProperty("mail.imap.starttls.enable", "true");
        props.setProperty("mail.imap.ssl.enable", "true");

        Session emailSession = Session.getDefaultInstance(props);
        store = null;
        try {
            store = emailSession.getStore("imaps");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }


        try {
            store.connect(imapHOst, eMail, password);
        } catch (MessagingException e) {

            return false;
        }

        isconn = store.isConnected();

        Log.d("isconnectedAuthenticate", isconn.toString());

        return true;

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }
}
