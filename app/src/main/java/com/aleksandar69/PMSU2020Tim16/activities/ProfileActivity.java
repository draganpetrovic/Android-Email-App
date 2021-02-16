package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.models.Account;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView displayNameTV;
    private TextView userNameTV;
    private TextView emailTV;
    private CircleImageView imageViewTV;
    public CircleImageView imageViewNav;
    private CircleImageView imageView23;
    MessagesDBHandler dbHandler;
    private TextView displayNameNav;
    private TextView emailNav;
    boolean ucitanaSlika = false;



    SharedPreferences sharedPreferences;

    private List<String> uriList;
    private static final int PERM_CODE = 126;
    private static final int PERMISSION_REQUEST_CODE = 127;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHandler = new MessagesDBHandler(this);

/*        LayoutInflater factory = getLayoutInflater();
        View imageViewLayout = factory.inflate(R.layout.nav_layout, null);*/

        emailTV= findViewById(R.id.emailProfile);
        displayNameTV = findViewById(R.id.displayNameProfile);
        userNameTV = findViewById(R.id.usernameProfile);
        imageViewTV = findViewById(R.id.imageViewProfile);



        uriList = new ArrayList<String>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile");

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        imageView23 = (CircleImageView) header.findViewById(R.id.imageViewNav);
        displayNameNav = (TextView) header.findViewById(R.id.displayNameNav);
        emailNav = (TextView) header.findViewById(R.id.emailNav);

        Bitmap bitmapImage = Data.StringToBitMap(Data.account.getImageBitmap());
        if(Data.profileImageFilePath!=null) {
            ucitajSliku();
        }
        else if(bitmapImage!=null) {
            imageView23.setImageBitmap(bitmapImage);
            imageViewTV.setImageBitmap(bitmapImage);
        }

        imageViewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile("image/*");

            }
        });

        popuniPolja();
    }

    public void ucitajSliku(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    File dir = new File(Data.profileImageFilePath);
                    if (dir.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(dir), options);

                        String bitmapStr = BitMapToString(bitmap);


                        Data.account.setImageBitmap(bitmapStr);

                        dbHandler.updateAccount(Data.account.get_id(), Data.account);

                        String username = Data.account.getUsername();
                        String password = Data.account.getPassword();

                        Account account2 = dbHandler.findAccount(username, password);

                        Bitmap imageBitmap = StringToBitMap(account2.getImageBitmap());

                        imageViewTV.setImageBitmap(imageBitmap);
                        imageView23.setImageBitmap(imageBitmap);
                    } else {
                    }
                } else {
                    requestPermission();
                }
            } else {
                File dir = new File(Data.profileImageFilePath);
                if (dir.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Data.bitmap = BitmapFactory.decodeFile(String.valueOf(dir), options);
                    imageViewTV.setImageBitmap(Data.bitmap);
                    imageView23.setImageBitmap(Data.bitmap);
                }
            }
        }
    }

    public void switchAcc(View view){
        startActivity(new Intent(this, LoginActivity.class));
        Data.account = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    public void onProfileClicked(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Please allow file permission in your settings", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void openFile(String mimeType) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
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
                            Data.profileImageFilePath = data.getClipData().getItemAt(i).getUri().getPath();
                            uriList.add(data.getClipData().getItemAt(i).getUri().getPath());
                        }
                    } else {
                        Data.profileImageFilePath = data.getData().getPath();
                        uriList.add(data.getData().getPath());
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "An error has occured: API level requirements not met", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void popuniPolja(){
        emailTV.setText(Data.account.geteMail());
        displayNameTV.setText(Data.account.getDisplayName());
        userNameTV.setText(Data.account.getUsername());
        displayNameNav.setText(Data.account.getDisplayName());
        emailNav.setText(Data.account.geteMail());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_logout:
                Intent intent = new Intent(this, LoginActivity.class);
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
            case R.id.nav_contacts:
                intent = new Intent(this, ContactsActivity.class);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, LoginActivity.class);
                Data.account = null;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                break;
            case R.id.nav_logout:
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.nav_inbox:
                intent = new Intent(this,EmailsActivity.class);
                break;
            default:
                intent = new Intent(this,ProfileActivity.class);
        }
        startActivity(intent);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        Bitmap bitmapImage = Data.StringToBitMap(Data.account.getImageBitmap());
        if(Data.profileImageFilePath!=null) {
            ucitajSliku();
        }
        else if(bitmapImage!=null) {
            imageView23.setImageBitmap(bitmapImage);
            imageViewTV.setImageBitmap(bitmapImage);
        }
        else{

        }

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
