package com.example.justgranite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

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
                return new FlowValue(flow, time, null, context);
            }
        }
        else return null;
    }

    public static void setSavedFlowValues(Context context, ArrayList<FlowValue> flowValues){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (FlowValue flowValue: flowValues) {
            if (flowValue.isDataGood()) {

            }
        }
        editor.apply();
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

    public static int getSavedSection(Context context){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt("section", 0);
    }

    public static void  setSavedSection(Context context, int section){
        // Store values in shared preferences
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("section", section);
        editor.apply();
    }
}
