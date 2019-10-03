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
import com.beaterboater.justgranite.remoteDataSource.StreamValue;
import com.beaterboater.justgranite.remoteDataSource.StreamRetrofitClientInstance;
import com.beaterboater.justgranite.remoteDataSource.StreamValueService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.max;

public class GraniteAppWidgetAsyncTask extends DownloadAsyncTask {
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
        super(context, riverSections);
        this.gauge = gauge;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        this.views = views;
        this.cellWidth = cellWidth;
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        if (riverSections.length == 0) return null;
        Map<String, String> options = super.getOptions();

        StreamValueService service = StreamRetrofitClientInstance.getRetrofitInstance().create(StreamValueService.class);
        Call<StreamValue> call = service.getStreamsValues(options);
        call.enqueue(new Callback<StreamValue>() {
            @Override
            public void onResponse(@NonNull Call<StreamValue> call, @NonNull Response<StreamValue> response) {
                // got my response now collapse response to list of stream flows
                ArrayList<FlowValue> flowValues = collapseResponse(response);
                HashMap<String, FlowValue> flowValueHashMap = GraniteAppWidgetAsyncTask.super.storeValueTinyDB(flowValues);
                // At this spot, I either want to update view model or widget
                setWidget(flowValueHashMap);

            }
            @Override
            public void onFailure(@NonNull Call<StreamValue> call, @NonNull Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

        return null;
    }


    private void setWidget(HashMap<String, FlowValue> flowValueHashMap){
        FlowValue flowValue = flowValueHashMap.get(gauge);
        if (flowValue != null) flowValue.setmContext(context);
        GraniteAppWidgetUtils.setLayout(context, appWidgetManager, appWidgetId, flowValue, views, cellWidth);
    }

    @Nullable
    private static ArrayList<FlowValue> collapseResponse(Response<StreamValue> response){
        List<StreamValue.StreamValueService.TimeSeries> streamValues;
        if (response.body() == null){
            return new ArrayList<FlowValue>();
        }
        streamValues = Objects.requireNonNull(response.body()).streamValueService.timeSeries;
        String gaugeId;
        int flow;
        long timeStamp;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date mDate;
        ArrayList<FlowValue> flowValues = new ArrayList<>();
        for (StreamValue.StreamValueService.TimeSeries streamValue: streamValues){
            gaugeId = streamValue.name.split(":")[1];
            flow = streamValue.streamValues.get(0).value.get(0).flow;
            flow = max(flow, 0); // When the gauge is offline, it returns -99999
            String timeStampStr = streamValue.streamValues.get(0).value.get(0).dateTime;
            // Got the time as a String and now convert to a milliseconds since epoch
            try {
                mDate = sdf.parse(timeStampStr);
                timeStamp = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                timeStamp = (long) 0;
            }
            flowValues.add(new FlowValue(flow, timeStamp, gaugeId, null));
        }
        return flowValues;
    }


}
