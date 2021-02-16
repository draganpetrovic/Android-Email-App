package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.javamail.DeleteEmail;
import com.aleksandar69.PMSU2020Tim16.models.Attachment;
import com.aleksandar69.PMSU2020Tim16.models.Message;
import com.aleksandar69.PMSU2020Tim16.models.Tag;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EmailActivity extends AppCompatActivity {


    private TextView tvFrom;
    private TextView tvSubject;
    private TextView tvContent;
    private TextView tvCC;
    private TextView tvTo;
    private Button buttonAttach;
    private TextView tvTags;
    private List<Attachment> attachments;


    private int itemId;
    private MessagesDBHandler emailsDb;
    private String emailId;
    private Message message;
    private StringBuffer tags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        tvFrom = findViewById(R.id.from_tv);
        tvSubject = findViewById(R.id.subject_tv);
        tvContent = findViewById(R.id.content);
        tvCC = findViewById(R.id.cc_tv);
        tvTo = findViewById(R.id.to_tv);
        buttonAttach = findViewById(R.id.attachments);
        tvTags = findViewById(R.id.tag_tv);


        emailsDb = new MessagesDBHandler(this);

        emailId = (String) getIntent().getExtras().get(Data.MESS_ID_EXTRA);
        itemId = Integer.parseInt(emailId);
        message = emailsDb.findMessage(itemId);
        tags = new StringBuffer();

        attachments = emailsDb.queryAttachForMessage(message.get_id());

        loadEmail();

        Toolbar toolbar = findViewById(R.id.email_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Message");

        if (attachments.size() < 1) {
            buttonAttach.setVisibility(View.GONE);
        }
    }




/*        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with Samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            startActivityForResult(chooserIntent,PERM_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }*/


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_email, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void decodeOnClick(View view) throws IOException {

        if (!(attachments.size() < 1)) {

            for (int i = 0; i < attachments.size(); i++) {

                String fileContent = decodeBase64(attachments.get(i).getContent());

                // String file = decodeBase64(message.getAttachments());

                byte[] imgBytes = Base64.decode(attachments.get(i).getContent(), Base64.DEFAULT);

                File storage = Environment.getExternalStorageDirectory();
                File dir = new File(storage.getAbsolutePath());


                File file1 = new File(dir, attachments.get(i).getFileName());

                FileOutputStream fos = new FileOutputStream(file1);
                fos.write(imgBytes);
                fos.flush();

                Toast.makeText(this, "Attachment Saved To Root", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "Your message has no attachment included.", Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String decodeBase64(String coded) {
        byte[] valueDecoded = new byte[0];
        valueDecoded = Base64.decode(coded.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String content = message.getContent();
        String from = message.getFrom();
        String subject = message.getSubject();
        String cc = message.getCc();
        String date = message.getDateTime();
        String to = message.getTo();
        int folderID = message.getFolder_id();
        switch (item.getItemId()) {
            case R.id.email_delete_button:
                if (folderID == 2) {
                    emailsDb.deleteMessage(itemId);
                    DeleteEmail deleteEmail = new DeleteEmail(this, message.get_id());
                    deleteEmail.execute();
                    startActivity(new Intent(this, EmailsActivity.class));
                    return true;
                }
                else {
                    emailsDb.moveToTrash(itemId);
                    super.onBackPressed();
                    return true;
                }
            case R.id.forward:
                Intent intentForwrad = new Intent(this, CreateEmailActivity.class);
                intentForwrad.putExtra(Data.EMAIL_FORWARD_EXTRA, content);
                intentForwrad.putExtra(Data.FORWARD_FROM_EXTRA, from);
                intentForwrad.putExtra(Data.FORWARD_SUBJECT_EXTRA, subject);
                intentForwrad.putExtra(Data.FORWARD_CC_EXTRA, cc);
                intentForwrad.putExtra(Data.FORWARD_DATE_EXTRA, date);
                intentForwrad.putExtra(Data.FORWARD_TO_EXTRA, to);
                Data.isForward = true;
                startActivity(intentForwrad);
                Toast.makeText(this, "Forward message", Toast.LENGTH_LONG).show();
                return true;
            case R.id.reply_button:
                Intent intentReply = new Intent(this, CreateEmailActivity.class);
                intentReply.putExtra(Data.REPLY_FROM, from);
                intentReply.putExtra(Data.REPLY_CONTENT, content);
                Data.isReply = true;
                startActivity(intentReply);
                Toast.makeText(this, "Reply to Message", Toast.LENGTH_LONG).show();
                return true;
            case R.id.reply_to_all_button:
                Intent intentReplyToAll = new Intent(this, CreateEmailActivity.class);
                intentReplyToAll.putExtra(Data.REPLY_TO_ALL_FROM, from);
                intentReplyToAll.putExtra(Data.REPLY_TO_ALL_TO, to);
                intentReplyToAll.putExtra(Data.REPLY_TO_ALL_CONTENT, content);
                Data.isReplyToAll = true;
                startActivity(intentReplyToAll);
                Toast.makeText(this, "Reply to All", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadEmail() {

        tvFrom.setText(message.getFrom());
        tvSubject.setText(message.getSubject());
        tvContent.setText(message.getContent());
        tvCC.setText(message.getCc());
        tvTo.setText(message.getTo());

        List<Tag> tagList = emailsDb.queryTagByMessID(message.get_id());

        for (Tag tag: tagList
        ) {
            tags.append(tag.getName() + " ");
        }

        tvTags.setText(tags.toString());


        message.setUnread(false);
        emailsDb.updateisRead(message);

    }


    @Override
    protected void onStart() {
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
