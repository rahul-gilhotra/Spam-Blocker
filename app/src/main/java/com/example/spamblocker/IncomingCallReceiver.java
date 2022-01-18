package com.example.spamblocker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import static com.example.spamblocker.MainActivity.flag140;
import static com.example.spamblocker.MainActivity.flaghidden;
import static com.example.spamblocker.MainActivity.flagoutsideIndia;
import static com.example.spamblocker.MainActivity.flagoutsidephonebook;
import static com.example.spamblocker.MainActivity.textInputLayout;

public class IncomingCallReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        String value = String.valueOf(textInputLayout.getEditText().getText());
        boolean cutMethod = value.equals("Reject Automatically") ? true : false;

        try {
            String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

            if (!intent.getExtras().containsKey(TelephonyManager.EXTRA_INCOMING_NUMBER)) return;

            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
            {
                if(shouldBlock(number))
                {
                    if(cutMethod) hangUp(context);
                    else silenceTheCall();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean shouldBlock(String number)
    {

        boolean cut140 = flag140.isChecked();
        boolean cutforeign = flagoutsideIndia.isChecked();
        boolean cuthidden = flaghidden.isChecked();
        boolean cutoutofcontacts = flagoutsidephonebook.isChecked();

        if(cut140 && number.substring(0,6)=="+91140") return true;

        if(cutforeign && number.substring(0,4)!="+91") return true;

        if(cuthidden && number=="null") return true;

        return true;
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
            return;
        }
    }
    public void silenceTheCall()
    {

    }
}
