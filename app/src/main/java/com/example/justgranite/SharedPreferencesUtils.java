package com.example.justgranite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtils {
    public static FlowValue getSavedFlowValue(Context context){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains("time") && sharedPreferences.contains("flow")){
            // shared preference file exists so load data
            Long time = sharedPreferences.getLong("time",0);
            Integer flow = sharedPreferences.getInt("flow", 0);
            FlowValue flowValue = new FlowValue(flow, time, context);
            return flowValue;
        }
        else return null;
    }
}
