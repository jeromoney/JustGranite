package com.example.justgranite.Repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.justgranite.AsyncTaskParams;
import com.example.justgranite.FlowValue;
import com.example.justgranite.GraniteViewModel;
import com.example.justgranite.RemoteDataSource.StreamRetrofitClientInstance;
import com.example.justgranite.RemoteDataSource.StreamValue;
import com.example.justgranite.RemoteDataSource.StreamValueService;
import com.example.justgranite.RiverSectionJsonUtil;
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

public class StreamRepository {
    private static final String TAG = StreamRepository.class.getSimpleName();
    private static GraniteViewModel mViewModel;

    public StreamRepository(GraniteViewModel viewModel, Context context){
        mViewModel = viewModel;
        updateStreamValues(context);
    }

    private static void updateStreamValues(Context context){
        ArrayList<String> riverIDs = RiverSectionJsonUtil.getRiverIDs(context); // Should this be run on the main thread?
        AsyncTaskParams params = new AsyncTaskParams(context, riverIDs, mViewModel);
        new StreamRepositoryAsyncTask().execute(params);
    }
}
