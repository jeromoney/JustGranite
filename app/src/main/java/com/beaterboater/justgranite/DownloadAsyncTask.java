package com.beaterboater.justgranite;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.beaterboater.justgranite.remoteDataSource.StreamValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Response;

import static java.lang.Math.max;

public class DownloadAsyncTask extends AsyncTask<Void, Void, Void> {
    protected Context context;
    protected RiverSection[] riverSections;

    public DownloadAsyncTask(
            Context context,
            RiverSection[] riverSections){
        this.riverSections = riverSections;
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


    public HashMap<String, FlowValue> storeValueTinyDB(ArrayList<FlowValue> flowValues){
        // i now have the flows from all gauges so store it in shared preferences
        TinyDB tinydb = new TinyDB(context);
        HashMap<String, FlowValue> flowValueHashMap = new HashMap<>();
        for (FlowValue flowValue: Objects.requireNonNull(flowValues)){
            tinydb.putObject(flowValue.getmGaugeId(),flowValue);
            flowValueHashMap.put(flowValue.getmGaugeId(), flowValue);
        }
        return flowValueHashMap;
    }

    public Map<String, String> getOptions(){ //TODO - this won't work when adding Colorado DWR
        Map<String, String> options = new HashMap<>();

        options.put("format","json"); //format=json
        String riverIDsStr = riverSections[0].getId();
        for (int i = 1; i < riverSections.length; i++){
            riverIDsStr = riverIDsStr + "," + riverSections[i].getId();
        }
        if (riverSections[0].getSource().equals("usgs")) {
            options.put("parameterCd", "00060"); //parameterCd=00060
            options.put("site",riverIDsStr);
        }
        else if (riverSections[0].getSource().equals("coDWR")){
            options.put("abbrev",riverIDsStr);
        }
        return options;
    }
}
