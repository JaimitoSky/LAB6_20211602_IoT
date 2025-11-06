package com.example.lab6_20211602_iot.util;


import android.content.Context;
import android.widget.Toast;


public class Toaster {
    public static void show(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }
}