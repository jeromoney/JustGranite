package com.example.justgranite.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


import com.example.justgranite.FlowValue;
import com.example.justgranite.InternetUtil;
import com.example.justgranite.R;
import com.example.justgranite.RiverSection;
import com.example.justgranite.RiverSectionJsonUtil;
import com.example.justgranite.TinyDB;

import java.util.ArrayList;

import static com.example.justgranite.Widget.GraniteAppWidgetUtils.setLayout;

/**
 * Implementation of App Widget functionality.
 */
public class GraniteAppWidget extends AppWidgetProvider {

    private static final String TAG = GraniteAppWidget.class.getSimpleName();
    private static final String MyOnClick = "MyOnclickTag";
    private static AppWidgetManager mAppWidgetManager;
    private static int[] mAppWidgetIds;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (MyOnClick.equals(intent.getAction())){
            if (!InternetUtil.isOnline(context)){
                // If there is no internet, nothing can be done.
                Log.d(TAG, "no internet");
                return;
            }


            // User clicked on widget so lets update widget.
            onUpdate(context, mAppWidgetManager, mAppWidgetIds);

        }
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mAppWidgetIds = appWidgetIds;
        mAppWidgetManager = appWidgetManager;


        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
            onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,options);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        // See the dimensions
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        // Get min width
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews views = getRemoteViews(context, minWidth);
        appWidgetManager.updateAppWidget(new ComponentName(context, GraniteAppWidget.class), views);

        // First load flow from memory
        String gauge = getPrefGauge(context);
        FlowValue flowValue = getSavedFlowValue(gauge, context);
        int cellWidth = getCellsForSize(minWidth);
        setLayout(context, appWidgetManager, appWidgetId, flowValue, views, cellWidth);
        if (!InternetUtil.isOnline(context) || flowValue.isDataFresh()){
            // If there is no internet or the data is fresh, there is nothing to be done.
            //return;
        }

        ArrayList<String> riverIDs = RiverSectionJsonUtil.getRiverIDs(context); // Should this be run on the main thread?
        new GraniteAppWidgetAsyncTask(context, riverIDs, gauge, appWidgetManager, appWidgetId, views, cellWidth).execute();

                // Construct the RemoteViews object
                // if flowValue is null, the internet is probably off so don't update value.
       //         if (flowValue != null && flowValue.isDataGood()) {
         //           setLayout(context, appWidgetManager, appWidgetId, flowValue, views, cellWidth);
                    // Save value to shared preferences
             super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }




    /**
     * Returns number of cells needed for given size of the widget.
     *
     * @param size Widget size in dp.
     * @return Size in number of cells.
     */
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

    /**
     * Determine appropriate view based on row or column provided.
     *
     * @param minWidth
     * @return
     */
    private RemoteViews getRemoteViews(Context context, int minWidth) {
        // First find out rows and columns based on width provided.
        int columns = getCellsForSize(minWidth);
        // Now you changing layout base on you column count
        // In this code from 1 column to 4
        // you can make code for more columns on your own.
        switch (columns) {
            case 1:  return new RemoteViews(context.getPackageName(), R.layout.activity_layout_widget_1column);
            case 2:  return new RemoteViews(context.getPackageName(), R.layout.activity_layout_widget_2column);
            default: return new RemoteViews(context.getPackageName(), R.layout.activity_layout_widget_2column);
        }
    }
    private String getPrefGauge(Context context){
        TinyDB tinyDB = new TinyDB(context);
        int gaugeInt = tinyDB.getInt("defaultGauge");
        RiverSection section = RiverSectionJsonUtil.getRiverSection(context, gaugeInt);
        String gaugeStr = section.getId();
        if (gaugeStr.equals("")) gaugeStr = context.getString(R.string.granite_gauge_id);
        return gaugeStr;
    }

    private FlowValue getSavedFlowValue(String gauge, Context context) {
        TinyDB tinyDB = new TinyDB(context);
        FlowValue flowValue = tinyDB.getObject(gauge, FlowValue.class);
        if (flowValue == null){
            flowValue = new FlowValue(0,(long) 0, gauge, context);
        }
        else if (flowValue.getmContext() == null){
            flowValue.setmContext(context);
        }
        return flowValue;
    }
}

