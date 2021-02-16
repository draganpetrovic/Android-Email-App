package com.aleksandar69.PMSU2020Tim16.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.activities.ContactActivity;
import com.aleksandar69.PMSU2020Tim16.database.provider.AccountsContentProvider;
import com.aleksandar69.PMSU2020Tim16.database.provider.AttachmentsContentProvider;
import com.aleksandar69.PMSU2020Tim16.database.provider.ContactsContentProvider;
import com.aleksandar69.PMSU2020Tim16.database.provider.FoldersContentProvider;
import com.aleksandar69.PMSU2020Tim16.database.provider.MessagesContentProvider;
import com.aleksandar69.PMSU2020Tim16.database.provider.RuleContentProvider;
import com.aleksandar69.PMSU2020Tim16.database.provider.TagsContentProvider;
import com.aleksandar69.PMSU2020Tim16.enums.Condition;
import com.aleksandar69.PMSU2020Tim16.enums.Operation;
import com.aleksandar69.PMSU2020Tim16.models.Account;
import com.aleksandar69.PMSU2020Tim16.models.Attachment;
import com.aleksandar69.PMSU2020Tim16.models.Contact;
import com.aleksandar69.PMSU2020Tim16.models.Message;
import com.aleksandar69.PMSU2020Tim16.models.Rule;
import com.aleksandar69.PMSU2020Tim16.models.Tag;
import com.aleksandar69.PMSU2020Tim16.models.Folder;

import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.aleksandar69.PMSU2020Tim16.Data.TABLE_CONTACTS;

