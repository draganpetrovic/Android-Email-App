package com.aleksandar69.PMSU2020Tim16;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.preference.PreferenceManager;

import com.aleksandar69.PMSU2020Tim16.models.Account;

public class Data {

    public static String syncTime;
    public static Boolean allowSync;
    public static String prefSort;

    public static String userId = "useridKey";
    public static String userEmail = "useremailKey";
    public static String userPassworrd = "userpasswordKey";
    public static Account account;


    public static final String MESS_ID_EXTRA = "messId";
    public static final String EMAIL_FORWARD_EXTRA = "forwardextra";
    public static final String FORWARD_FROM_EXTRA = "forwardfrom";
    public static final String FORWARD_SUBJECT_EXTRA = "forwardsubject";
    public static final String FORWARD_CC_EXTRA = "forwardcc";
    public static final String FORWARD_DATE_EXTRA = "forwarddate";
    public static final String FORWARD_TO_EXTRA = "forwardto";
    public static boolean isForward = false;
    public static final String REPLY_FROM = "replyfrom";
    public static final String REPLY_CONTENT = "replycontent";
    public static boolean isReply = false;
    public static final String REPLY_TO_ALL_FROM = "replytoallfrom";
    public static final String REPLY_TO_ALL_TO = "replytoallto";
    public static final String REPLY_TO_ALL_CONTENT = "replytoallcontent";
    public static boolean isReplyToAll = false;

    public static int totalEmailsServer;
    public static int totalEmailsDB;
    //contacts
    public static final String CONTACT_ID_EXTRA = "contactID";
    public static final String TABLE_CONTACTS = "CONTACT";

    public static  final String FOLDERS_ID_EXTRA = "folder_id";

    public static String profileImageFilePath = null;
    public static Bitmap bitmap = null;
    public static String currentImage = null;

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
