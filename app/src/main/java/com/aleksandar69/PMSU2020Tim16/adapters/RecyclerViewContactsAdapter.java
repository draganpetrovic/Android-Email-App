package com.aleksandar69.PMSU2020Tim16.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.activities.ContactActivity;
import com.aleksandar69.PMSU2020Tim16.models.Contact;
import com.aleksandar69.PMSU2020Tim16.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewContactsAdapter extends RecyclerView.Adapter<RecyclerViewContactsAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contactList;
    //sadrzi listu upload-a
    private List<Photo> mPhotos;
    public ImageView mImageView;
    public TextView mFirst;

    //konstrutkor koji mi ucitava sliku i ime
    //zakomentarisala sam konstruktor od maloprije
    public RecyclerViewContactsAdapter(Context context, List<Photo> photos) {
        this.context  = context;
        mPhotos = photos;
    }

    /*
    public RecyclerViewContactsAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }
     */
    public void refreshBlockOverlay(int position) {
        notifyItemChanged(position);
    }

    public RecyclerViewContactsAdapter() {
    }

    @NonNull
    @Override
    public RecyclerViewContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewContactsAdapter.ViewHolder holder, int position) {
        Photo uploadCurrent = mPhotos.get(position);
        holder.contactName.setText(uploadCurrent.getName());
        Picasso.get().load(uploadCurrent.getPath()).placeholder(R.drawable.person_icon).fit().centerCrop().into(holder.iconButton);
        /*
        Contact currentItem = contactList.get(position);
        String imageURL = currentItem.getUrl();
        String first = currentItem.getFirst();
        holder.contactName.setText(first);
        Picasso.get().load(imageURL).fit().centerInside().into(holder.iconButton);
         */
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView contactName;
        public ImageView iconButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconButton = itemView.findViewById(R.id.imageViewUpload);
            contactName = itemView.findViewById(R.id.row_first);
            itemView.setOnClickListener(this);

            iconButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            Contact contact = contactList.get(position);
            String first = contact.getFirst();
            String last = contact.getLast();
            String display = contact.getDisplay();
            String email = contact.getEmail();
            //int photoId = contact.getImageSourceID();

            Toast.makeText(context, "The position is " + String.valueOf(position) +
                    ",First name is " + first +
                    ",Last name is " + last +
                    ",Display " + display +
                    ",Email is " + email,Toast.LENGTH_LONG).show();

            Intent intent = new Intent(context, ContactActivity.class);
            intent.putExtra("RFirst" ,first);
            intent.putExtra("RLast",last);
            intent.putExtra("RDisplay", display);
            intent.putExtra("REmail",email);
            //intent.putExtra("RPhoto", photoId);

            context.startActivity(intent);
        }
    }
}

