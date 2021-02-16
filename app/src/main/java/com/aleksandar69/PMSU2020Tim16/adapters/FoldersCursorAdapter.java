package com.aleksandar69.PMSU2020Tim16.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;


import com.aleksandar69.PMSU2020Tim16.R;

public class FoldersCursorAdapter extends CursorAdapter {

    private MessagesDBHandler handler;

    public FoldersCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_layout_folders, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){

        TextView columnFolderName = view.findViewById(R.id.columnFolderName);
        TextView columnNumberOfMessages = view.findViewById(R.id.columnNumberOfMessages);

        String folderName = cursor.getString(cursor.getColumnIndex("name"));
        int folderID = cursor.getInt(cursor.getColumnIndex("_id"));
        handler = new MessagesDBHandler(context);
        String folderNumberOfMessages = handler.getMessagesInFolderCount(folderID);

        columnFolderName.setText(folderName);
        columnNumberOfMessages.setText(folderNumberOfMessages);

    }

}
