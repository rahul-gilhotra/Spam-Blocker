package com.example.spamblocker;

public class Info {
    String phoneNumber,time, reason;

    public Info(String phoneNumber, String time, String reason) {
        this.phoneNumber = phoneNumber;
        this.time = time;
        this.reason = reason;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }
}
