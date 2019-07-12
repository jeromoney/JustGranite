package com.example.justgranite.Repository;

import android.content.Context;

import com.example.justgranite.GraniteViewModel;
import com.example.justgranite.RiverSectionJsonUtil;

import java.util.ArrayList;

public class StreamRepository {
    private static final String TAG = StreamRepository.class.getSimpleName();
    private static GraniteViewModel mViewModel;

    public StreamRepository(GraniteViewModel viewModel, Context context){
        mViewModel = viewModel;
        updateStreamValues(context);
    }

    private static void updateStreamValues(Context context){
        ArrayList<String> riverIDs = RiverSectionJsonUtil.getRiverIDs(context); // Should this be run on the main thread?
        StreamRepositoryAsyncTaskParams params = new StreamRepositoryAsyncTaskParams(context, riverIDs, mViewModel);
        new StreamRepositoryAsyncTask().execute(params);
    }
}
