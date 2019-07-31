package com.example.justgranite;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.example.justgranite.remoteDataSource.StreamValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Response;

public class DownloadAsyncTask extends AsyncTask<Void, Void, Void> {
    protected Context context;
    protected ArrayList<String> riverIDs;

    public DownloadAsyncTask(
            Context context,
            ArrayList<String> riverIDs){
        this.riverIDs = riverIDs;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
    return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    /**
     * The JSON response returned by the USGS IVS service is quite detailed. This function extracts
     * just the data we want.
     * @param response the raw internet response from USGS service
     * @return a list of gauge data flow/date/gauge
     */
    @Nullable
    private static ArrayList<FlowValue> collapseResponse(Response<StreamValue> response){
        List<StreamValue.StreamValueService.TimeSeries> streamValues;
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

    public HashMap<String, FlowValue> storeValueTinyDB(Response<StreamValue> response){
        ArrayList<FlowValue> flowValues = collapseResponse(response);
        // i now have the flows from all gauges so store it in shared preferences
        TinyDB tinydb = new TinyDB(context);
        HashMap<String, FlowValue> flowValueHashMap = new HashMap<>();
        for (FlowValue flowValue: Objects.requireNonNull(flowValues)){
            tinydb.putObject(flowValue.getmGaugeId(),flowValue);
            flowValueHashMap.put(flowValue.getmGaugeId(), flowValue);
        }
        return flowValueHashMap;
    }

    public Map<String, String> getOptions(){
        Map<String, String> options = new HashMap<>();
        options.put("parameterCd","00060"); //parameterCd=00060
        options.put("format","json"); //format=json
        String riverIDsStr = android.text.TextUtils.join(",", riverIDs); //there is probably a native retrofit way to do this.
        options.put("site",riverIDsStr);//sites=07087050,07094500,07091200
        return options;
    }
}
