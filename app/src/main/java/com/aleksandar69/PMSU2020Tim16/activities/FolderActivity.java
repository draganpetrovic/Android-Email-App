//package com.aleksandar69.PMSU2020Tim16.activities;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.preference.PreferenceManager;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import android.app.SearchManager;
//import android.app.job.JobInfo;
//import android.app.job.JobScheduler;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.aleksandar69.PMSU2020Tim16.Data;
//import com.aleksandar69.PMSU2020Tim16.R;
//import com.aleksandar69.PMSU2020Tim16.adapters.EmailsCursorAdapter;
//import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
//import com.aleksandar69.PMSU2020Tim16.services.EmailSyncService;
//import com.aleksandar69.PMSU2020Tim16.services.EmailsJobSchedulerSyncService;
//import com.google.android.material.navigation.NavigationView;
//
//public class FolderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListView.OnItemClickListener {
//
//    Cursor cursor;
//    ListView emails;
//    SharedPreferences sharedPreferences;
//    MessagesDBHandler handler;
//    SearchView searchView;
//    EmailsCursorAdapter emailsAdapter;
//    SwipeRefreshLayout pullToRefresh;
//    private String folderID;
//    private int itemID;
//
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_folder);
//
//
//        folderID = (String) getIntent().getExtras().get(Data.FOLDERS_ID_EXTRA);
//        itemID = Integer.parseInt(folderID);
//
//        try {
//            handler = new MessagesDBHandler(this);
//        } catch (SQLException e) {
//            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
//            toast.show();
//        }
//
//        Toolbar toolbar = findViewById(R.id.toolbar_folder);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("Folder");
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        populateList();
//
//        Log.d("onCreateSharedPrefs", Data.allowSync.toString());
//        Log.d("onCreateSharedPrefs", Data.syncTime);
//        Log.d("onCreateSharedPrefs", Data.prefSort);
//
//
//
//        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                populateList();
//
//                pullToRefresh.setRefreshing(false);
//            }
//        });
//
//    }
//
//    public void startSyncService() {
//
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//
//                ComponentName componentName = new ComponentName(this,
//                        EmailsJobSchedulerSyncService.class);
//
//                JobInfo jobInfoObj = new JobInfo.Builder(111, componentName).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
//
//                jobScheduler.schedule(jobInfoObj);
//            }
//            else{
//                Intent i = new Intent(this, EmailSyncService.class);
//                startService(i);
//            }
//        }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void StopSyncingService(){
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.cancel(111);
//    }
//
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
//        String email_id = cursor.getString(0);
//
//        Intent intent = new Intent(this, EmailActivity.class);
//        intent.putExtra(Data.MESS_ID_EXTRA, email_id);
//        startActivity(intent);
//    }
//
//    public void populateList() {
//
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Data.syncTime = sharedPreferences.getString(getString(R.string.pref_syncConnectionType),"60000" );
//        Data.allowSync = sharedPreferences.getBoolean(getString((R.string.pref_sync)),false);
//        Data.prefSort = sharedPreferences.getString(getString(R.string.pref_sort),"ascending");
//
//        if(Data.prefSort.equals("ascending")){
//            cursor = handler.sortEmailAsc(sharedPreferences.getInt(Data.userId, -1));
//        }
//        else if(Data.prefSort.equals("descending")){
//            cursor = handler.sortEmailDesc(sharedPreferences.getInt(Data.userId, -1));
//        }
//
//        Cursor c = handler.findEmailByFolderID(itemID);
//        emailsAdapter = new EmailsCursorAdapter(this, c);
//        emails.setOnItemClickListener(this);
//        emails.setAdapter(emailsAdapter);
//
//    }
//
//
//    public void onProfileClicked(View view) {
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item){
//        switch (item.getItemId()){
//            case R.id.create_folder:
//                Intent intent = new Intent(this, CreateFolderActivity.class);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_folder, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        int id = item.getItemId();
//        Intent intent = null;
//
//        switch (id) {
//
//            case R.id.nav_folders:
//                intent = new Intent(this, FoldersActivity.class);
//                break;
//            case R.id.nav_contacts:
//                intent = new Intent(this, ContactsActivity.class);
//                break;
//            case R.id.nav_settings:
//                intent = new Intent(this, SettingsActivity.class);
//                break;
//            case R.id.nav_logout:
//                intent = new Intent(this, LoginActivity.class);
//                Data.account = null;
//                emails.clearChoices();
//                emails.setAdapter(null);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.clear();
//                editor.commit();
//                break;
//            default:
//                intent = new Intent(this, EmailsActivity.class);
//        }
//
//        startActivity(intent);
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onStart() {
//        populateList();
//        if(Data.allowSync == true) {
//            startSyncService();
//        }
//        else if(Data.allowSync == false){
//            StopSyncingService();
//        }
//
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        populateList();
//        Log.d("onResumeSharedPrefs",Data.prefSort);
//        Log.d("onResumeSharedPrefs",Data.allowSync.toString());
//        Log.d("onResumeSharedPrefs",Data.syncTime);
//
//        super.onResume();
//
//        super.onResume();
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//}

package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.SearchManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.adapters.EmailsCursorAdapter;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.javamail.ImapFetchMail;
import com.aleksandar69.PMSU2020Tim16.models.Folder;
import com.aleksandar69.PMSU2020Tim16.models.Message;
import com.aleksandar69.PMSU2020Tim16.services.EmailSyncService;
import com.aleksandar69.PMSU2020Tim16.services.EmailsJobSchedulerSyncService;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FolderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListView.OnItemClickListener {

    Cursor cursor;
    ListView emails;
    SharedPreferences sharedPreferences;
    MessagesDBHandler handler;
    String folderID;
    EmailsCursorAdapter emailsAdapter;
    SwipeRefreshLayout pullToRefresh;
    private TextView displayNameNav;
    private TextView emailNav;
    int idID;
    Folder f;
    String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        try {
            handler = new MessagesDBHandler(this);
        } catch (SQLException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        emails = findViewById(R.id.emails_list_view);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        try {
            folderID = (String) getIntent().getExtras().get(Data.FOLDERS_ID_EXTRA);
            idID = Integer.parseInt(folderID);
        }
        catch (NullPointerException e){}

        if(idID != 0) {
            f = handler.findFolder(idID);
            folderName = f.getName();
        }




        Toolbar toolbar = findViewById(R.id.toolbar_folder);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(folderName);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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
        populateList();

        handleIntent(getIntent());

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateList();
                handleIntent(getIntent());
                pullToRefresh.setRefreshing(false);
            }
        });

    }

