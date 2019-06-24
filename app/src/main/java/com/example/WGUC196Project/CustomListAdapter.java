package com.example.WGUC196Project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

////this sets up the adapter for a customized layout of the lists in the assessment. course, and term list screens

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> values;
    private ArrayList<String> info;

    public CustomListAdapter(Context context, ArrayList<String> values, ArrayList<String> info) {
        super(context, R.layout.custom_list_with_image, values);
        this.context = context;
        this.values = values;
        this.info = info;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.custom_list_with_image, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView infoText = (TextView) rowView.findViewById(R.id.infoTextViewID);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);


        textView.setText(values.get(position));
        infoText.setText(info.get(position));
        imageView.setImageResource(R.mipmap.ic_arrowdetails_foreground);

        // Change icon based on name
        String s = values.get(position);

        System.out.println(s);

        return rowView;
    }

}
