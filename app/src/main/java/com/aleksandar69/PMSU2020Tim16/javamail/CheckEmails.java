package com.aleksandar69.PMSU2020Tim16.javamail;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.sun.mail.pop3.POP3Store;

import org.apache.harmony.awt.internal.nls.Messages;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class CheckEmails extends AsyncTask<Void, Void, Void> {

    String user = "clockworkaleks@gmail.com";
    String password = "TooStronk69!";
    String storeType = "pop3";
    private Context mContext;

    public CheckEmails(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        try {

            Properties properties = new Properties();
            properties.put("mail.pop3.host", "pop.gmail.com");
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            properties.put("mail.pop3.ssl.enable", "true");

            Session emailSession = Session.getDefaultInstance(properties);


            Store store = emailSession.getStore("pop3s");

            store.connect("pop.gmail.com", user, password);

            Folder inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);

            Message[] messages = inboxFolder.getMessages();

            for (int i = 0; i < messages.length; i++) {
                Message messageInb = messages[i];

                com.aleksandar69.PMSU2020Tim16.models.Message message = new com.aleksandar69.PMSU2020Tim16.models.Message();

                message.setSubject(messageInb.getSubject());
                message.setFrom(messageInb.getFrom()[0].toString());
                message.setContent(messageInb.getContent().toString());

                MessagesDBHandler dbHandler = new MessagesDBHandler(mContext);

                dbHandler.addMessage(message);

            }
            inboxFolder.close(false);
            store.close();

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
