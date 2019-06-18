package com.example.justgranite;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 */
public class GraniteAppWidget extends AppWidgetProvider {

    private static final String MyOnClick = "MyOnclickTag";

    private static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // First load flow from memory
        FlowValue flowValue = SharedPreferencesUtils.getSavedFlowValue(context);
        if (flowValue != null) setLayout(context, appWidgetManager, appWidgetId, flowValue);

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
                setLayout(context, appWidgetManager, appWidgetId, flowValue);
            }
        }.execute();



    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (MyOnClick.equals(intent.getAction())){
            String URL = "https://waterdata.usgs.gov/monitoring-location/07087050/";
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            newIntent.setData(Uri.parse(URL));
            context.startActivity(newIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.granite_app_widget);
            views.setOnClickPendingIntent(R.id.appwidget_text, getPendingSelfIntent(context));
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private PendingIntent getPendingSelfIntent(Context context){
        Intent intent = new Intent(context, getClass());
        intent.setAction(MyOnClick);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private static void setLayout(final Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, FlowValue flowValue){
        String flowStr;
        if (flowValue == null){
            flowStr = context.getString(R.string.flowDefault);
        }
        else {
            flowStr = flowValue.getmFlow().toString();
        }
        String widgetText = context.getString(R.string.cfs_format);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.granite_app_widget);
        views.setTextViewText(R.id.appwidget_text, String.format(widgetText, flowStr));

        // Set the data freshness
        String AgeStr = TimeFormatterUtil.formatFreshness(flowValue);
        views.setTextViewText(R.id.data_freshness, AgeStr);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

