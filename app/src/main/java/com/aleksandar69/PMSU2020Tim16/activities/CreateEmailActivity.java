package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.javamail.SendEmail;
import com.aleksandar69.PMSU2020Tim16.javamail.SendMultipartEmail;
import com.aleksandar69.PMSU2020Tim16.javamail.SendMultipartMailConcurrent;
import com.aleksandar69.PMSU2020Tim16.models.Account;
import com.aleksandar69.PMSU2020Tim16.models.Message;
import com.aleksandar69.PMSU2020Tim16.models.Tag;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateEmailActivity extends AppCompatActivity {

    private static final int PERM_CODE = 125;


    private TextInputEditText toEditBox;
    private TextInputEditText ccEditBox;
    private TextInputEditText bccEditBox;
    private TextInputEditText subjectEditBox;
    private EditText contentEditBox;
    private TextView attachedFiles;
    private TextInputEditText tagsEditBox;
    MessagesDBHandler dbHandler;
    Message mes;

    private TextInputLayout toLayout;
    private TextInputLayout ccLayout;
    private TextInputLayout bccLayout;

    int itemId;


    private List<Tag> listOfTagObjs;


    SharedPreferences mSharedPreferences;

    StringBuffer tags;

    private String filePath = null;
    private List<String> uriList;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        dbHandler = new MessagesDBHandler(this);

        toEditBox = findViewById(R.id.email_to);
        ccEditBox = findViewById(R.id.email_cc);
        bccEditBox = findViewById(R.id.email_bcc);
        subjectEditBox = findViewById(R.id.email_subject);
        contentEditBox = findViewById(R.id.email_content);
        attachedFiles = findViewById(R.id.attached_files);
        tagsEditBox = findViewById(R.id.tags_edit);
        toLayout = findViewById(R.id.to_edit_layout);
        ccLayout = findViewById(R.id.cc_layout_createnew);



        bccLayout = findViewById(R.id.bcc_layout_createnew);


        try {
            String emailId = (String) getIntent().getExtras().get(Data.MESS_ID_EXTRA);
            itemId = Integer.parseInt(emailId);
        }
        catch (NullPointerException e){}

        if (itemId != 0){
            mes = dbHandler.findMessage(itemId);
            toEditBox.setText(mes.getTo());
            ccEditBox.setText(mes.getCc());
            bccEditBox.setText(mes.getBcc());
            subjectEditBox.setText(mes.getSubject());
            contentEditBox.setText(mes.getContent());
        }


        tags = new StringBuffer();

        attachedFiles.setText("0");

        uriList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.create_email);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("New Message");

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (Data.isForward == true) {
            getForwardContent();
            Data.isForward = false;
        }

        if(Data.isReply == true){
            getReplyContent();
            Data.isReply = false;
        }
        if(Data.isReplyToAll == true){
            getReplyToAllContent();
            Data.isReplyToAll = false;
        }

    }


    public void getReplyContent(){
        String from = (String) getIntent().getExtras().get(Data.REPLY_FROM);
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(from.toLowerCase());
        StringBuffer frombuff = new StringBuffer();
        if (matcher.find()) {
            frombuff.append(matcher.group());
        }
        String content = (String) getIntent().getExtras().get(Data.REPLY_CONTENT);
        toEditBox.setText(frombuff + ";");
        contentEditBox.setText("__________________\n\n" +  content + "\n__________________ \n\n");

    }

    public void getReplyToAllContent(){
        String from = (String) getIntent().getExtras().get(Data.REPLY_TO_ALL_FROM);
        String to = (String) getIntent().getExtras().get(Data.REPLY_TO_ALL_TO);
        String content = (String) getIntent().getExtras().get(Data.REPLY_TO_ALL_CONTENT);

        if(from != null && !(from.equals(""))) {
            Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
            Matcher matcher = pattern.matcher(from.toLowerCase());
            if (matcher.find()) {
                toEditBox.append(matcher.group() + ";");
            }
        }

        if(to != null && !(to.equals(""))) {

            Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
            Matcher matcher = pattern.matcher(to.toLowerCase());
            if (matcher.find()) {
                toEditBox.append(matcher.group() + ";");
            }
        }
        contentEditBox.setText("__________________\n\n" +  content + "\n__________________ \n\n");



    }

    public void getForwardContent() {
        String content = (String) getIntent().getExtras().get(Data.EMAIL_FORWARD_EXTRA);
        String from = (String) getIntent().getExtras().get(Data.FORWARD_FROM_EXTRA);
        String subject = (String) getIntent().getExtras().get(Data.FORWARD_SUBJECT_EXTRA);
        String cc = (String) getIntent().getExtras().get(Data.FORWARD_CC_EXTRA);
        String date = (String) getIntent().getExtras().get(Data.FORWARD_DATE_EXTRA);
        String to = (String) getIntent().getExtras().get(Data.FORWARD_TO_EXTRA);

        subjectEditBox.setText("FWD: " + subject);

        contentEditBox.setText("From: " + from +
                "\n To: " + to +
                "\n Sent: " + date +
                "\n CC:" + cc +
                "\n Subject: " + subject +
                "\n" +
                "\n" +
                "\n"
                + content);
    }


    public void openFileClick(View view) {
        openFile("*/*");
    }

    public static Date fromUTC (String dateStr){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String toUTC (Date date){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Date trenutniDatum = new Date();
        String formatiranDatum = toUTC( trenutniDatum);

        switch (item.getItemId()) {
            case R.id.email_cancel_button:

                if (!toEditBox.getText().toString().equals("") || !ccEditBox.getText().toString().equals("") ||
                        !bccEditBox.getText().toString().equals("") || !subjectEditBox.getText().toString().equals("") || !contentEditBox.getText().toString().equals("") ){
                    Message message = new Message(toEditBox.getText().toString(),
                            ccEditBox.getText().toString(), bccEditBox.getText().toString(), subjectEditBox.getText().toString(), contentEditBox.getText().toString());
                    message.setLogged_user_id(mSharedPreferences.getInt(Data.userId, -1));
                    message.setUnread(true);
                    message.setFolder_id(1);
                    message.setDateTime(formatiranDatum);
                    //Message m = dbHandler.findMessage(itemId);
//                    if(!m.getTo().equals(message.getTo()) || !m.getCc().equals(message.getCc()) || !m.getBcc().equals(message.getBcc())
//                    || !m.getSubject().equals(message.getSubject()) || !m.getContent().equals(message.getContent()) ){
                    dbHandler.addMessage(message);
                    try {
                        if(mes.getFolder_id() == 1){
                            dbHandler.deleteMessage(itemId);
                        }
                    }
                    catch(NullPointerException e){}

                    Intent intent = new Intent(this, EmailsActivity.class);
                    Data.totalEmailsServer++;
                    Toast.makeText(this, "Message saved as Draft", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    return true;
                }
                else{
                    Intent intent = new Intent(this, EmailsActivity.class);
                    Toast.makeText(this, "Message Creation Cancelled", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    return true;
                }
            case R.id.email_send_button:
                if(validateMail() & validateBCC() & validateCC() & checkIfAllAreEmpty()){


                Message message = new Message(toEditBox.getText().toString(),
                        ccEditBox.getText().toString(), bccEditBox.getText().toString(), subjectEditBox.getText().toString(), contentEditBox.getText().toString());
                message.setLogged_user_id(mSharedPreferences.getInt(Data.userId, -1));
                tags.append(tagsEditBox.getText().toString());
                if(mes.getFolder_id() == 1){
                    dbHandler.deleteMessage(itemId);
                }

                if (filePath != null || !tags.toString().isEmpty()) {
                 //   SendMultipartEmail sendMessage = new SendMultipartEmail(this, message.getSubject(), message.getContent(), uriList, message.getCc(), message.getBcc(), message.getTo(), tags.toString(), Data.account);
                //    sendMessage.execute();
                   SendMultipartMailConcurrent sendeMai= new SendMultipartMailConcurrent(this, message.getSubject(), message.getContent(), uriList, message.getCc(), message.getBcc(), message.getTo(), tags.toString(), Data.account);
                    sendeMai.Send();

               } else {
                    SendEmail sendMessage = new SendEmail(message.getSubject(), message.getContent(), message.getCc(), message.getBcc(), message.getTo(), tags.toString(), Data.account);
                    sendMessage.execute();
                }
                Toast.makeText(this, "Message sent", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, EmailsActivity.class));
                return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openFile(String mimeType) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.putExtra("CONTENT_TYPE", mimeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            if(android.os.Build.MANUFACTURER.toLowerCase().equals("samsung")) {
                startActivityForResult(sIntent, PERM_CODE);
            }
            else{
                startActivityForResult(Intent.createChooser(intent, "Select Files"), PERM_CODE);
            }
            //startActivityForResult(Intent.createChooser(intent,"Select file/s"), PERM_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERM_CODE) {
            if (null != data) { // checking empty selection
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (null != data.getClipData()) { // checking multiple selection or not
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            filePath = data.getClipData().getItemAt(i).getUri().getPath();
                            uriList.add(data.getClipData().getItemAt(i).getUri().getPath());
                            attachedFiles.setText(String.valueOf(uriList.size()));
                        }
                    } else {
                        filePath = data.getData().getPath();
                        uriList.add(data.getData().getPath());
                        attachedFiles.setText(String.valueOf(uriList.size()));
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "An error has occured: API level requirements not met", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public boolean validateMail(){

        String emailLayout = toLayout.getEditText().getText().toString().trim();

        boolean status = true;

        String[] emails = emailLayout.split(";[ ]*");

        for (String s: emails
             )
        {
            if(emailLayout.isEmpty() || emailLayout == ""){
            toLayout.setError(null);
            status=true;
        }
            else if(!Patterns.EMAIL_ADDRESS.matcher(s).matches())
            {
                status = false;
                toLayout.setError("Wrong email format.");
                Toast.makeText(this, "Wrong email format", Toast.LENGTH_SHORT).show();

             }
            else
            {
                toLayout.setError(null);
                status = true;
            }
        }
        return status;
    }

    public boolean validateCC(){

        String emailLayout = ccLayout.getEditText().getText().toString().trim();

        boolean status = true;

        String[] emails = emailLayout.split(";[ ]*");


        for (String s: emails)
        {

            if(emailLayout.isEmpty() || emailLayout == ""){
            ccLayout.setError(null);
            status=true;
        }
            else  if(!Patterns.EMAIL_ADDRESS.matcher(s).matches())
        {
            status = false;
            ccLayout.setError("Wrong email format.");
            Toast.makeText(this, "Wrong email format", Toast.LENGTH_SHORT).show();
        }
            else
            {
                ccLayout.setError(null);
                status = true;
            }
        }
        return status;
    }

    public boolean validateBCC(){

        String emailLayout = bccLayout.getEditText().getText().toString().trim();

        boolean status = true;

        String[] emails = emailLayout.split(";[ ]*");

        for (String s: emails)
        {
            if(emailLayout.isEmpty() || emailLayout == ""){
            bccLayout.setError(null);
            status=true;
        }

            else if(!Patterns.EMAIL_ADDRESS.matcher(s).matches())
        {
            status = false;
            bccLayout.setError("Wrong email format.");
            Toast.makeText(this, "Wrong email format", Toast.LENGTH_SHORT).show();
        }
            else
            {
                bccLayout.setError(null);
                status = true;
            }

        }


        return status;
    }

    public boolean checkIfAllAreEmpty(){
        String toL = toLayout.getEditText().getText().toString().trim();
        String ccL = ccLayout.getEditText().getText().toString().trim();
        String bccL = bccLayout.getEditText().getText().toString().trim();

        boolean status = true;

        if(toL.isEmpty() && ccL.isEmpty() && bccL.isEmpty()){
            toLayout.setError("Please fill at least one recipient field");
            ccLayout.setError("Please fill at least one recipient field");
            bccLayout.setError("Please fill at least one recipient field");
            status = false;
        }
        else{
            toLayout.setError(null);
            ccLayout.setError(null);
            bccLayout.setError(null);
        }
        return status;


    }

/*    public void sendMessage(View view) {
        MessagesDBHandler dbHandler = new MessagesDBHandler(this);

       Message message = new Message(fromEditBox.getText().toString(), toEditBox.getText().toString(),
                ccEditBox.getText().toString(), bccEditBox.getText().toString(),subjectEditBox.getText().toString(), contentEditBox.getText().toString());

       message.setLogged_user_id(mSharedPreferences.getInt(LoginActivity.userId, -1));

        dbHandler.addMessage(message);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_email, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onStart() {
/*        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }*/
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
