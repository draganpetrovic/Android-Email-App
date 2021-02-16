package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.adapters.FoldersCursorAdapter;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoldersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListView.OnItemClickListener  {

    Cursor cursor;
    ListView folders;
    MessagesDBHandler handler;
    SwipeRefreshLayout pullToRefresh;
    FoldersCursorAdapter foldersAdapter;
    private TextView displayNameNav;
    private TextView emailNav;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);

        folders = findViewById(R.id.folders_list_view);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            handler = new MessagesDBHandler(this);
        } catch (SQLException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        Toolbar toolbar = findViewById(R.id.folders_toolbar);
        setSupportActionBar(toolbar);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_input_get);
        actionBar.setTitle("Folders");


        Cursor c = handler.getAllFolders();
        foldersAdapter = new FoldersCursorAdapter(this, c);
        folders.setOnItemClickListener(this);
        folders.setAdapter(foldersAdapter);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_folders);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        CircleImageView imageView23 = (CircleImageView) header.findViewById(R.id.imageViewNav);
        Bitmap bitmapImage = Data.StringToBitMap(Data.account.getImageBitmap());
        if(bitmapImage!=null) {
            imageView23.setImageBitmap(bitmapImage);
        }
        displayNameNav = (TextView) header.findViewById(R.id.displayNameNav);
        emailNav = (TextView) header.findViewById(R.id.emailNav);
        displayNameNav.setText(Data.account.getDisplayName());
        emailNav.setText(Data.account.geteMail());
    }


    public void onProfileClicked(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folders, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_folder:
                Intent intent = new Intent(this, CreateFolderActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickFAB(View view) {
        startActivity(new Intent(this, CreateFolderActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent = null;

        switch (id) {

            case R.id.nav_contacts:
                intent = new Intent(this, ContactsActivity.class);
                break;
            case R.id.nav_inbox:
                intent = new Intent(this,EmailsActivity.class);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.nav_logout:
                startActivity(new Intent(this, LoginActivity.class));
                Data.account = null;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                break;
            default:
                intent = new Intent(this, FoldersActivity.class);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        String folder_id = cursor.getString(0);


        Intent intent = new Intent(this, FolderActivity.class);
        intent.putExtra(Data.FOLDERS_ID_EXTRA, folder_id);
        startActivity(intent);
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
