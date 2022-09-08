package com.example.spamblocker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import static com.example.spamblocker.MainActivity.flag140;
import static com.example.spamblocker.MainActivity.flaghidden;
import static com.example.spamblocker.MainActivity.flagoutsideIndia;
import static com.example.spamblocker.MainActivity.flagoutsidephonebook;
import static com.example.spamblocker.MainActivity.textInputLayout;

public class IncomingCallReceiver extends BroadcastReceiver
{
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String value = String.valueOf(textInputLayout.getEditText().getText());
        boolean cutMethod = value.equals("Reject Automatically") ? true : false;

        try {
            String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

            if (!intent.getExtras().containsKey(TelephonyManager.EXTRA_INCOMING_NUMBER)) return;

            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
            {
                int shouldblockresult = shouldBlock(number,context);
                Toast.makeText(context,String.valueOf(shouldblockresult),Toast.LENGTH_LONG).show();
                if(shouldblockresult<0)
                {
                    if(cutMethod) hangUp(context);
                    else silenceTheCall(context);

                    String pattern = "MM/dd/yyyy HH:mm:ss";
                    DateFormat df = new SimpleDateFormat(pattern);
                    Date today = Calendar.getInstance().getTime();
                    String todayAsString = df.format(today);

                    String reason="";
                    if(shouldblockresult==-1) reason = "SPAM";
                    if(shouldblockresult==-2) reason = "OUT OF INDIA";
                    if(shouldblockresult==-3) reason = "HIDDEN";
                    if(shouldblockresult==-1 || shouldblockresult==1) reason = "OUT OF CONTACTS";

                    Info info = new Info(number,todayAsString,reason);
                    saveData(context,info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int shouldBlock(String number, Context context)
    {

        boolean cut140 = flag140.isChecked();
        boolean cutforeign = flagoutsideIndia.isChecked();
        boolean cuthidden = flaghidden.isChecked();
        boolean cutoutofcontacts = flagoutsidephonebook.isChecked();

        if(cut140 && number.substring(0,6)=="+91140") return -1;

        if(cutforeign && !number.startsWith("+91")) return -2;

        if(cuthidden && number==null) return -3;

        if(cutoutofcontacts)
        {
            HashSet<String> contactNumbers = getContacts(context);

            if(contactNumbers.contains(number) || contactNumbers.contains(number.substring(3,8))) return 1;
            return -4;
        }
        return 1;
    }
    public void saveData(Context context, Info info)
    {
        TinyDB tinyDB = new TinyDB(context);
        Gson gson = new Gson();
        String json = gson.toJson(info);

        ArrayList<String> data = tinyDB.getListString("callLogs");
        if(data==null) data = new ArrayList<>();

        data.add(json);
        tinyDB.putListString("callLogs",data);

        Toast.makeText(context, "array list : "+data, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void hangUp(Context context)
    {
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (tm != null)
        {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            boolean success = tm.endCall();
        }
    }
    private void silenceTheCall(Context context)
    {
        System.out.println("silencing the call");

        AudioManager audioManager;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Toast.makeText(context,"in this method",Toast.LENGTH_LONG).show();
            int adJustMute;
            if (true) adJustMute = AudioManager.ADJUST_MUTE;
            else adJustMute = AudioManager.ADJUST_UNMUTE;

            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, adJustMute, 0);
        }
    }
    public HashSet<String> getContacts(Context context)
    {
        HashSet<String> contacts = new HashSet<>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while(cursor.moveToNext())
        {
            String num =  cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(num);
        }
        return contacts;
    }
}
