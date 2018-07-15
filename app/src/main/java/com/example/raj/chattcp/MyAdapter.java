package com.example.raj.chattcp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Raj on 11-Jul-18.
 */

class MyAdapter extends ArrayAdapter<Data>{

    public MyAdapter(@NonNull Context context, ArrayList<Data> messageArray) {
        super(context, 0, messageArray );
    }
    public View getView(int postion ,View contentview ,ViewGroup GroupParent){
        View listItemView = contentview;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.message, GroupParent,false);
        }
        Data example = getItem(postion);
        TextView message = (TextView) listItemView.findViewById(R.id.message);
        message.setText(example.message);
        if(example.server)
        {
            message.setGravity(Gravity.RIGHT);
            message.setBackgroundColor(135206250);
        }
        else {
            message.setGravity(Gravity.LEFT);
            message.setBackgroundColor(255222173);
        }
        return listItemView;
    }
}
