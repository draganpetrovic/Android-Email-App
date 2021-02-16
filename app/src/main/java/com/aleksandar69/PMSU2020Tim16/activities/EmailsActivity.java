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

public class EmailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListView.OnItemClickListener {

    Cursor cursor;
    ListView emails;
    SharedPreferences sharedPreferences;
    MessagesDBHandler handler;
    SearchView searchView;
    EditText searchViewET;
    EmailsCursorAdapter emailsAdapter;
    SwipeRefreshLayout pullToRefresh;
    private TextView displayNameNav;
    private TextView emailNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emails);


        emails = findViewById(R.id.emails_list_view);
        pullToRefresh = findViewById(R.id.pullToRefresh);


        try {
            handler = new MessagesDBHandler(this);
        } catch (SQLException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }



        Toolbar toolbar = findViewById(R.id.emails_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Inbox");

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
        // populateListFromDB();

        //sharedPreferences = getSharedPreferences(LoginActivity.myPreferance, Context.MODE_PRIVATE);

 /*        Data.syncTime= sharedPreferences.getString(getString(R.string.pref_sync_list),"60000" );
        Data.allowSync = sharedPreferences.getBoolean(getString(R.string.pref_sync),false);

        Log.d("SYNCTIME:", Data.syncTime);
       Log.d("ALLOWSYNC", Data.allowSync.toString());*/

        populateList();

        Log.d("onCreateSharedPrefs" ,Data.allowSync.toString());
        Log.d("onCreateSharedPrefs" ,Data.syncTime);
        Log.d("onCreateSharedPrefs" ,Data.prefSort);

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

    public void startSyncService() {

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
    }

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

    public void onTempButtonClickedFind(View view) {
        onSearchRequested();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
/*            if (query == "" || query.isEmpty()) {
                Cursor c = handler.getAllMessages2();
                EmailsCursorAdapter emailsAdapter = new EmailsCursorAdapter(this, c);
                emails.setAdapter(emailsAdapter);

            }*/
            Cursor c = handler.filterEmail(query);
            emailsAdapter = new EmailsCursorAdapter(this, c);
            emails.setAdapter(emailsAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
            String email_id = cursor.getString(0);

            Intent intent = new Intent(this, EmailActivity.class);
            intent.putExtra(Data.MESS_ID_EXTRA, email_id);
            startActivity(intent);
    }


    public void populateList() {
        // cursor = handler.getAllMessages(sharedPreferences.getInt(LoginActivity.userId, -1));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Data.syncTime = sharedPreferences.getString(getString(R.string.pref_syncConnectionType),"60000" );
        Data.allowSync = sharedPreferences.getBoolean(getString((R.string.pref_sync)),false);
        Data.prefSort = sharedPreferences.getString(getString(R.string.pref_sort),"ascending");
        //Cursor c = handler.inboxEmails();

        if(Data.prefSort.equals("ascending")){
            cursor = handler.sortEmailByDateAsc(sharedPreferences.getInt(Data.userId, -1));
        }
        else if(Data.prefSort.equals("descending")){
            cursor = handler.sortEmailByDateDesc(sharedPreferences.getInt(Data.userId, -1));
        }


        emailsAdapter = new EmailsCursorAdapter(this, cursor);
        emails.setOnItemClickListener(this);
        emails.setAdapter(emailsAdapter);
        try {
            handler.runRule();
        }
        catch (NullPointerException e){}

    }


    public void onProfileClicked(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onTempButtonClickedFind(View view) {
        EditText editText = (EditText) findViewById(R.id.searchText);
        EditText editText1 = (EditText) findViewById(R.id.searchText2);
        String edit = editText.getText().toString();
        String edit1 = editText1.getText().toString();

        MessagesDBHandler messagesDBHandler = new MessagesDBHandler(this);
        List <Message> messages = messagesDBHandler.findMessagesTest(edit, edit1);


*/
    /*        List<Message> messages = messagesDBHandler.queryAllMessages();*//*


        Message[] listMessages = new Message[messages.size()];
        listMessages = messages.toArray(listMessages);

        //Message[] messages = messagesDBHandler.queryAllMessages();


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listMessages);
        emails.setOnItemClickListener(this);
        emails.setAdapter(adapter);
    }
*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_emails, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_emails).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo( new ComponentName(this, SearchResultActivity.class)));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                populateList();
                EditText et = findViewById(R.id.search_src_text);
                et.setText("");
            }
        });


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_message:
                Intent intent = new Intent(this, CreateEmailActivity.class);
                Toast.makeText(this, "Create email selected", Toast.LENGTH_LONG).show();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
    }

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
