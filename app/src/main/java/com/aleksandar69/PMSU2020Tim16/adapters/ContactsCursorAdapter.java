package com.aleksandar69.PMSU2020Tim16.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aleksandar69.PMSU2020Tim16.R;

public class ContactsCursorAdapter extends CursorAdapter {

    public ContactsCursorAdapter(Context context, Cursor cursor) {
        super(context,cursor);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row,parent,false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView columnFirst = view.findViewById(R.id.columnFirst);
        TextView columnLast = view.findViewById(R.id.columnLast);
        TextView columnDisplay = view.findViewById(R.id.columnDisplay);
        TextView columnEmail = view.findViewById(R.id.columnEmail);


        String contactFirstName = cursor.getString(cursor.getColumnIndex("firstname"));
        String contactLastName = cursor.getString(cursor.getColumnIndex("lastname"));
        //displaytext i emailtext
        String contactDisplayText = cursor.getString(cursor.getColumnIndex("displaytext"));
        String contactEmailText = cursor.getString(cursor.getColumnIndex("emailtext"));

        columnFirst.setText(contactFirstName);
        columnLast.setText(contactLastName);
        columnDisplay.setText(contactDisplayText);
        columnEmail.setText(contactEmailText);
    }
}
