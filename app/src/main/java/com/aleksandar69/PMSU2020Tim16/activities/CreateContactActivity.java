package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.util.ByteArrayDataSource;

import de.hdodenhof.circleimageview.CircleImageView;
public class CreateContactActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1000;
    private ProgressBar progressBarEdit;
    Button chooseButton;
    Context context;
    public static ImageView imageEdit;
    private TextInputEditText firstNameEdit;
    private TextInputEditText lastNameEdit;
    private TextInputEditText displayNameEdit;
    private TextInputEditText emailEdit;
    public static MessagesDBHandler handler;
    private TextView showContacts;
    private Uri imageUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        context = this;
        handler = new MessagesDBHandler(context);

        showContacts = findViewById(R.id.text_view_show_contacts);
        imageEdit = findViewById(R.id.image_contact);
        chooseButton = findViewById(R.id.choose_image_button);
        firstNameEdit = findViewById(R.id.new_contact_firstname);
        lastNameEdit = findViewById(R.id.new_contact_lastname);
        displayNameEdit = findViewById(R.id.new_contact_displayname);
        emailEdit = findViewById(R.id.new_contact_email);

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        showContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactsActivity();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_create_contact);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
    }

    private void openContactsActivity() {
        Intent intent = new Intent(this,ContactsActivity.class);
        startActivity(intent);
    }

    //metoda sluzi za dobavljanje ekstenzije
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.cancel_contact_creation:
                Intent intent = new Intent(this,ContactsActivity.class);
                Toast.makeText(this,"Otkazano kreiranje kontakta!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                return true;
            case R.id.save_new_contact:
                String first =  firstNameEdit.getText().toString().trim();
                String last = lastNameEdit.getText().toString().trim();
                String display = displayNameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                byte[] image = imageViewToByte(imageEdit);

                if(first.isEmpty() || first.length() < 3) {
                    firstNameEdit.setError("Ime mora da sadrzi najmanje tri slova! ");
                    firstNameEdit.requestFocus();
                } else if (last.isEmpty() || last.length() < 3) {
                    lastNameEdit.setError("Prezime mora da sadrzi najmanje tri slova! ");
                    lastNameEdit.requestFocus();
                } else if (display.isEmpty()) {
                    displayNameEdit.setError("Unesite ime koje zelite da se prikazuje! ");
                    displayNameEdit.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Email adresa nije unesena u ispravnom formatu! Pokusajte ponovo..", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.insertData10(first,last,display,email,image);
                    Toast.makeText(this, "Kontakt je uspjesno kreiran!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateContactActivity.this,ContactsActivity.class));
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //pomocu ove metode image view kompresujem u bajtove
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray= stream.toByteArray();
        return byteArray;
    }

    //when we pick out file this is called
    //kad izaberem sliku iz galerije, pomocu ove metode se slika setuje u imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageEdit.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //enkodiranje slike u base 65 preko bitmapa
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    //enkodiranje slike u base 64 preko putanje
    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;

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