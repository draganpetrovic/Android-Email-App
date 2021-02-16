package com.aleksandar69.PMSU2020Tim16.javamail;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.aleksandar69.PMSU2020Tim16.models.Account;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMultipartMailConcurrent {
    public static String subject;
    public static String content;
    private List<String> filePath;
    Context mContext;
    private String myToList;
    private String myCCList;
    private String myBCCList;
    private StringBuffer tagList;
    private static final String MAIL_SERVER = "smtp";
    Account account;

    public SendMultipartMailConcurrent(Context context, String subject, String content, List<String> filePath, String myCC, String myBCC, String myTo, String tags, Account account) {
        this.subject = subject;
        this.content = content;
        this.filePath = filePath;
        mContext = context;
        this.myCCList = myCC;
        this.myBCCList = myBCC;
        this.myToList = myTo;
        tagList = new StringBuffer();
        tagList.append(tags);
        this.account = account;
    }

    public void Send() {


        List<Thread> threads = new ArrayList<Thread>();

        ExecutorService executor = Executors.newFixedThreadPool(1);

        for (int i = 0; i < 1; i++) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    Properties props = new Properties();

                    props.put("mail.smtp.host", account.getSmtphost());
                    props.put("mail.smtp.socketFactory.port", account.getSmtpPort());
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", account.getSmtpPort());
                    Session sess = Session.getInstance(props, new Authenticator() {

                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(account.geteMail(), account.getPassword());
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

                    Log.d("ToLength", String.valueOf(toList.length));


                    try {

                        if (!(toListunConv.contains(""))) {
                            InternetAddress[] toAdress = new InternetAddress[toList.length];

                            for (int i = 0; i < toList.length; i++) {
                                toAdress[i] = new InternetAddress(toList[i]);
                                Log.d("address:", toList[i].toString());
                            }
                            for (int i = 0; i < toAdress.length; i++) {
                                Log.d("addressAdress", toAdress[i].toString());
                                message.addRecipient(Message.RecipientType.TO, toAdress[i]);
                            }

                        }


                        if (!(ccListunConv.contains(""))) {
                            InternetAddress[] cCAddress = new InternetAddress[ccList.length];

                            for (int i = 0; i < ccList.length; i++) {
                                cCAddress[i] = new InternetAddress(ccList[i]);
                            }

                            for (int i = 0; i < cCAddress.length; i++) {
                                message.addRecipient(Message.RecipientType.CC, cCAddress[i]);
                            }
                        }
                        if (!(bccListunConv.contains(""))) {
                            InternetAddress[] bCCAddress = new InternetAddress[bccList.length];

                            for (int i = 0; i < bccList.length; i++) {
                                bCCAddress[i] = new InternetAddress(bccList[i]);
                            }

                            for (int i = 0; i < bCCAddress.length; i++) {
                                message.addRecipient(Message.RecipientType.BCC, bCCAddress[i]);
                            }

                        }

                        message.setFrom(new InternetAddress(account.geteMail()));
                        //    message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                        message.setSubject(subject);

                        BodyPart messageBodyPart = new MimeBodyPart();

                        Multipart multiPart = new MimeMultipart();

                        // dio sa tekstom poruke
                        multiPart.addBodyPart(messageBodyPart);

                        messageBodyPart.setText(content);

                        if(tagList != null || !(tagList.length() < 1)) {
                            MimeBodyPart tagBodyPart = new MimeBodyPart();
                            tagBodyPart.setText("\n\n\nTAGS: " + tagList.toString());
                            tagBodyPart.setContentID("11");
                            multiPart.addBodyPart(tagBodyPart);
                        }

                        //attachment

                        for (int i = 0; i < filePath.size(); i++) {

                            Random random = new Random();

                            String ranint = String.valueOf(random.nextInt());

                            File file = new File(filePath.get(i));
                            saveFile(encodeFileToBase64Binary(filePath.get(i)), "/" + ranint + "tempBase64.txt");
                            String encoded = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ranint + "tempBase64.txt";

                            if (!(encoded.isEmpty())) {
                                MimeBodyPart messageBodyPart2 = new MimeBodyPart();

                                messageBodyPart2.attachFile(encoded);

                                messageBodyPart2.setDisposition(Part.ATTACHMENT);
                                messageBodyPart2.setFileName(file.getName());
                                multiPart.addBodyPart(messageBodyPart2);

                            }
                        }


                        message.setContent(multiPart);
                        message.saveChanges();

                        Log.d("VELICINA", String.valueOf(filePath.size()));


                        Transport transport = sess.getTransport(MAIL_SERVER);
                        transport.connect(account.getSmtphost(), Integer.parseInt(account.getSmtpPort()), account.geteMail(), account.getPassword());
                        transport.sendMessage(message, message.getAllRecipients());
                        transport.close();


                    } catch (MessagingException |
                            IOException e) {
                        e.printStackTrace();
                    }
                }


                private void saveFile(String base64file, String fileName) {
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        if (Build.VERSION.SDK_INT > 23) {
                            if (checkPermission()) {
                                File storage = Environment.getExternalStorageDirectory();
                                File dir = new File(storage.getAbsolutePath());
                                dir.mkdir();
                                File file = new File(dir, fileName);
                                FileOutputStream os = null;
                                try {
                                    os = new FileOutputStream(file);
                                    os.write(base64file.getBytes());
                                    os.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            };
            Thread worker = new Thread(runnable);
            worker.setName("Thread fetch " + String.valueOf(i));
            worker.start();
            threads.add(worker);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    private String encodeFileToBase64Binary(String fileName)
            throws IOException {

        File file = new File(fileName);
        byte[] bytes = loadFile(file);
        byte[] encoded = (Base64.encodeBase64(bytes));
        String encodedString = new String(encoded);

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // Toast.makeText(Context, "File too large",Toast.LENGTH_LONG);
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }


}