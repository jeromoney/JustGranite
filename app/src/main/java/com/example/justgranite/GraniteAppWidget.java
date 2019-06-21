package com.example.justgranite;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 */
public class GraniteAppWidget extends AppWidgetProvider {

    private static final String TAG = GraniteAppWidget.class.getSimpleName();
    private static final String MyOnClick = "MyOnclickTag";
    private static AppWidgetManager mAppWidgetManager;
    private static int[] mAppWidgetIds;

    private static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Set onClick method
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_layout_widget_2column);
        views.setOnClickPendingIntent(R.id.justgranite_widget, getPendingSelfIntent(context));
        appWidgetManager.updateAppWidget(appWidgetId, views);

        // First load flow from memory
        FlowValue flowValue = SharedPreferencesUtil.getSavedFlowValue(context);
        if (flowValue != null && flowValue.isDataGood()){
            setLayout(context, appWidgetManager, appWidgetId, flowValue);
            // if data is fresh enough, we're done. save battery and network usage.
            if (flowValue.isDataFresh()){
                return;
            }
        }
        else {
            // fill widget with no data display
            setLayout(context, appWidgetManager, appWidgetId, null);
        }
        if (!InternetUtil.isOnline(context)){
            // No internet, so nothing to be done.
            Log.d(TAG, "no internet");
            return;
        }

        // TODO - move to separate class
        // Get flow info as an async task
        new AsyncTask<Context, Void, FlowValue>(){

            @Override
            protected FlowValue doInBackground(Context... contexts) {
                FlowValue flowValue;
                DownloadXmlTask myTask = new DownloadXmlTask(null);
                try {
                    flowValue = myTask.loadXmlFromNetwork(context.getString(R.string.granite_url));
                    flowValue.setmContext(context);
                    return flowValue;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(FlowValue flowValue) {
                super.onPostExecute(flowValue);
                // Construct the RemoteViews object
                // if flowValue is null, the internet is probably off so don't update value.
                if (flowValue != null && flowValue.isDataGood()) {
                    setLayout(context, appWidgetManager, appWidgetId, flowValue);
                    // Save value to shared preferences
                    SharedPreferencesUtil.setSavedFlowValue(context, flowValue);
                }
            }
        }.execute();
    }

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

            // launch web page
            String URL = "https://waterdata.usgs.gov/monitoring-location/07087050/";
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            newIntent.setData(Uri.parse(URL));
            context.startActivity(newIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mAppWidgetIds = appWidgetIds;
        mAppWidgetManager = appWidgetManager;

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        // See the dimensions
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        // Get min width
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        appWidgetManager.updateAppWidget(new ComponentName(context, GraniteAppWidget.class), getRemoteViews(context, minWidth));
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


    private static PendingIntent getPendingSelfIntent(Context context){
        Intent intent = new Intent(context, GraniteAppWidget.class);
        intent.setAction(MyOnClick);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private static void setLayout(final Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, FlowValue flowValue){
        String flowStr;
        String ageStr;
        if (flowValue == null){
            flowStr = context.getString(R.string.flowDefault);
            ageStr = context.getString(R.string.ageDefault);
        }
        else {
            flowStr = flowValue.getmFlow().toString();
            ageStr = TimeFormatterUtil.formatFreshness(flowValue);
        }
        String widgetText = context.getString(R.string.cfs_format);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_layout_widget_2column);
        views.setTextViewText(R.id.appwidget_text, String.format(widgetText, flowStr));

        // Set the data freshness

        views.setTextViewText(R.id.data_freshness, ageStr);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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

}

