package com.beaterboater.justgranite.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beaterboater.justgranite.DownloadAsyncTask;
import com.beaterboater.justgranite.FlowValue;
import com.beaterboater.justgranite.RiverSection;
import com.beaterboater.justgranite.RiverSectionJsonUtil;
import com.beaterboater.justgranite.TinyDB;
import com.beaterboater.justgranite.remoteDataSource.StreamValue;
import com.beaterboater.justgranite.remoteDataSource.StreamRetrofitClientInstance;
import com.beaterboater.justgranite.remoteDataSource.StreamValueService;
import com.beaterboater.justgranite.repository.StreamRepositoryAsyncTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.max;

public class GraniteAppWidgetAsyncTask extends StreamRepositoryAsyncTask {
    private String TAG = GraniteAppWidget.class.getSimpleName();

    protected String gauge;
    protected AppWidgetManager appWidgetManager;
    protected int appWidgetId;
    protected RemoteViews views;
    protected int cellWidth;

    GraniteAppWidgetAsyncTask(Context context,
                              RiverSection[] riverSections,
                              String gauge,
                              AppWidgetManager appWidgetManager,
                              int appWidgetId,
                              RemoteViews views,
                              int cellWidth){
        super(context, riverSections, null);
        this.gauge = gauge;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        this.views = views;
        this.cellWidth = cellWidth;
    }

    @Override
    protected void updateAfterResponse(ArrayList<FlowValue> flowValues){
        GraniteAppWidgetAsyncTask.super.storeValueTinyDB(flowValues);
        FlowValue flowValue = new TinyDB(context).getObject(gauge, FlowValue.class);
        setWidget(flowValue);
    }

    private void setWidget(FlowValue flowValue){
        if (flowValue != null) flowValue.setmContext(context);
        GraniteAppWidgetUtils.setLayout(context, appWidgetManager, appWidgetId, flowValue, views, cellWidth);
    }


}
