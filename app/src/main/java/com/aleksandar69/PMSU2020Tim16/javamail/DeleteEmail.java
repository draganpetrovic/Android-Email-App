package com.aleksandar69.PMSU2020Tim16.javamail;

import android.content.Context;
import android.os.AsyncTask;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;

public class DeleteEmail extends AsyncTask<Void, Void, Void> {

    //String user = "mindsnackstore@gmail.com";
    String user = "clockworkaleks@gmail.com";
    String password = "TooStronk69!";
    String storeType = "imaps";
    Context mContext;
    int messageIdfromDB;

    public DeleteEmail(Context context, int messageId) {
        this.mContext = context;
        this.messageIdfromDB = messageId;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");

            Session emailSession = Session.getDefaultInstance(props);
            Store store = emailSession.getStore(storeType);

            store.connect("imap.googlemail.com", user, password);

            Folder inboxFolder = store.getFolder("INBOX");
            UIDFolder uf = (UIDFolder) inboxFolder;
            inboxFolder.open(Folder.READ_WRITE);

            Message[] messages = inboxFolder.getMessages();
            MessagesDBHandler dbHandler = new MessagesDBHandler(mContext);

            List<com.aleksandar69.PMSU2020Tim16.models.Message> messagesPulled = dbHandler.queryAllMessages(Data.account.get_id());
            com.aleksandar69.PMSU2020Tim16.models.Message[] listMessages = new com.aleksandar69.PMSU2020Tim16.models.Message[messagesPulled.size()];
            listMessages = messagesPulled.toArray(listMessages);

            ArrayList<Integer> listInt = new ArrayList<Integer>();

            for (int j = 0; j < listMessages.length; j++) {
                com.aleksandar69.PMSU2020Tim16.models.Message messagefromDB = listMessages[j];
                int idMess = messagefromDB.get_id();
                listInt.add(idMess);
            }

            for (int i = 0; i < messages.length; i++) {
                Message messageInb = messages[i];
                int messageid = (int) uf.getUID(messageInb);

                if (messageid == messageIdfromDB) {

                    messageInb.setFlag(Flags.Flag.DELETED, true);

                }
            }
            inboxFolder.close(false);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}