/*    public void startSyncService() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

            ComponentName componentName = new ComponentName(this,
                    EmailsJobSchedulerSyncService.class);

            JobInfo jobInfoObj = new JobInfo.Builder(111, componentName).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();

            jobScheduler.schedule(jobInfoObj);
        }
        else{
            Intent i = new Intent(this, EmailSyncService.class);
            startService(i);
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void StopSyncingService(){
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(111);
    }

    public void onClickFAB(View view) {
        startActivity(new Intent(this, CreateEmailActivity.class));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        super.onNewIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            Cursor c = handler.filterEmail(query);
            emailsAdapter = new EmailsCursorAdapter(this, c);
            emails.setAdapter(emailsAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        String email_id = cursor.getString(0);
        int itemId = Integer.parseInt(email_id);
        // Proverimo da li se nalazimo u Drafts folderu, onda pozivamo createEmailActivity
        //da bi mogli nastaviti prekinuto kreiranje emaila.
        if (idID == 1) {
             Intent intent = new Intent(this, CreateEmailActivity.class);
            intent.putExtra(Data.MESS_ID_EXTRA, email_id);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, EmailActivity.class);
            intent.putExtra(Data.MESS_ID_EXTRA, email_id);
            startActivity(intent);
        }
    }

    public void populateList() {

        Cursor c = handler.emailsFolder(folderID);
        emailsAdapter = new EmailsCursorAdapter(this, c);
        emails.setOnItemClickListener(this);
        emails.setAdapter(emailsAdapter);

    }


    public void onProfileClicked(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folder, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.folder_delete_button:
                if(idID == 1 || idID == 2){
                    Toast.makeText(this, "Ne mozete obrisati ovaj folder", Toast.LENGTH_LONG).show();
                    return true;
                }
                else{
                    handler.deleteFolder(idID);
                    startActivity(new Intent(this, FoldersActivity.class));
                    return true;
                }


            case R.id.edit_folder:
                if(idID == 1 || idID == 2){
                    Toast.makeText(this, "Ne mozete menjati ovaj folder.", Toast.LENGTH_LONG).show();
                    return true;
                }
                else {
                    Intent intent = new Intent(this, CreateFolderActivity.class);
                    intent.putExtra(Data.FOLDERS_ID_EXTRA, folderID);

                    Toast.makeText(this, "Edit folder selected", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    return true;
                }
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
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.nav_logout:
                intent = new Intent(this, LoginActivity.class);
                Data.account = null;
                emails.clearChoices();
                emails.setAdapter(null);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                break;
            default:
                intent = new Intent(this, EmailsActivity.class);
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

/*    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        populateList();
        handleIntent(getIntent());
        if(Data.allowSync == true) {
            startSyncService();
        }
        else if(Data.allowSync == false){
            StopSyncingService();
        }

        super.onStart();
    }*/

    @Override
    protected void onResume() {
        populateList();
        handleIntent(getIntent());
        Log.d("onResumeSharedPrefs",Data.prefSort);
        Log.d("onResumeSharedPrefs",Data.allowSync.toString());
        Log.d("onResumeSharedPrefs",Data.syncTime);

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
