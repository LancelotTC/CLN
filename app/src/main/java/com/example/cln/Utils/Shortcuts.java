package com.example.cln.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Shortcuts {
    public static void log(String message) {
        Log.d("", message);
    }
    public static void log(String tag, String message) {
        Log.d(tag, message);
    }
    public static void log(String tag, Object message) {
        Log.d(tag, message.toString());
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void toast(Context context, Object message) {
        Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
    }
}