public class MessagesDBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 498;
    public static final String DATABASE_NAME = "EMAILDB";

    //folders

    public static final String TABLE_FOLDERS = "FOLDERS";
    public static final String COLUMN_ID_FOLDER = "_id";
    public static final String COLUMN_NAME = "name";

    public static final String TABLE_RULE = "RULE";
    public static final String COLUMN_ID_RULE = "_id";
    public static final String COLUMN_CONDITION_TXT = "condition_txt";
    public static final String COLUMN_CONDITION_ENUM = "condition_enum";
    public static final String COLUMN_OPERATION_ENUM = "operation_enum";
    public static final String COLUMN_ID_FOLDER_FK = "folder_fk";

    //contacts
    public static final String TABLE_CONTACTS = "CONTACT";

    public static final String COLUMN_ID_CONTACTS = "_id";
    private static final String COLUMN_FIRST = "firstname";
    private static final String COLUMN_LAST = "lastname";
    private static final String COLUMN_DISPLAY = "displaytext";
    //DODATI PLAIN,HTML
    private static final String COLUMN_CONTACT_EMAIL = "emailtext";
    private static final String COLUMN_IMAGE_RESOURCE = "imageresourceid";
   // private static final String COLUMN_NEW_IMAGE = "newimage";
    //emails
    public static final String TABLE_MESSAGES = "EMAILS";

    public static final String COLUMN_ID_EMAILS = "_id";
    private static final String COLUMN_FROM = "messagefrom";
    private static final String COLUMN_TO = "messageto";
    private static final String COLUMN_CC = "cc";
    private static final String COLUMN_BCC = "bcc";
    private static final String COLUMN_DATETIME = "datetime";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_ACCOUNTS_FK = "accounts_id";
    private static final String COLUMN_ISUNREAD = "isunreadmessage";
    private static final String COLUMN_TAGS = "tagsinmail";
    private static final String COLUMN_ID_FOLDERS_FK = "folder_id";
    private static final String COLUMN_MESSAGE_ON_SERVER_ID = "message_on_server_id";

    //accounts


    public static final String TABLE_ACCOUNTS = "ACCOUNTS";

    public static final String COLUMN_ID_ACCOUNTS = "_id";
    private static final String COLUMN_SMTPADDRESS = "smptadress";
    private static final String COLUMN_PORT = "port";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_DISPLAYNAME = "displayname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SMTPHOST = "smtphost";
    private static final String COLUMN_IMAPHOST = "imaphost";
    private static final String COLUMN_BITMAP = "bitmap";

    //attachments

    public static final String TABLE_ATTACHMENTS = "ATTACHMENTS";

    public static final String COLUMN_ID_ATTACHMENTS = "_id";
    private static final String COLUMN_ATTACH_CONTENT = "content";
    public static final String COLUMN_FILENAME = "filename";
    public static final String COLUMN_EMAIL_ID_FK = "linkedemailid";

    //TAG

    public static final String TABLE_TAGS = "TAGS";
    public static final String COLUMN_ID_TAG = "_id";
    private static final String COLUMN_TAG_TEXT = "text";
    private static final String COLUMN_EMAIL_TAG_ID_FK = "emailtagfk";

    private ContentResolver myContentResolver;

    private static String CREATE_FOLDERS_TABLE = "CREATE TABLE " + TABLE_FOLDERS +
            "(" + COLUMN_ID_FOLDER + " INTEGER PRIMARY KEY, " + COLUMN_NAME + " TEXT " + ")";

    private static String CREATE_RULE_TABLE = "CREATE TABLE " + TABLE_RULE +
            "(" + COLUMN_ID_RULE + " INTEGER PRIMARY KEY, " + COLUMN_CONDITION_TXT + " TEXT, " +
            COLUMN_CONDITION_ENUM + " TEXT, " +
            COLUMN_OPERATION_ENUM + " TEXT, " + COLUMN_ID_FOLDER_FK + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_ID_FOLDER_FK + ") REFERENCES FOLDERS(_id) " + ")";

    private static String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES +
            "(" + COLUMN_ID_EMAILS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FROM + " TEXT, " + COLUMN_TO + " TEXT, " + COLUMN_CC + " TEXT, " +
            COLUMN_BCC + " TEXT, " + COLUMN_SUBJECT + " TEXT, " +
            COLUMN_CONTENT + " TEXT, " + COLUMN_DATETIME + " TEXT, " +
            COLUMN_ACCOUNTS_FK + " INTEGER, " +
            COLUMN_ID_FOLDERS_FK + " INTEGER, " +
            COLUMN_ISUNREAD + " INTEGER NOT NULL DEFAULT 1 CHECK(isunreadmessage IN (0,1)), " +
            COLUMN_TAGS + " TEXT, " + COLUMN_MESSAGE_ON_SERVER_ID + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_ID_FOLDERS_FK + ") REFERENCES FOLDERS(_id), " +
            "FOREIGN KEY(" + COLUMN_ACCOUNTS_FK + ") REFERENCES ACCOUNTS(_id) " + ")";
            //"FOREIGN KEY(" + COLUMN_ID_FOLDERS_FK + ") REFERENCES FOLDERS(_id)" +


    private static String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS +
            "(" + COLUMN_ID_ACCOUNTS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SMTPADDRESS + " TEXT, " + COLUMN_PORT + " TEXT, " +
            COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD + " TEXT, " +
            COLUMN_DISPLAYNAME + " TEXT, " + COLUMN_EMAIL + " TEXT, " +
            COLUMN_SMTPHOST + " TEXT, " + COLUMN_IMAPHOST + " TEXT, " +
            COLUMN_BITMAP + " TEXT" +
            ")";

    private static String CREATE_ATTACHMENT_TABLE = "CREATE TABLE " + TABLE_ATTACHMENTS +
            "(" + COLUMN_ID_ATTACHMENTS + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ATTACH_CONTENT + " TEXT, " + COLUMN_FILENAME + " TEXT, "
            + COLUMN_EMAIL_ID_FK + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_EMAIL_ID_FK + ") REFERENCES EMAILS(_id)" + ")";

    public static String CREATE_TAG_TABLE = "CREATE TABLE " + TABLE_TAGS +
            "(" + COLUMN_ID_TAG + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TAG_TEXT + " TEXT, " + COLUMN_EMAIL_TAG_ID_FK + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_EMAIL_TAG_ID_FK + ") REFERENCES EMAILS(_id)" + ")";


    private static String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS +
            "(" + COLUMN_ID_CONTACTS + " INTEGER PRIMARY KEY, " +
            COLUMN_FIRST + " TEXT, " + COLUMN_LAST + " TEXT, " +
            COLUMN_DISPLAY + " TEXT, " + COLUMN_CONTACT_EMAIL + " TEXT, " + COLUMN_IMAGE_RESOURCE + " INTEGER" + ")";


    /*
    private static String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS +
            "(" + COLUMN_ID_CONTACTS + " INTEGER PRIMARY KEY, " +
            COLUMN_FIRST + " TEXT, " + COLUMN_LAST + " TEXT, " +
            COLUMN_DISPLAY + " TEXT, " + COLUMN_CONTACT_EMAIL + " TEXT, " + COLUMN_NEW_IMAGE + " BLOB" + ")";
     */

    public MessagesDBHandler(Context context/*, String name, SQLiteDatabase.CursorFactory factory, int version*/) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContentResolver = context.getContentResolver();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FOLDERS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_ACCOUNTS_TABLE);
        db.execSQL(CREATE_ATTACHMENT_TABLE);
        Log.d("Elena", "Pokrenuto kreiranje tabela");
        db.execSQL(CREATE_TAG_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        //db.execSQL("ALTER TABLE CONTACT ADD COLUMN PICTURE BLOB");
        db.execSQL(CREATE_RULE_TABLE);
        db.execSQL("insert into " + TABLE_FOLDERS + "(" + COLUMN_ID_FOLDER + "," + COLUMN_NAME + ") values(1,'Drafts')");
        db.execSQL("insert into " + TABLE_FOLDERS + "(" + COLUMN_ID_FOLDER + "," + COLUMN_NAME + ") values(2,'Trash')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTACHMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULE);


        onCreate(db);

    }

    //////////////////////////FOLDERS //////////////////////////////
    public void addFolder(Folder folder){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, folder.getName());
        myContentResolver.insert(FoldersContentProvider.CONTENT_URI, values);
    }


    public Cursor getAllFolders(){

        String[] projection = {COLUMN_ID_FOLDER, COLUMN_NAME};

        Cursor cursor = myContentResolver.query(FoldersContentProvider.CONTENT_URI, projection,
                null, null, null);

        return cursor;
    }

    public String getMessagesInFolderCount(int folderID){

        String countQuery = "SELECT  * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_ID_FOLDERS_FK + " = " + folderID ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        if(count == 0){
            return "Nema poruka";
        }
        else {
            return String.valueOf(count);
        }
    }

    /////////////////////////// MESSAGES /////////////////////////////////

    public void addMessage(Message message) {
        ContentValues values = new ContentValues();
        //values.put(COLUMN_ID_EMAILS, message.get_id());
        values.put(COLUMN_FROM, message.getFrom());
        values.put(COLUMN_TO, message.getTo());
        values.put(COLUMN_CC, message.getCc());
        values.put(COLUMN_BCC, message.getBcc());
        values.put(COLUMN_DATETIME, message.getDateTime());
        values.put(COLUMN_SUBJECT, message.getSubject());
        values.put(COLUMN_CONTENT, message.getContent());
        values.put(COLUMN_ACCOUNTS_FK, message.getLogged_user_id());
        values.put(COLUMN_ID_FOLDERS_FK, message.getFolder_id());
        // values.put(COLUMN_ISUNREAD,message.isUnread());
        if (message.isUnread() == true) {
            values.put(COLUMN_ISUNREAD, 1);
        } else if (message.isUnread() == false) {
            values.put(COLUMN_ISUNREAD, 0);
        }
        StringBuffer tagsStr = new StringBuffer();
        for (Tag tag : message.getTags()) {
            tagsStr.append(tag.getName() + ";");
        }
        values.put(COLUMN_TAGS, tagsStr.toString());
        values.put(COLUMN_MESSAGE_ON_SERVER_ID, message.getIdOnServer());

        myContentResolver.insert(MessagesContentProvider.CONTENT_URI, values);
    }


    public void updateisRead(Message message) {

        ContentValues values = new ContentValues();

        if (message.isUnread() == true) {
            values.put(COLUMN_ISUNREAD, 1);
        } else if (message.isUnread() == false) {
            values.put(COLUMN_ISUNREAD, 0);
        }

        myContentResolver.update(MessagesContentProvider.CONTENT_URI, values, COLUMN_ID_EMAILS + "=" + message.get_id(), null);
    }

    public Message findMessageByServerId(int messageId) {

        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_ID_FOLDERS_FK, COLUMN_MESSAGE_ON_SERVER_ID};
        String selection = COLUMN_MESSAGE_ON_SERVER_ID + " = \"" + messageId + "\"";

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection, selection, null, null);

        Message message = new Message();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            message.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID_EMAILS))));
            message.setFrom(cursor.getString(cursor.getColumnIndex(COLUMN_FROM)));
            message.setTo(cursor.getString(cursor.getColumnIndex(COLUMN_TO)));
            message.setCc(cursor.getString(cursor.getColumnIndex(COLUMN_CC)));
            message.setBcc(cursor.getString(cursor.getColumnIndex(COLUMN_BCC)));
            message.setSubject(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT)));
            message.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
            message.setDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME)));
            message.setLogged_user_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNTS_FK))));
            message.setFolder_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID_FOLDERS_FK))));
            int unread = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ISUNREAD)));
            if (unread == 1) {
                message.setUnread(true);
            } else if (unread == 0) {
                message.setUnread(false);
            }
            message.setIdOnServer(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_ON_SERVER_ID))));

            String tags = cursor.getString(11);

            String[] tagList = tags.split(";[ ]*");

            List<Tag> tagsList = new ArrayList<>();

            for (String tag : tagList) {
                Tag tagobj = new Tag();
                tagobj.setName(tag);
                tagsList.add(tagobj);
            }

            message.setTags(tagsList);

            cursor.close();
        } else {
            message = null;
        }
        return message;
    }

    public Cursor filterEmail(String term) {
        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};

        String selection = COLUMN_CONTENT + " LIKE ? OR " + COLUMN_SUBJECT + " LIKE ? OR " + COLUMN_TAGS + " LIKE ? OR " + COLUMN_TO + " LIKE ? OR " +
                COLUMN_FROM + " LIKE ? OR " + COLUMN_BCC + " LIKE ? OR " + COLUMN_CC + " LIKE ?";

        String[] selectionArgs = {"%" + term + "%", "%" + term + "%", "%" + term + "%", "%" + term + "%", "%" + term + "%", "%" + term + "%", "%" + term + "%"};

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection, selection, selectionArgs, null);

        return cursor;
    }

    public Cursor sortEmailAsc(int userId) {

        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ID_FOLDERS_FK + "=" + 0, null,COLUMN_ID_EMAILS + " ASC");

        return cursor;
    }

    public Cursor sortEmailDesc(int userId) {



        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ID_FOLDERS_FK + "=" + 0, null, COLUMN_ID_EMAILS + " DESC");

        return cursor;
    }

    public Cursor sortEmailByDateDesc(int userID){
        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};

