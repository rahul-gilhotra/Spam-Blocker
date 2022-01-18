package com.example.spamblocker;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    String[] items =  {"Reject Automatically","Ring Silent"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    static Switch flag140, flagoutsidephonebook, flagoutsideIndia, flaghidden ;
    static AutoCompleteTextView autoCompleteTextView;
    static TextInputLayout textInputLayout;

    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!foregroundServiceRunning()) {
            Intent serviceIntent = new Intent(this, MyForegroundService.class);
            startForegroundService(serviceIntent);
        }


        flag140 = (Switch) findViewById(R.id.btn140);
        flagoutsidephonebook = (Switch) findViewById(R.id.btnphnbook);
        flagoutsideIndia = (Switch) findViewById(R.id.btnforeign);
        flaghidden = (Switch) findViewById(R.id.btnhidden);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_txt);
        textInputLayout = (TextInputLayout) findViewById(R.id.dropdown);

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        flag140.setChecked(sharedPreferences.getBoolean("value", false));
        flagoutsidephonebook.setChecked(sharedPreferences.getBoolean("value2", false));
        flagoutsideIndia.setChecked(sharedPreferences.getBoolean("value3",false));
        flaghidden.setChecked(sharedPreferences.getBoolean("value4",false));
        autoCompleteTextView.setText(items[sharedPreferences.getInt("cutMethod", 1)]);


        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,items);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = getSharedPreferences("save", Activity.MODE_PRIVATE).edit();
                editor.putInt("cutMethod", position);
                editor.apply();
            }
        });



        flag140.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                if (isChecked == true) {
                    editor.putBoolean("value", true);
                    editor.apply();
                    flag140.setChecked(true);
                } else {
                    editor.putBoolean("value", false);
                    editor.apply();
                    flag140.setChecked(false);
                }

            }
        });

        flagoutsidephonebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                if (isChecked == true) {
                    editor.putBoolean("value2", true);
                    editor.apply();
                    flagoutsidephonebook.setChecked(true);
                    Toast.makeText(getBaseContext(), "switch1 ON", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("value2", false);
                    editor.apply();
                    flagoutsidephonebook.setChecked(false);
                }
            }
        });

        flagoutsideIndia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                if (isChecked == true) {
                    editor.putBoolean("value3", true);
                    editor.apply();
                    flagoutsideIndia.setChecked(true);
                } else {
                    editor.putBoolean("value3", false);
                    editor.apply();
                    flagoutsideIndia.setChecked(false);
                }

            }
        });

        flaghidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                if (isChecked == true) {
                    editor.putBoolean("value4", true);
                    editor.apply();
                    flaghidden.setChecked(true);
                } else {
                    editor.putBoolean("value4", false);
                    editor.apply();
                    flaghidden.setChecked(false);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.ANSWER_PHONE_CALLS};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }
    }

    public boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }
}