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
import com.beaterboater.justgranite.remoteDataSource.ColoradoStreamRetrofitClientInstance;
import com.beaterboater.justgranite.remoteDataSource.ColoradoStreamValue;
import com.beaterboater.justgranite.remoteDataSource.ColoradoStreamValueService;
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

public class CODWRStreamRepositoryAsyncTask extends DownloadAsyncTask {
    private GraniteViewModel viewModel;
    private static final String TAG = CODWRStreamRepositoryAsyncTask.class.getSimpleName();


    public CODWRStreamRepositoryAsyncTask(Context context, RiverSection[] riverSections, GraniteViewModel viewModel) {
        super(context, riverSections);
        this.viewModel = viewModel;
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        if (riverSections.length > 0){
            String source = riverSections[0].getSource();
            Map<String, String> options = super.getOptions();
            ColoradoStreamValueService service = ColoradoStreamRetrofitClientInstance.getRetrofitInstance().create(ColoradoStreamValueService.class);
            Call<ColoradoStreamValue> call = service.getStreamsValues(options);
            call.enqueue(new Callback<ColoradoStreamValue>() {
                @Override
                public void onResponse(@NonNull Call<ColoradoStreamValue> call, @NonNull Response<ColoradoStreamValue> response) {
                    // Convert to Flowvalue and store

                    ArrayList<FlowValue> flowValues = collapseResponse(response);
                    HashMap<String, FlowValue> flowValueHashMap = CODWRStreamRepositoryAsyncTask.super.storeValueTinyDB(flowValues);

                    // update viewmodel if available
                    if (viewModel != null) viewModel.setmStreamValues(flowValueHashMap);
                }

                @Override
                public void onFailure(@NonNull Call<ColoradoStreamValue> call, @NonNull Throwable t) {
                    Log.i(TAG, t.getMessage());
                    Toast.makeText(context, context.getString(R.string.server_failure_message), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }


    @Nullable
    private static ArrayList<FlowValue> collapseResponse(Response<ColoradoStreamValue> response){
        List<ColoradoStreamValue.ColoradoStreamValueService> streamValues;
        if (response.body() == null){
            return new ArrayList<FlowValue>();
        }
        streamValues = response.body().coloradoStreamValueService;
        String gaugeId;
        int flow;
        long timeStamp;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"); // 2019-10-03T15:15:00-06:00
        Date mDate;
        ArrayList<FlowValue> flowValues = new ArrayList<>();
        for (ColoradoStreamValue.ColoradoStreamValueService streamvalue: streamValues){
            gaugeId = streamvalue.abbrev;
            flow = streamvalue.measValue;
            String timeStampStr = streamvalue.measDateTime;

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
