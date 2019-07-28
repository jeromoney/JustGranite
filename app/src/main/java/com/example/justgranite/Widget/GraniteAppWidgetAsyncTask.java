package com.example.justgranite.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class GraniteAppWidgetAsyncTask extends com.example.justgranite.DownloadAsyncTask {
    GraniteAppWidgetAsyncTask(Context context,
                              ArrayList<String> riverIDs,
                              String gauge,
                              AppWidgetManager appWidgetManager,
                              int appWidgetId,
                              RemoteViews views,
                              int cellWidth){
        super(context, riverIDs, gauge, appWidgetManager, appWidgetId, views, cellWidth);
    }

}
