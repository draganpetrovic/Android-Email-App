package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.models.Account;
import com.aleksandar69.PMSU2020Tim16.services.EmailSyncService;
import com.aleksandar69.PMSU2020Tim16.services.EmailsJobSchedulerSyncService;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextView registerTV;
    private ListView testView;
    private EditText usernameText;
    private EditText passwordText;
    MessagesDBHandler dbHandler;

    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;

    SharedPreferences sharedPreferences;
    //public static String myPreferance = "mypref";
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isStoragePermissionGranted();

        registerTV = findViewById(R.id.register);
        usernameText = findViewById(R.id.username_field);
        passwordText = findViewById(R.id.password_field);
        dbHandler = new MessagesDBHandler(this);
        mProgressDialog = new ProgressDialog(this);

        //sharedPreferences = getSharedPreferences(myPreferance, Context.MODE_PRIVATE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

/*
        Data.syncTime = sharedPreferences.getString(getString(R.string.pref_syncConnectionType),"60000" );
        Data.allowSync = sharedPreferences.getBoolean(getString((R.string.pref_sync)),false);
        Data.prefSort = sharedPreferences.getString(getString(R.string.pref_sort),"descending");
*/


        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        usernameLayout = findViewById(R.id.usernameLayoutLogin);
        passwordLayout = findViewById(R.id.password_layout_login);

    }


    public boolean validateUsername() {
        String usernameLayoutStr = usernameLayout.getEditText().getText().toString().trim();

        if (usernameLayoutStr.isEmpty()) {
            usernameLayout.setError("Field can't be empty");
            return false;
        } else {
            usernameLayout.setError(null);
            return true;
        }
    }

    public boolean valdiatePassword(){
        String passwordLayoutStr = passwordLayout.getEditText().getText().toString().trim();


        if(passwordLayoutStr.isEmpty()){
            passwordLayout.setError("Field can't be empty");
            return false;
        }
        else{
            passwordLayout.setError(null);
            return true;
        }
    }

    public void onLoginButtonClicked(View view) {

        if(!validateUsername() | !valdiatePassword()){
            return;
        }


        Data.account = dbHandler.findAccount(usernameText.getText().toString(), passwordText.getText().toString());


        if (Data.account != null) {

            mProgressDialog.setMessage("Logging you in, please wait.");
            mProgressDialog.show();
            mProgressDialog.setTitle("Logging in");

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.cancel();
                }
            };

            Handler pdCancel = new Handler();
            pdCancel.postDelayed(runnable, 3000);

            Intent intent = new Intent(this, EmailsActivity.class);
            startActivity(intent);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Data.userEmail, Data.account.geteMail());
            editor.putString(Data.userPassworrd, Data.account.getPassword());
            editor.putInt(Data.userId, Data.account.get_id());
            editor.commit();
        } else {
            Toast.makeText(this, "The credentials you entered are not valid", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }



        public  boolean isStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                return true;
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
/*        Intent i = new Intent(this, EmailsForegroundService.class);
        startService(i);*/
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
