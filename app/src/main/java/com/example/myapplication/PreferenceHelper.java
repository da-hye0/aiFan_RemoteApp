package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PreferenceHelper {
    public static void setFanList(Context context, List<FanItem> list) {
        SharedPreferences pref = context.getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString("KEY_FAN_LIST", json);
        prefsEditor.commit();
    }

    public static List<FanItem> getFanList(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
        String connectionsJSONString = pref.getString("KEY_FAN_LIST", null);
        Type type = new TypeToken<List<FanItem>>() {}.getType();
        return new Gson().fromJson(connectionsJSONString, type);
    }
}
