package com.example.myapplication;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREFERENCES_NAME =  "rebuild_preference";;
    private static final int DEFAULT_VALUE_INT = 10;
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences((Context) context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        int value = prefs.getInt(key, DEFAULT_VALUE_INT);
        return value;
    }
}
