package com.example.justgranite.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.justgranite.FlowValue;
import com.example.justgranite.RemoteDataSource.StreamRetrofitClientInstance;
import com.example.justgranite.RemoteDataSource.StreamValue;
import com.example.justgranite.RemoteDataSource.StreamValueService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.justgranite.Widget.GraniteAppWidgetUtils.setLayout;

public class GraniteAppWidgetAsyncTask extends com.example.justgranite.DownloadAsyncTask {
    private String TAG = GraniteAppWidget.class.getSimpleName();

    protected String gauge;
    protected AppWidgetManager appWidgetManager;
    protected int appWidgetId;
    protected RemoteViews views;
    protected int cellWidth;

    GraniteAppWidgetAsyncTask(Context context,
                              ArrayList<String> riverIDs,
                              String gauge,
                              AppWidgetManager appWidgetManager,
                              int appWidgetId,
                              RemoteViews views,
                              int cellWidth){
        super(context, riverIDs);
        this.gauge = gauge;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        this.views = views;
        this.cellWidth = cellWidth;
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        if (riverIDs.size() == 0) return null;
        Map<String, String> options = super.getOptions();

        StreamValueService service = StreamRetrofitClientInstance.getRetrofitInstance().create(StreamValueService.class);
        Call<StreamValue> call = service.getStreamsValues(options);
        call.enqueue(new Callback<StreamValue>() {
            @Override
            public void onResponse(Call<StreamValue> call, Response<StreamValue> response) {
                // got my response now collapse response to list of stream flows
                HashMap<String, FlowValue> flowValueHashMap = GraniteAppWidgetAsyncTask.super.storeValueTinyDB(response);
                // At this spot, I either want to update view model or widget
                setWidget(flowValueHashMap);

            }
            @Override
            public void onFailure(Call<StreamValue> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

        return null;
    }


    private void setWidget(HashMap<String, FlowValue> flowValueHashMap){
        FlowValue flowValue = flowValueHashMap.get(gauge);
        flowValue.setmContext(context);
        setLayout(context, appWidgetManager, appWidgetId, flowValue, views, cellWidth);
    }

}
