package com.aleksandar69.PMSU2020Tim16.javamail;


import android.os.AsyncTask;
import android.util.Log;

import com.aleksandar69.PMSU2020Tim16.models.Account;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail extends AsyncTask<Void, Void, Void> {

    public static String myeMail = "mindsnackstore@gmail.com";
    public static String myUsername = "MindSnack";
    public static String myPassword = "TooStronk69!";
    public static String subject;
    public static String content;
    private String myToList;
    private String myCCList;
    private String myBCCList;
    private StringBuffer tagList;
    Account mAccount;
    private static final String MAIL_SERVER = "smtp";
    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;


    public SendEmail(String subject, String content, String myCC, String myBCC, String myTo, String tags, Account account) {
        SendEmail.subject = subject;
        SendEmail.content = content;
        this.myCCList = myCC;
        this.myBCCList = myBCC;
        this.myToList = myTo;
        tagList = new StringBuffer();
        tagList.append(tags);
        mAccount = account;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        Properties props = new Properties();

        props.put("mail.smtp.host", mAccount.getSmtphost());
        props.put("mail.smtp.socketFactory.port", mAccount.getSmtpPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mAccount.getSmtpPort());

        Session sess = Session.getInstance(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mAccount.geteMail(), mAccount.getPassword());
            }
        });

        Message message = new MimeMessage(sess);

        List<String> ccListunConv = Arrays.asList(myCCList.split(";[ ]*"));
        String[] ccList = new String[ccListunConv.size()];
        ccList = ccListunConv.toArray(ccList);

        List<String> bccListunConv = Arrays.asList(myBCCList.split(";[ ]*"));
        String[] bccList = new String[bccListunConv.size()];
        bccList = bccListunConv.toArray(bccList);

        List<String> toListunConv = Arrays.asList(myToList.split(";[ ]*"));
        String[] toList = new String[toListunConv.size()];
        toList = toListunConv.toArray(toList);


        try {

            if(!(toListunConv.contains(""))){
                InternetAddress[] toAddress = new InternetAddress[toList.length];

                for(int i = 0; i < toList.length; i++){
                    toAddress[i] = new InternetAddress(toList[i]);
                }
                for(int i = 0; i < toAddress.length; i++){
                   message.addRecipient(Message.RecipientType.TO, toAddress[i]);
                }

            }


            if(!(ccListunConv.contains(""))) {
                InternetAddress[] cCAddress = new InternetAddress[ccList.length];

                for (int i = 0; i < ccList.length; i++) {
                    cCAddress[i] = new InternetAddress(ccList[i]);
                }

                for (int i = 0; i < cCAddress.length; i++) {
                    message.addRecipient(Message.RecipientType.CC, cCAddress[i]);
                }
            }
            if(!(bccListunConv.contains(""))) {
                InternetAddress[] bCCAddress = new InternetAddress[bccList.length];

                for (int i = 0; i < bccList.length; i++) {
                    bCCAddress[i] = new InternetAddress(bccList[i]);
                }

                for (int i = 0; i < bCCAddress.length; i++) {
                    message.addRecipient(Message.RecipientType.BCC, bCCAddress[i]);
                }

            }


            message.setFrom(new InternetAddress(mAccount.geteMail()));
           // message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            if(!tagList.toString().isEmpty()) {
                message.setText(content + "\n\n----------\nTAGS: " + tagList);
            }
            else{
                message.setText(content);
            }
            //Transport.send(message);

            Transport transport = sess.getTransport(MAIL_SERVER);
            transport.connect(mAccount.getSmtphost() ,Integer.parseInt(mAccount.getSmtpPort()), mAccount.geteMail(), mAccount.getPassword());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();

        }
        return null;
    }
/*    public static Message prepareMessage(Session session, String myEmail, String recepient, String subject, String content){
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(myeMail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject(subject);
            message.setText(content);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }*/

}
