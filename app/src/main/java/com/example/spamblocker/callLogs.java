package com.example.spamblocker;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

public class callLogs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        ListView myListView = findViewById(R.id.myListView);
        TinyDB tinyDB = new TinyDB(this);
        Gson gson = new Gson();

        ArrayList<String> data = tinyDB.getListString("callLogs");
        if(data==null) data = new ArrayList<>();

        ArrayList<Info> list = new ArrayList<>();
        for(int i=0;i<data.size();i++) list.add(gson.fromJson(data.get(i),Info.class));

        LogsAdapter adapter = new LogsAdapter(this,R.layout.adapter_view_layout,list);
        myListView.setAdapter(adapter);
    }
}