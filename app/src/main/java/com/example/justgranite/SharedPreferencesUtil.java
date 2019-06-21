package com.example.justgranite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class SharedPreferencesUtil {
    public static FlowValue getSavedFlowValue(Context context){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains("time") && sharedPreferences.contains("flow")){
            // shared preference file exists so load data
            Long time = sharedPreferences.getLong("time",0);
            Integer flow = sharedPreferences.getInt("flow", 0);
            if (time == 0 || flow == 0){
                // bad value in shared preferences
                return null;
            }
            else {
                return new FlowValue(flow, time, context);
            }
        }
        else return null;
    }

    public static void  setSavedFlowValue(Context context, FlowValue flowValue){
        // Store values in shared preferences
        if (flowValue.isDataGood()) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("time", flowValue.getmTimeStamp());
            editor.putInt("flow", flowValue.getmFlow());
            editor.apply();
        }
    }
}
