package com.example.cln.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Convenient class that groups both logs and toast together. Not really conventional so
 * I removed its usages.
 */
public class Shortcuts {

    public static void broadcast(Context context, String tag, String message) {
        Shortcuts.log(tag, message);
        Shortcuts.toast(context, message);
    }

    public static void broadcast(Context context, String message) {
        Shortcuts.log(message);
        Shortcuts.toast(context, message);
    }

    public static void log(String message) {
        Log.d("placeholder-message", message);
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
