package com.beaterboater.justgranite.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beaterboater.justgranite.DownloadAsyncTask;
import com.beaterboater.justgranite.FlowValue;
import com.beaterboater.justgranite.GraniteViewModel;
import com.beaterboater.justgranite.R;
import com.beaterboater.justgranite.RiverSection;
import com.beaterboater.justgranite.remoteDataSource.StreamRetrofitClientInstance;
import com.beaterboater.justgranite.remoteDataSource.StreamValue;
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

public class USGSStreamRepositoryAsyncTask extends DownloadAsyncTask {
    private static final String TAG = StreamRepository.class.getSimpleName();

    private GraniteViewModel viewModel;

    public USGSStreamRepositoryAsyncTask(Context context,
                                         RiverSection[] riverSections,
                                         GraniteViewModel viewModel){
        super(context,riverSections);
        this.viewModel = viewModel;
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        if (riverSections.length > 0){
        String source = riverSections[0].getSource();
        Map<String, String> options = super.getOptions();
            StreamValueService service = StreamRetrofitClientInstance.getRetrofitInstance().create(StreamValueService.class);
            Call<StreamValue> call = service.getStreamsValues(options);
            call.enqueue(new Callback<StreamValue>() {
                @Override
                public void onResponse(@NonNull Call<StreamValue> call, @NonNull Response<StreamValue> response) {

                    ArrayList<FlowValue> flowValues = collapseResponse(response);
                    HashMap<String, FlowValue> flowValueHashMap = USGSStreamRepositoryAsyncTask.super.storeValueTinyDB(flowValues);

                    // update viewmodel if available
                    if (viewModel != null) viewModel.setmStreamValues(flowValueHashMap);
                }

                @Override
                public void onFailure(@NonNull Call<StreamValue> call, @NonNull Throwable t) {
                    Log.i(TAG, t.getMessage());
                    Toast.makeText(context, context.getString(R.string.server_failure_message), Toast.LENGTH_SHORT).show();
                }
            });
    }
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
