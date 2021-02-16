package com.aleksandar69.PMSU2020Tim16.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.aleksandar69.PMSU2020Tim16.R;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EmailsCursorAdapter extends CursorAdapter {

    public EmailsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_layout_emails, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView columnFrom = view.findViewById(R.id.columnFrom);
        TextView columnSubject = view.findViewById(R.id.columnSubject);
        TextView columnContent = view.findViewById(R.id.columnContent);
        TextView columnDate = view.findViewById(R.id.columnDate);
        TextView columnTags = view.findViewById(R.id.tags_listview);

        String messageFrom = cursor.getString(cursor.getColumnIndex("messagefrom"));
        String messageSubject = cursor.getString(cursor.getColumnIndex("subject"));
        String messageContent = cursor.getString(cursor.getColumnIndex("content"));
        String messageDate = cursor.getString(cursor.getColumnIndex("datetime"));
        //Integer isUnread = Integer.parseInt(cursor.getString(cursor.getColumnIndex("isunreadmessage")));
        String messageTags = cursor.getString(cursor.getColumnIndex("tagsinmail"));

        Boolean isUnread = (cursor.getInt(cursor.getColumnIndex("isunreadmessage")) == 1);

        Random random = new Random();

        String[] tagList = messageTags.split(";[ ]*");

        Random random2 = new Random();

        columnFrom.setText(messageFrom);
        columnSubject.setText(messageSubject);
        columnContent.setText(messageContent);
        columnDate.setText(messageDate);

        for (String tag : tagList) {
            int nextInt = random.nextInt(0xffffff + 1);
            String colorCode = String.format("#%06x", nextInt);
            Spannable word = new SpannableString(tag + ";");
            word.setSpan(new ForegroundColorSpan(Color.parseColor(colorCode)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            columnTags.append(word);
            //columnTags.setTextColor(Color.parseColor(colorCode));
        }
/*        columnTags.setText(messageTags);
        columnTags.setTextColor(Color.parseColor(colorCode));*/

        if (isUnread == true) {

            columnFrom.setTypeface(null, Typeface.BOLD);
            columnSubject.setTypeface(null, Typeface.BOLD);
            columnContent.setTypeface(null, Typeface.BOLD);
        } else if (isUnread == false) {
            columnFrom.setTypeface(null, Typeface.NORMAL);
            columnSubject.setTypeface(null, Typeface.NORMAL);
            columnContent.setTypeface(null, Typeface.NORMAL);
        }
    }

}
