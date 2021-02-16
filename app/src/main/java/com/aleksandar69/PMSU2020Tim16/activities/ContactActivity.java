package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.adapters.RecyclerViewContactsAdapter;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.models.Contact;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends AppCompatActivity {

    private TextInputEditText firstNameEditt;
    String selectedFirst;
    String selectedLast;
    String selectedDisplay;
    String selectedEmail;
    int selectedID;
    Button deleteButton;
    private TextInputEditText lastNameEditt;
    private TextInputEditText displayNameEditt;
    private TextInputEditText emailEditt;
    private CircleImageView imageView;
    MessagesDBHandler handler;
    private String contactId;
    private int contactIdInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactIdInt = Integer.parseInt(String.valueOf(selectedID));

        firstNameEditt = (TextInputEditText) findViewById(R.id.new_contact_firstnamee);
        lastNameEditt = (TextInputEditText) findViewById(R.id.new_contact_lastnamee);
        displayNameEditt = (TextInputEditText) findViewById(R.id.new_contact_displaynamee);
        emailEditt = (TextInputEditText) findViewById(R.id.new_contact_emaill);
        imageView = (CircleImageView) findViewById(R.id.image_single_contact);
        deleteButton = (Button) findViewById(R.id.delete_button);

        handler = new MessagesDBHandler(this);

        Intent intent2 = getIntent();
        selectedID = intent2.getIntExtra("id", -1);
        selectedFirst = intent2.getStringExtra("first");
        selectedLast = intent2.getStringExtra("last");
        selectedDisplay = intent2.getStringExtra("display");
        selectedEmail = intent2.getStringExtra("email");
        if(getIntent().hasExtra("byteArray")) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),
                    0,getIntent().getByteArrayExtra("byteArray").length); //ne moze length
            imageView.setImageBitmap(bitmap);
        }
        firstNameEditt.setText(selectedFirst);
        lastNameEditt.setText(selectedLast);
        displayNameEditt.setText(selectedDisplay);
        emailEditt.setText(selectedEmail);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(ContactActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        888);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.deleteContact10(selectedID);
                startActivity(new Intent(ContactActivity.this,ContactsActivity.class));
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Contact");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_contact:
                String first =  firstNameEditt.getText().toString().trim();
                String last = lastNameEditt.getText().toString().trim();
                String display = displayNameEditt.getText().toString().trim();
                String email = emailEditt.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                byte[] image = CreateContactActivity.imageViewToByte(imageView);

                if(first.isEmpty() || first.length() < 3) {
                    firstNameEditt.setError("Ime mora da sadrzi najmanje tri slova! ");
                    firstNameEditt.requestFocus();
                } else if (last.isEmpty() || last.length() < 3) {
                    lastNameEditt.setError("Prezime mora da sadrzi najmanje tri slova! ");
                    lastNameEditt.requestFocus();
                } else if (display.isEmpty()) {
                    displayNameEditt.setError("Unesite ime koje zelite da se prikazuje! ");
                    displayNameEditt.requestFocus();
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Email adresa nije unesena u ispravnom formatu! Unesite ponovo.. ", Toast.LENGTH_SHORT).show();
                }
                else {
                    handler.updateData10(selectedID,first,last,display,email);
                    Toast.makeText(this, "Uspjesno ste izmijenili kontakt! ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ContactActivity.this,ContactsActivity.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 888 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
