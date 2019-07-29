package com.example.justgranite.Repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.justgranite.DownloadAsyncTask;
import com.example.justgranite.FlowValue;
import com.example.justgranite.GraniteViewModel;
import com.example.justgranite.RemoteDataSource.StreamRetrofitClientInstance;
import com.example.justgranite.RemoteDataSource.StreamValue;
import com.example.justgranite.RemoteDataSource.StreamValueService;
import com.example.justgranite.TinyDB;
import com.example.justgranite.Widget.GraniteAppWidgetAsyncTask;

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

public class StreamRepositoryAsyncTask extends DownloadAsyncTask {
    private static final String TAG = StreamRepository.class.getSimpleName();

    private GraniteViewModel viewModel;

    public StreamRepositoryAsyncTask(Context context,
                             ArrayList<String> riverIDs,
                             GraniteViewModel viewModel){
        super(context,riverIDs);
        this.viewModel = viewModel;
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        if (riverIDs.size() > 0){
            Map<String, String> options = super.getOptions();

            StreamValueService service = StreamRetrofitClientInstance.getRetrofitInstance().create(StreamValueService.class);
            Call<StreamValue> call = service.getStreamsValues(options);
            call.enqueue(new Callback<StreamValue>() {
                @Override
                public void onResponse(Call<StreamValue> call, Response<StreamValue> response) {
                    HashMap<String, FlowValue> flowValueHashMap = StreamRepositoryAsyncTask.super.storeValueTinyDB(response);

                    // update viewmodel if available
                    if (viewModel != null) viewModel.setmStreamValues(flowValueHashMap);
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

}
