package com.aleksandar69.PMSU2020Tim16.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aleksandar69.PMSU2020Tim16.R;
import com.aleksandar69.PMSU2020Tim16.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsArrayAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "ContactsArrayAdapter";
    private Context mContext;
    int mResource;
    private int lastPosition = -1;
    private ArrayList<Contact> mExampleList;

    public int getItemCount() {
        return  mExampleList.size();
    }

    public ContactsArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    static class ViewHolder{
        TextView first;
    }

    @NonNull
    @Override
    //ova metoda je odgovorna za dobavljanje view-a i kacenje istog na listview
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //get the name of the contact
        String first = getItem(position).getFirst();

        //create the object with the first name
        Contact contact = new Contact(first);

        //create the view result for showing the animation
        final View result;
        //view holder object
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);
            holder  = new ViewHolder();
            holder.first = (TextView) convertView.findViewById(R.id.row_first);

            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        //declaration of animation
        //logic whether I scroll up or down
        //ako je sadasnja pozicija veca od prethodne, onda skroluj ka dole, u suprotno skroluj ka gore
        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.first.setText(first);

        return convertView;
    }
}