/*        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ID_FOLDERS_FK + "=" + 0, null, COLUMN_DATETIME + " DESC");*/

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ID_FOLDERS_FK + "=" + 0 +" AND " + COLUMN_ACCOUNTS_FK + " = " + userID, null, COLUMN_DATETIME + " DESC");

        return cursor;
    }

    public Cursor sortEmailByDateAsc(int userID){
        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};

        String selection = COLUMN_ID_FOLDERS_FK+" = ?" + " AND " + COLUMN_ACCOUNTS_FK + " = ?";
        String[] selectionArgs = {"0", String.valueOf(userID)};

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ID_FOLDERS_FK + "=" + 0 +" AND " + COLUMN_ACCOUNTS_FK + " = " + userID, null, COLUMN_DATETIME + " ASC");

  /*      Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                selection, selectionArgs, COLUMN_DATETIME + " ASC");*/
        return cursor;
    }

    public void updateMessage(int idPoruke,Message message) {
        int id = idPoruke;
        ContentValues values = new ContentValues();
        values.put(COLUMN_FROM, message.getFrom());
        values.put(COLUMN_TO, message.getTo());
        values.put(COLUMN_CC, message.getCc());
        values.put(COLUMN_BCC, message.getBcc());
        values.put(COLUMN_DATETIME, message.getDateTime());
        values.put(COLUMN_SUBJECT, message.getSubject());
        values.put(COLUMN_CONTENT, message.getContent());
        values.put(COLUMN_ACCOUNTS_FK, message.getLogged_user_id());
        values.put(COLUMN_ID_FOLDERS_FK, message.getFolder_id());
        // values.put(COLUMN_ISUNREAD,message.isUnread());
        if (message.isUnread() == true) {
            values.put(COLUMN_ISUNREAD, 1);
        } else if (message.isUnread() == false) {
            values.put(COLUMN_ISUNREAD, 0);
        }
        StringBuffer tagsStr = new StringBuffer();
        for (Tag tag : message.getTags()) {
            tagsStr.append(tag.getName() + ";");
        }
        values.put(COLUMN_TAGS, tagsStr.toString());
        values.put(COLUMN_MESSAGE_ON_SERVER_ID, message.getIdOnServer());
        // values.put(COLUMN_NEW_IMAGE,image);
        myContentResolver.update(MessagesContentProvider.CONTENT_URI, values, COLUMN_ID_EMAILS + "=" + id, null);
    }
    public Message findMessage(int messageId) {

        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_ID_FOLDERS_FK, COLUMN_MESSAGE_ON_SERVER_ID};
        String selection = "_id = \"" + messageId + "\"";

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection, selection, null, null);

        Message message = new Message();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            message.set_id(Integer.parseInt(cursor.getString(0)));
            message.setFrom(cursor.getString(1));
            message.setTo(cursor.getString(2));
            message.setCc(cursor.getString(3));
            message.setBcc(cursor.getString(4));
            message.setSubject(cursor.getString(5));
            message.setContent(cursor.getString(6));
            message.setDateTime(cursor.getString(7));
            message.setLogged_user_id(Integer.parseInt(cursor.getString(8)));
            message.setFolder_id(Integer.parseInt(cursor.getString(11)));
            int unread = Integer.parseInt(cursor.getString(9));
            if (unread == 1) {
                message.setUnread(true);
            } else if (unread == 0) {
                message.setUnread(false);
            }

            String tags = cursor.getString(10);
            message.setIdOnServer(cursor.getInt(11));

            String[] tagList = tags.split(";[ ]*");

            List<Tag> tagsList = new ArrayList<>();

            for (String tag : tagList) {
                Tag tagobj = new Tag();
                tagobj.setName(tag);
                tagsList.add(tagobj);
            }

            message.setTags(tagsList);

            cursor.close();
        } else {
            message = null;
        }
        return message;
    }

    public Cursor getAllMessages(int userId) {

        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};


        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ACCOUNTS_FK + "=" + userId, null, null);

        return cursor;
    }

    public Cursor getAllMessages2() {

        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};


        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                null, null, null);

        return cursor;
    }

    public List<Message> queryAllMessages(int userId) {

        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ISUNREAD, COLUMN_TAGS, COLUMN_MESSAGE_ON_SERVER_ID};
        String selection = null;

        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection, COLUMN_ACCOUNTS_FK + "=" + userId, null, null);

        List<Message> messages = new ArrayList<>();

