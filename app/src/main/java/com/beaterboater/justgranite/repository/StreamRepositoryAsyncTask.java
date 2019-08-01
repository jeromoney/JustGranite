package com.beaterboater.justgranite.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.beaterboater.justgranite.DownloadAsyncTask;
import com.beaterboater.justgranite.FlowValue;
import com.beaterboater.justgranite.GraniteViewModel;
import com.beaterboater.justgranite.R;
import com.beaterboater.justgranite.remoteDataSource.StreamRetrofitClientInstance;
import com.beaterboater.justgranite.remoteDataSource.StreamValue;
import com.beaterboater.justgranite.remoteDataSource.StreamValueService;

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
                public void onResponse(@NonNull Call<StreamValue> call, @NonNull Response<StreamValue> response) {
                    HashMap<String, FlowValue> flowValueHashMap = StreamRepositoryAsyncTask.super.storeValueTinyDB(response);

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

}
