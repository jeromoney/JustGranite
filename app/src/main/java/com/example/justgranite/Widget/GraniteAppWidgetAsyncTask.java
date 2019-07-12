package com.example.justgranite.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import static com.example.justgranite.Widget.GraniteAppWidgetUtils.setLayout;
import com.example.justgranite.FlowValue;
import com.example.justgranite.RemoteDataSource.StreamRetrofitClientInstance;
import com.example.justgranite.RemoteDataSource.StreamValue;
import com.example.justgranite.RemoteDataSource.StreamValueService;
import com.example.justgranite.TinyDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraniteAppWidgetAsyncTask extends AsyncTask<Void, Void, FlowValue> {
    private static final String TAG = GraniteAppWidgetAsyncTask.class.getSimpleName();
    private Context context;
    private ArrayList<String> riverIDs;
    private String gauge;
    private AppWidgetManager appWidgetManager;
    private int appWidgetId;
    private RemoteViews views;
    private int cellWidth;

    public GraniteAppWidgetAsyncTask(
            Context context,
            ArrayList<String> riverIDs,
            String gauge,
            AppWidgetManager appWidgetManager,
            int appWidgetId,
            RemoteViews views,
            int cellWidth){
        this.riverIDs = riverIDs;
        this.context = context;
        this.gauge = gauge;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        this.views = views;
        this.cellWidth = cellWidth;
    }

    @Override
    protected FlowValue doInBackground(Void... aVoid) {
        if (riverIDs.size() > 0){
            Map<String, String> options = new HashMap<>();
            options.put("parameterCd","00060"); //parameterCd=00060
            options.put("format","json"); //format=json
            String riverIDsStr = android.text.TextUtils.join(",", riverIDs); //there is probably a native retrofit way to do this.
            options.put("site",riverIDsStr);//sites=07087050,07094500,07091200

            StreamValueService service = StreamRetrofitClientInstance.getRetrofitInstance().create(StreamValueService.class);
            Call<StreamValue> call = service.getStreamsValues(options);
            call.enqueue(new Callback<StreamValue>() {
                @Override
                public void onResponse(Call<StreamValue> call, Response<StreamValue> response) {
                    // got my response now collapse response to list of stream flows
                    ArrayList<FlowValue> flowValues = collapseResponse(response);
                    // i now have the flows from all gauges so store it in shared prefences
                    TinyDB tinydb = new TinyDB(context);
                    HashMap<String, FlowValue> flowValueHashMap = new HashMap<>();
                    for (FlowValue flowValue: flowValues){
                        tinydb.putObject(flowValue.getmGaugeId(),flowValue);
                        flowValueHashMap.put(flowValue.getmGaugeId(), flowValue);
                    }

                    // update widget
                    FlowValue flowValue = flowValueHashMap.get(gauge);
                    flowValue.setmContext(context);
                    setLayout(context, appWidgetManager, appWidgetId, flowValue, views, cellWidth);

                }

                @Override
                public void onFailure(Call<StreamValue> call, Throwable t) {
                    Log.i(TAG, t.getMessage());
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(FlowValue flowValue) {
        super.onPostExecute(flowValue);

    }

    /**
     * The JSON response returned by the USGS IVS service is quite detailed. This function extracts
     * just the data we want.
     * @param response
     * @return a list of gauge data flow/date/gauge
     */
    private static ArrayList<FlowValue> collapseResponse(Response<StreamValue> response){
        List<StreamValue.StreamValueService.TimeSeries> streamValues = response.body().streamValueService.timeSeries;
        String gaugeId;
        int flow;
        Long timeStamp;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date mDate;
        ArrayList<FlowValue> flowValues = new ArrayList<>();
        for (StreamValue.StreamValueService.TimeSeries streamValue: streamValues){
            gaugeId = streamValue.name.split(":")[1];
            flow = streamValue.streamValues.get(0).value.get(0).flow;
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
