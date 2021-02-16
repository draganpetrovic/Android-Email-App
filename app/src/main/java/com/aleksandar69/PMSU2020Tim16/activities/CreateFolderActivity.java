package com.aleksandar69.PMSU2020Tim16.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.Data;
import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.enums.Condition;
import com.aleksandar69.PMSU2020Tim16.enums.Operation;
import com.aleksandar69.PMSU2020Tim16.models.Folder;
import com.aleksandar69.PMSU2020Tim16.models.Rule;
import com.google.android.material.textfield.TextInputEditText;

public class CreateFolderActivity extends AppCompatActivity {


    private TextView tvName;
    private TextView tvConditionTxt;
    private TextView tvConditionE;
    private TextView tvOperationE;

    private TextInputEditText etName;
    private TextInputEditText etConditonTxt;
    private TextInputEditText etConditionE;
    private TextInputEditText etOperationE;
    private Spinner spinerCondition;
    private Spinner spinerOperation;

    private int id;
    private String folderId;
    //private TextInputEditText nameEditBox;
    MessagesDBHandler dbHandler;
    private Folder folder;
    private Rule rule;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_folder);

        try {
            folderId = (String) getIntent().getExtras().get(Data.FOLDERS_ID_EXTRA);
            id = Integer.parseInt(folderId);
        }
        catch (NullPointerException e){
        }



        //nameEditBox = findViewById(R.id.folder_name);
        etName = findViewById(R.id.folder_name);
        etConditonTxt = findViewById(R.id.folder_condition);
        //etConditionE = findViewById(R.id.spinner1);
        //etOperationE = findViewById(R.id.spinner2);

        tvName = findViewById(R.id.folder_name);
        tvConditionTxt = findViewById(R.id.folder_condition);
        //tvConditionE = findViewById(R.id.spinner1);
        //tvOperationE = findViewById(R.id.spinner2);

        spinerCondition = findViewById(R.id.spinner1);
        SpinnerAdapter conAdapter = new ArrayAdapter<Condition>(this, android.R.layout.simple_spinner_item, Condition.values());
        spinerCondition.setAdapter(conAdapter);
        //spinerCondition.setAdapter(new ArrayAdapter<Condition>(this, android.R.layout.simple_spinner_item, Condition.values()));
        spinerOperation = findViewById(R.id.spinner2);
        SpinnerAdapter opeAdapter = new ArrayAdapter<Operation>(this, android.R.layout.simple_spinner_item, Operation.values());
        //spinerOperation.setAdapter(new ArrayAdapter<Operation>(this, android.R.layout.simple_spinner_item, Operation.values()));
        spinerOperation.setAdapter(opeAdapter);

        dbHandler = new MessagesDBHandler(this);


        if (id != 0){
            folder = dbHandler.findFolder(id);
            rule = dbHandler.findRule(id);
            tvName.setText(folder.getName());
            tvConditionTxt.setText(rule.getConditonTxt());
            spinerCondition.setSelection(((ArrayAdapter) conAdapter).getPosition(rule.getCondition()));
            spinerOperation.setSelection(((ArrayAdapter) opeAdapter).getPosition(rule.getOperation()));
        }

        Toolbar toolbar = findViewById(R.id.toolbar_create_folder);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(id == 0) {
            setTitle("Create Folder");
        }
        else{
            setTitle("Edit folder" );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_folder, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        String enum1 = spinerCondition.getSelectedItem().toString();
        String enum2 = spinerOperation.getSelectedItem().toString();
        String folderName = etName.getText().toString().trim();
        String cTxt = etConditonTxt.getText().toString().trim();

        switch (item.getItemId()) {
            case R.id.folder_cancel:
                Intent intent = new Intent(this, FoldersActivity.class);
                startActivity(intent);
                return true;
            case R.id.folder_save:

                if (id == 0){
                    if(folderName.isEmpty()){
                        etName.setError("Unesite ime foldera.");
                        etName.requestFocus();
                    }
                    else if(cTxt.isEmpty()){
                        etConditonTxt.setError("Popunite polje.");
                        etConditonTxt.requestFocus();
                    }
                    else {
                        Folder folder = new Folder(folderName);
                        dbHandler.addFolder(folder);
                        Folder newFolder = dbHandler.findFolderID(folderName);
                        dbHandler.addRule(enum1, cTxt, enum2, newFolder.getId());
                        startActivity(new Intent(this, FoldersActivity.class));
                        return true;
                    }
                }
                else {
                    String name = tvName.getText().toString().trim();
                    String conTxt = tvConditionTxt.getText().toString().trim();
                    dbHandler.updateFolder(id,name);
                    dbHandler.updateRule(enum1,enum2,conTxt,id);
                    super.onBackPressed();
                    return true;

                }
            default:
                return super.onOptionsItemSelected(item);
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
