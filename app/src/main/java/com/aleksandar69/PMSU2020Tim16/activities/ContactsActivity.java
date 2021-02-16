package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.adapters.ContactsArrayAdapter;
import com.aleksandar69.PMSU2020Tim16.adapters.ContactsBaseAdapter;
import com.aleksandar69.PMSU2020Tim16.adapters.ContactsCursorAdapter;
import com.aleksandar69.PMSU2020Tim16.adapters.RecyclerViewContactsAdapter;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.models.Contact;
import com.aleksandar69.PMSU2020Tim16.models.Photo;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ListView.OnItemClickListener {
    private ListView contactsListView;
    private ArrayList<Contact> list = new ArrayList<>();
    MessagesDBHandler handler;

    ContactsBaseAdapter adapter = null;
    ImageView imageViewContact;
    String filePath;
    private TextView displayNameNav;
    private TextView emailNav;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsListView = (ListView) findViewById(R.id.recycler_v);
        handler = new MessagesDBHandler(this);
        CustomAdapter adapter = new CustomAdapter(this, R.layout.row, list);
        populateView();
        contactsListView.setAdapter(adapter);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Cursor cursor = handler.getData();
        list.clear();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String first = cursor.getString(1);
            String last = cursor.getString(2);
            String display = cursor.getString(3);
            String email = cursor.getString(4);
            final byte[] image = cursor.getBlob(5);

            list.add(new Contact(id, first,last,display,email,image));
        }

        Toolbar toolbar = findViewById(R.id.contacts_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_input_get);
        actionBar.setTitle("Contacts");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_contacts);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void ToastMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                Toast.makeText(getApplicationContext(), "Nemate dozvolu da pristupite ovom fajlu!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //when we pick out file this is called
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 888 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewContact.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void onProfileClicked(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    private void populateView() {
        Cursor data = handler.getData();
        while(data.moveToNext()) {
            //ona ne dodaje int
            String first = data.getString(1);
            String last = data.getString(2);
            String display = data.getString(3);
            String email =data.getString(4);
            byte[] image = data.getBlob(5);

            list.add(new Contact(first,last,display,email,image));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_contact:
                Intent intent = new Intent(this, CreateContactActivity.class);
                Toast.makeText(this, "Otkazano kreiranje kontakta!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent = null;

        switch (id) {

            case R.id.nav_folders:
                intent = new Intent(this, FoldersActivity.class);
                break;
            case R.id.nav_inbox:
                intent = new Intent(this, EmailsActivity.class);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.nav_logout:
                intent = new Intent(this, LoginActivity.class);
                Data.account = null;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                break;
            default:
                intent = new Intent(this, ContactsActivity.class);
        }

        startActivity(intent);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        String contact_id = cursor.getString(0);

        Intent intent = new Intent(this,ContactActivity.class);
        intent.putExtra(Data.CONTACT_ID_EXTRA, contact_id);
        startActivity(intent);
    }

    public class CustomAdapter extends BaseAdapter {

        private Context context;
        ArrayList<Contact> contactsFromAdapter;
        private int layout;
        Contact contact = new Contact();
        private int lastPosition = -1;

        public CustomAdapter(Context context, int layout, ArrayList<Contact> contactsFromAdapter) {
            this.context = context;
            this.layout = layout;
            this.contactsFromAdapter = contactsFromAdapter;
        }

        @Override
        public int getCount() {
            return contactsFromAdapter.size();
        }

        @Override
        public Object getItem(int position) {
            return contactsFromAdapter.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            CircleImageView imageViewHolder;
            TextView textViewHolder;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View row = view;
            ViewHolder holder = new ViewHolder();
            final View result;

            if(row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layout,null);
                holder.textViewHolder = (TextView) row.findViewById(R.id.row_first);
                holder.imageViewHolder = (CircleImageView) row.findViewById(R.id.imageViewUpload);
                result = row;
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
                result = row;
            }

            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
            result.startAnimation(animation);
            lastPosition = position;

            final Contact contact = contactsFromAdapter.get(position);
            holder.textViewHolder.setText(contact.getFirst());
            final byte[] contactImage = contact.getImage();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(contactImage, 0, contactImage.length);
            holder.imageViewHolder.setImageBitmap(bitmap);

            row.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor data = handler.getItemID(contact.getFirst());
                    int itemID = -1; //there is no data of this id
                    String first = "";
                    String last = "";
                    String display = "";
                    String email = "";
                    byte[] image = null;

                    while (data.moveToNext()) {
                        itemID = data.getInt(0);
                        first = data.getString(1);
                        last = data.getString(2);
                        display = data.getString(3);
                        email  = data.getString(4);
                        image = data.getBlob(5);

                        //pravljenje intenta
                        Intent intent = new Intent(ContactsActivity.this, ContactActivity.class);
                        intent.putExtra("id", itemID);
                        intent.putExtra("first", contact.getFirst());
                        intent.putExtra("last", contact.getLast());
                        intent.putExtra("display", contact.getDisplay());
                        intent.putExtra("email", contact.getEmail());
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bs);
                        intent.putExtra("byteArray",bs.toByteArray());
                        startActivity(intent);

                    }
                    /*

                    if (itemID > -1) {
                        //ima podatke
                        ToastMessage("ID ovog reda je : " + itemID +
                                " Ime :" + first + "\n" +
                                " Prezime : " + last + "\n" +
                                "Display : " + display + "\n" +
                                "Email :" + email + "\n" +
                                "Bajt slike :" + image
                        );
                    } else {
                        ToastMessage("NEMA PODATAKA !!!");
                    }

                     */
                }

            });
            return row;
        }
    }
}