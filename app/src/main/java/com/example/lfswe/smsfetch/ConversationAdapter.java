package com.example.lfswe.smsfetch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * An extension of ArrayAdapter to convert the "Message" datatype elements into views
 * Created by MacGyver on 2/27/2017.
 */

public class ConversationAdapter extends ArrayAdapter<Message> {

    /**
     * A static class to hold a reference to the TextView objects that are called for
     */
    private static class ViewHolder {
        TextView name;
        TextView message;
    }

    public ConversationAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.message_widget, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            //if there's no view to re-use, inflate a brand new view
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.message_widget, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.message_address);
            viewHolder.message = (TextView) convertView.findViewById(R.id.message_preview);
            convertView.setTag(viewHolder);
        } else {
            //view is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(message.getName());
        viewHolder.message.setText(message.getSmsText());

        return convertView;
    }
}
