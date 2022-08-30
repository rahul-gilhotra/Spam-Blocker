package com.example.spamblocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LogsAdapter extends ArrayAdapter<Info> {
    private static final String TAG = "PersonListAdapter";

    private Context mcontext;
    int mresource;
    public LogsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Info> objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String number = getItem(position).getPhoneNumber();
        String time = getItem(position).getTime();
        String reason = getItem(position).getReason();

        Info info = new Info(number,time,reason);

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource,parent,false);

        TextView tvnumber = (TextView) convertView.findViewById(R.id.textView1);
        TextView tvTime = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvReason = (TextView) convertView.findViewById(R.id.textView3);

        tvnumber.setText(number);
        tvTime.setText(time);
        tvReason.setText(reason);

        return convertView;
    }
}