/*        Message[] messages = new Message[cursor.getCount()];
        int i = 0;*/

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Message message = new Message();
            message.set_id(Integer.parseInt(cursor.getString(0)));
            message.setFrom(cursor.getString(1));
            message.setTo(cursor.getString(2));
            message.setCc(cursor.getString(3));
            message.setBcc(cursor.getString(4));
            message.setSubject(cursor.getString(5));
            message.setContent(cursor.getString(6));
            message.setDateTime(cursor.getString(7));
            message.setLogged_user_id(Integer.parseInt(cursor.getString(8)));
            int unread = Integer.parseInt(cursor.getString(9));
            if (unread == 1) {
                message.setUnread(true);
            } else if (unread == 0) {
                message.setUnread(false);
            }
            String tags = cursor.getString(10);

            String[] tagList = tags.split(";[ ]*");

            List<Tag> tagsList = new ArrayList<>();

            for (String tag : tagList) {
                Tag tagobj = new Tag();
                tagobj.setName(tag);
                tagsList.add(tagobj);
            }

            message.setTags(tagsList);

            message.setIdOnServer(cursor.getInt(11));
            messages.add(message);
            cursor.moveToNext();
        }
        cursor.close();
        return messages;

    }

    public boolean deleteMessage(int id) {

        boolean result = false;

        //String selection = "_id = " + "\"" + id + "\"";

        int rowsDeleted = myContentResolver.delete(MessagesContentProvider.CONTENT_URI, COLUMN_ID_EMAILS + " = " + id, null);

        if (rowsDeleted > 0)
            result = true;

        return result;


    }


    /////////////////////////// ACCOUNTS ////////////////////////////


    public void addAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SMTPADDRESS, account.getSmtpPort());
        values.put(COLUMN_PORT, account.getPort());
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_DISPLAYNAME, account.getDisplayName());
        values.put(COLUMN_EMAIL, account.geteMail());
        values.put(COLUMN_SMTPHOST, account.getSmtphost());
        values.put(COLUMN_IMAPHOST, account.getImapHost());
        values.put(COLUMN_BITMAP,account.getImageBitmap());

        myContentResolver.insert(AccountsContentProvider.CONTENT_URI, values);
    }

    public void updateAccount(int idAcc, Account account) {
        int id = idAcc;
        ContentValues values = new ContentValues();
        values.put(COLUMN_SMTPADDRESS, account.getSmtpPort());
        values.put(COLUMN_PORT, account.getPort());
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_DISPLAYNAME, account.getDisplayName());
        values.put(COLUMN_EMAIL, account.geteMail());
        values.put(COLUMN_SMTPHOST, account.getSmtphost());
        values.put(COLUMN_IMAPHOST, account.getImapHost());
        values.put(COLUMN_BITMAP, account.getImageBitmap());

        myContentResolver.update(AccountsContentProvider.CONTENT_URI, values, COLUMN_ID_EMAILS + "=" + id, null);
    }

    public List<Account> queryAccounts() {

        String[] projection = {COLUMN_ID_ACCOUNTS, COLUMN_SMTPADDRESS, COLUMN_PORT, COLUMN_USERNAME,
                COLUMN_PASSWORD, COLUMN_DISPLAYNAME, COLUMN_EMAIL, COLUMN_SMTPHOST, COLUMN_IMAPHOST, COLUMN_BITMAP};

        Cursor cursor = myContentResolver.query(AccountsContentProvider.CONTENT_URI, projection, null, null, null);

        List<Account> accounts = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Account account = new Account();
            account.set_id(Integer.parseInt(cursor.getString(0)));
            account.setSmtpPort(cursor.getString(1));
            account.setPort(cursor.getString(2));
            account.setUsername(cursor.getString(3));
            account.setPassword(cursor.getString(4));
            account.setDisplayName(cursor.getString(5));
            account.seteMail(cursor.getString(6));
            account.setSmtphost(cursor.getString(7));
            account.setImapHost(cursor.getString(8));
            account.setImageBitmap(cursor.getString(9));
            accounts.add(account);
            cursor.moveToNext();
        }
        cursor.close();
        return accounts;
    }




    public boolean deleteAccount(String userName) {
        boolean result = false;

        String selection = "subject = ?";
        String[] selectionArgs = {userName};

        int rowsDeleted = myContentResolver.delete(AccountsContentProvider.CONTENT_URI, selection, selectionArgs);

        if (rowsDeleted > 0)
            result = true;

        return result;
    }

    public Account findAccount(String username, String password) {
        String[] projection = {COLUMN_ID_ACCOUNTS, COLUMN_SMTPADDRESS, COLUMN_PORT, COLUMN_USERNAME,
                COLUMN_PASSWORD, COLUMN_DISPLAYNAME, COLUMN_EMAIL, COLUMN_SMTPHOST, COLUMN_IMAPHOST, COLUMN_BITMAP};

        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = myContentResolver.query(AccountsContentProvider.CONTENT_URI, projection, selection, selectionArgs, null);

        Account account = new Account();

        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            account = new Account();
            account.set_id(Integer.parseInt(cursor.getString(0)));
            account.setSmtpPort(cursor.getString(1));
            account.setPort(cursor.getString(2));
            account.setUsername(cursor.getString(3));
            account.setPassword(cursor.getString(4));
            account.setDisplayName(cursor.getString(5));
            account.seteMail(cursor.getString(6));
            account.setSmtphost(cursor.getString(7));
            account.setImapHost(cursor.getString(8));
            account.setImageBitmap(cursor.getString(9));
            cursor.close();
        } else {
            account = null;
        }
        return account;

    }

    /////////////////////////////////////////ATTACHMENTS/////////////////////////////////////////

    public void addAttachment(Attachment attachment) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTACH_CONTENT, attachment.getContent());
        values.put(COLUMN_FILENAME, attachment.getFileName());
        values.put(COLUMN_EMAIL_ID_FK, attachment.getMessageId());

        myContentResolver.insert(AttachmentsContentProvider.CONTENT_URI, values);
    }

    public List<Attachment> queryAttachForMessage(int messageAttId) {
        String[] projection = {COLUMN_ID_ATTACHMENTS, COLUMN_ATTACH_CONTENT, COLUMN_FILENAME, COLUMN_EMAIL_ID_FK};

        Cursor cursor = myContentResolver.query(AttachmentsContentProvider.CONTENT_URI,
                projection, COLUMN_EMAIL_ID_FK + "=" + messageAttId, null, null);

        List<Attachment> attachemntList = new ArrayList<>();

        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                Attachment attachment = new Attachment();
                attachment.set_id(Integer.parseInt(cursor.getString(0)));
                attachment.setContent(cursor.getString(1));
                attachment.setFileName(cursor.getString(2));
                attachemntList.add(attachment);
                cursor.moveToNext();
            }
        } catch (NullPointerException e) {
            Log.e("NULL", "Cursor attachment can't be null");
        }
        cursor.close();
        return attachemntList;

    }

    public Attachment queryAttachbyName(String messageName) {
        String[] projection = {COLUMN_ID_ATTACHMENTS, COLUMN_ATTACH_CONTENT, COLUMN_FILENAME, COLUMN_EMAIL_ID_FK};

        String selection = COLUMN_FILENAME + " = ? ";

        String[] selectionArgs = {messageName};

        Cursor cursor = myContentResolver.query(AttachmentsContentProvider.CONTENT_URI,
                projection, selection, selectionArgs, null);

        Attachment attachment;


        if (cursor.moveToFirst()) {
            attachment = new Attachment();
            attachment.set_id(Integer.parseInt(cursor.getString(0)));
            attachment.setContent(cursor.getString(1));
            attachment.setFileName(cursor.getString(2));
            cursor.close();
        } else {
            attachment = null;
        }
        return attachment;
    }

    //////////////////////////////////////////////TAGS/////////////////////////////////////

    public void addTag(Tag tag) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TAG_TEXT, tag.getName());
        values.put(COLUMN_EMAIL_TAG_ID_FK, tag.getMessageId());

        myContentResolver.insert(TagsContentProvider.CONTENT_URI, values);
    }

    public List<Tag> queryTagByMessID(int id) {
        String[] projection = {COLUMN_ID_TAG, COLUMN_TAG_TEXT, COLUMN_EMAIL_TAG_ID_FK};

        Cursor cursor = myContentResolver.query(TagsContentProvider.CONTENT_URI, projection, COLUMN_EMAIL_TAG_ID_FK + "=" + id, null, null);

        List<Tag> tagsList = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tag tag = new Tag();
            tag.set_id(Integer.parseInt(cursor.getString(0)));
            tag.setName(cursor.getString(1));
            tag.setMessageId(Integer.parseInt(cursor.getString(2)));
            tagsList.add(tag);
            cursor.moveToNext();

        }
        cursor.close();
        return tagsList;
    }


    /////////////////////////// CONTACTS /////////////////////////////////

    public List getAllContactsList() {
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //read from db
        String selection = "SELECT * FROM " + TABLE_CONTACTS;
        Cursor cursor = db.rawQuery(selection, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.setFirst(cursor.getString(1));
                contact.setLast(cursor.getString(2));
                contact.setDisplay(cursor.getString(3));
                contact.setEmail(cursor.getString(4));
                //               contact.setImageSourceID(Integer.parseInt(cursor.getString(5))); //zato sto baca NumberFormatException, zakomentarisala sam i u ContactsActivity
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    public Contact getContact(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
          TABLE_CONTACTS,
          new String []{COLUMN_ID_CONTACTS,COLUMN_FIRST,COLUMN_LAST,COLUMN_DISPLAY,COLUMN_CONTACT_EMAIL},
                COLUMN_ID_CONTACTS + "=?",
                new String[]{String.valueOf(id)},
                null,null,null,null
        );

        Contact contact;

        if(cursor!=null){
            cursor.moveToFirst();
            //dodati za display html i imagerseosurce id
            contact = new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),cursor.getString(4));
            return contact;
        } else{
            return null;
        }
    }


    public Cursor getItemID(String first) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "Select * from " + TABLE_CONTACTS + " Where firstname" +  " = '" + first + "'";

        Cursor data = db.rawQuery(query,null);
        return  data;
    }

    public Cursor getData() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

       public void updateData10(int contactID ,String first, String last, String display, String email){
        int id = contactID;
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST, first);
        values.put(COLUMN_LAST, last);
        values.put(COLUMN_DISPLAY, display);
        values.put(COLUMN_CONTACT_EMAIL, email);
        myContentResolver.update(ContactsContentProvider.CONTENT_URI, values, COLUMN_ID_CONTACTS + "=" + id, null);
    }

    public boolean deleteContact10(int id) {
        boolean result = false;
        int rowsDeleted = myContentResolver.delete(ContactsContentProvider.CONTENT_URI, COLUMN_ID_CONTACTS + " = " + id, null);
        if (rowsDeleted > 0)
            result = true;

        return result;

    }

    public void insertData10(String first,String last, String display, String email, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO CONTACT VALUES (NULL,?,?,?,?,?)";

        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,first);
        statement.bindString(2,last);
        statement.bindString(3,display);
        statement.bindString(4,email);
        statement.bindBlob(5,image);

        statement.executeInsert();
    }


    public void deleteFolder(int folderID) {
        myContentResolver.delete(FoldersContentProvider.CONTENT_URI, COLUMN_ID_FOLDER + " = " + folderID, null);
        deleteRule(folderID);
    }

    private void deleteRule(int folderID) {
        myContentResolver.delete(RuleContentProvider.CONTENT_URI, COLUMN_ID_FOLDER_FK + " = " + folderID, null);
    }


    public Rule findRule(int id) {
        String[] projection = {COLUMN_ID_RULE, COLUMN_CONDITION_TXT, COLUMN_CONDITION_ENUM , COLUMN_OPERATION_ENUM, COLUMN_ID_FOLDER_FK};
        String selection = "folder_fk = \"" + id + "\"";

        Cursor cursor = myContentResolver.query(RuleContentProvider.CONTENT_URI, projection, selection, null, null);

        Rule rule = new Rule();
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            rule.setId(Integer.parseInt(cursor.getString(0)));
            rule.setConditonTxt(cursor.getString(1));
            rule.setCondition(Condition.valueOf(cursor.getString(2)));
            rule.setOperation(Operation.valueOf(cursor.getString(3)));
            rule.setFolder_id(Integer.parseInt(cursor.getString(4)));
            cursor.close();
        }
        else {
            rule = null;
        }
        return rule;
    }


    public Folder findFolder(int id) {

        String[] projection = {COLUMN_ID_FOLDER, COLUMN_NAME};
        String selection = "_id = \"" + id + "\"";

        Cursor cursor = myContentResolver.query(FoldersContentProvider.CONTENT_URI, projection, selection, null, null);

        Folder folder = new Folder();
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            folder.setId(Integer.parseInt(cursor.getString(0)));
            folder.setName(cursor.getString(1));
            cursor.close();
        }
        else {
            folder = null;
        }
        return folder;
    }

    public void addRule(String enum1, String conTxt, String enum2, int folderID) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_CONDITION_ENUM, enum1);
        values.put(COLUMN_CONDITION_TXT, conTxt);
        values.put(COLUMN_OPERATION_ENUM, enum2);
        values.put(COLUMN_ID_FOLDER_FK, folderID);

        myContentResolver.insert(RuleContentProvider.CONTENT_URI, values);
    }

    public Folder findFolderID(String folderName) {

//        String countQuery = " SELECT " + COLUMN_ID_FOLDER + " FROM " + TABLE_FOLDERS + " WHERE " + COLUMN_NAME + " = " + folderName ;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        int id = Integer.parseInt(String.valueOf(cursor));
//        return id;
        String[] projection = {COLUMN_ID_FOLDER, COLUMN_NAME};
        String selection = "name = \"" + folderName + "\"";

        Cursor cursor = myContentResolver.query(FoldersContentProvider.CONTENT_URI, projection, selection, null, null);

        Folder folder = new Folder();
        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            folder.setId(Integer.parseInt(cursor.getString(0)));
            folder.setName(cursor.getString(1));
            cursor.close();
        }
        else {
            folder = null;
        }
        return folder;


    }


    public void updateRule(String enum1, String enum2, String conTxt, int id) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONDITION_ENUM, enum1);
        values.put(COLUMN_OPERATION_ENUM, enum2);
        values.put(COLUMN_CONDITION_TXT, conTxt);

        myContentResolver.update(RuleContentProvider.CONTENT_URI, values, COLUMN_ID_FOLDER_FK + "=" + id, null);
    }


    public void updateFolder(int id, String name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        myContentResolver.update(FoldersContentProvider.CONTENT_URI, values, COLUMN_ID_FOLDER + "=" + id, null);
    }

    public List<Rule> queryAllRules() {

        String[] projection = {COLUMN_ID_RULE, COLUMN_CONDITION_TXT, COLUMN_CONDITION_ENUM , COLUMN_OPERATION_ENUM, COLUMN_ID_FOLDER_FK};
        String selection = null;

        Cursor cursor = myContentResolver.query(RuleContentProvider.CONTENT_URI, projection, null, null, null);

        List<Rule> rules = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Rule rule = new Rule();
            rule.setId(Integer.parseInt(cursor.getString(0)));
            rule.setConditonTxt(cursor.getString(1));
            rule.setCondition(Condition.valueOf(cursor.getString(2)));
            rule.setOperation(Operation.valueOf(cursor.getString(3)));
            rule.setFolder_id(Integer.parseInt(cursor.getString(4)));
            rules.add(rule);
            cursor.moveToNext();
        }
        cursor.close();
        return rules;
    }

    public void runRule(){
        List<Message> messaages;
        messaages = queryAllMessages(Data.account.get_id());
        List<Rule> rules;
        rules = queryAllRules();
        int i;
        int l;

        for (i = 0 ; i < rules.size(); i++){
            Rule rule = rules.get(i);
            Condition c = rule.getCondition();
            Operation o = rule.getOperation();
            String t = rule.getConditonTxt();
            int idFolder = rule.getFolder_id();
            for(l = 0; l < messaages.size(); l++){
                Message message = messaages.get(l);
                if(message.getFolder_id() == 0){
                    int idMessage = message.get_id();
                    String to = message.getTo();
                    String from = message.getFrom();
                    String cc = message.getCc();
                    String subject = message.getSubject();
                    int messageFolderID = message.getFolder_id();

                    if(c == Condition.CC && o == Operation.COPY && cc.contains(t)){
                        copyInFolder(message, idFolder);
                    }
                    else if(c == Condition.CC && o == Operation.DELETE  && cc.contains(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                    else if(c == Condition.CC && o == Operation.MOVE  && cc.contains(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                    else if(c == Condition.TO && o == Operation.COPY && to.equals(t)){
                        copyInFolder(message, idFolder);
                    }
                    else if(c == Condition.TO && o == Operation.DELETE && to.equals(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                    else if(c == Condition.TO && o == Operation.MOVE && to.equals(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                    else if(c == Condition.FROM && o == Operation.COPY && from.equals(t)){
                        copyInFolder(message, idFolder);
                    }
                    else if(c == Condition.FROM && o == Operation.DELETE && from.equals(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                    else if(c == Condition.FROM && o == Operation.MOVE && from.equals(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                    else if(c == Condition.SUBJECT && o == Operation.COPY && subject.contains(t)){
                        copyInFolder(message, idFolder);
                    }
                    else if(c == Condition.SUBJECT && o == Operation.DELETE && subject.contains(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                    else if(c == Condition.SUBJECT && o == Operation.MOVE && subject.contains(t)){
                        moveToFolder(idMessage, idFolder);
                    }
                }
            }
        }

    }

    private void copyInFolder(Message message, int idFolder) {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_MESSAGE_ON_SERVER_ID + " = " + message.getIdOnServer();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        if(count == 1){
            message.setFolder_id(idFolder);
            addMessage(message);
        }
//        else if(count > 1 && message.getFolder_id() != idFolder && message.getFolder_id() != 0 ){
//            message.setFolder_id(idFolder);
//            addMessage(message);
//        }

//        ContentValues values = new ContentValues();
//        values.put(COLUMN_FROM, message.getFrom());
//        values.put(COLUMN_TO, message.getTo());
//        values.put(COLUMN_CC, message.getCc());
//        values.put(COLUMN_BCC, message.getBcc());
//        values.put(COLUMN_DATETIME, message.getDateTime());
//        values.put(COLUMN_SUBJECT, message.getSubject());
//        values.put(COLUMN_CONTENT, message.getContent());
//        values.put(COLUMN_ACCOUNTS_FK, message.getLogged_user_id());
//        values.put(COLUMN_ID_FOLDERS_FK, idFolder);
//        if (message.isUnread() == true) {
//            values.put(COLUMN_ISUNREAD, 1);
//        } else if (message.isUnread() == false) {
//            values.put(COLUMN_ISUNREAD, 0);
//        }
//        StringBuffer tagsStr = new StringBuffer();
//        for (Tag tag : message.getTags()) {
//            tagsStr.append(tag.getName() + ";");
//        }
//        values.put(COLUMN_TAGS, tagsStr.toString());
//        values.put(COLUMN_MESSAGE_ON_SERVER_ID, message.getIdOnServer());
//
//        myContentResolver.insert(MessagesContentProvider.CONTENT_URI, values);
    }

    public void moveToFolder(int idMessage, int idFolder) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_FOLDERS_FK, idFolder);
        myContentResolver.update(MessagesContentProvider.CONTENT_URI, values, COLUMN_ID_EMAILS + "=" + idMessage, null);
    }

    public void moveToTrash(int id) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_FOLDERS_FK, 2);
        myContentResolver.update(MessagesContentProvider.CONTENT_URI, values, COLUMN_ID_EMAILS + "=" + id, null);
    }



    public Cursor inboxEmails() {
        String inbox = "0";
        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ID_FOLDERS_FK, COLUMN_ISUNREAD, COLUMN_TAGS};
        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ID_FOLDERS_FK + "=" + inbox, null, null);
        return cursor;
    }

    public Cursor emailsFolder(String foldersIdExtra) {
        int id = Integer.parseInt(foldersIdExtra);
        String[] projection = {COLUMN_ID_EMAILS, COLUMN_FROM, COLUMN_TO, COLUMN_CC, COLUMN_BCC, COLUMN_SUBJECT, COLUMN_CONTENT, COLUMN_DATETIME, COLUMN_ACCOUNTS_FK, COLUMN_ID_FOLDERS_FK, COLUMN_ISUNREAD, COLUMN_TAGS};
        Cursor cursor = myContentResolver.query(MessagesContentProvider.CONTENT_URI, projection,
                COLUMN_ID_FOLDERS_FK + "=" + id, null, null);
        return cursor;
    }
}
