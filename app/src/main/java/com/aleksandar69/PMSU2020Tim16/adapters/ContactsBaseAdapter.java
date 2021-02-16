package com.aleksandar69.PMSU2020Tim16.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.activities.ContactsActivity;
import com.aleksandar69.PMSU2020Tim16.database.MessagesDBHandler;
import com.aleksandar69.PMSU2020Tim16.models.Contact;

import java.util.ArrayList;

public class ContactsBaseAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Contact> contactsList;
    MessagesDBHandler handler;
    Contact contact = new Contact();

    public ContactsBaseAdapter(Context context, int layout, ArrayList<Contact> contactsList) {
        this.context = context;
        this.layout = layout;
        this.contactsList = contactsList;
    }

    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView txtFirst, txtLast, txtDisplay, txtEmail;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtFirst = (TextView) row.findViewById(R.id.row_first);
            holder.imageView = (ImageView) row.findViewById(R.id.imageViewUpload);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Contact contact = contactsList.get(position);
        holder.txtFirst.setText(contact.getFirst());
        byte[] contactImage = contact.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(contactImage, 0, contactImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}

        //red clickable
        //ovo je isto kao da sam u contacts kliknula na item iz liste, ista metoda
        //probati ovu metodu prebaciti u contacts metodu
        /*
        row.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = handler.getItemID(contact.getFirst());
                int itemID = -1;
                String first = "";
                byte[] image = null;

                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                    first = data.getString(1);
                    //image je 6, ovo je za probu samo
                    image = data.getBlob(2);

                }

                if (itemID > -1) {
                    //ima podatke
                    ToastMessage("On item click: --ID IS : -- " + itemID + " --FIRST NAME --" + first + "--BYTE TYPE--" + image);
                } else {
                    ToastMessage("NO DATA !!!");
                }
            }

        });
        return row;
    }


    private void ToastMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

         */



