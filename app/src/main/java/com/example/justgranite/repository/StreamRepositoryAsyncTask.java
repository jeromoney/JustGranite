package com.example.justgranite.repository;

import android.content.Context;
import android.util.Log;

import com.example.justgranite.DownloadAsyncTask;
import com.example.justgranite.FlowValue;
import com.example.justgranite.GraniteViewModel;
import com.example.justgranite.remoteDataSource.StreamRetrofitClientInstance;
import com.example.justgranite.remoteDataSource.StreamValue;
import com.example.justgranite.remoteDataSource.StreamValueService;

import java.util.ArrayList;
import java.util.HashMap;
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
