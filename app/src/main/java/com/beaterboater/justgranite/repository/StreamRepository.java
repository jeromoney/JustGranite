package com.beaterboater.justgranite.repository;

import android.content.Context;

import com.beaterboater.justgranite.GraniteViewModel;
import com.beaterboater.justgranite.RiverSectionJsonUtil;

import java.util.ArrayList;

public class StreamRepository {
    private static GraniteViewModel mViewModel;

    public StreamRepository(GraniteViewModel viewModel, Context context){
        mViewModel = viewModel;
        updateStreamValues(context);
    }

    private static void updateStreamValues(Context context){
        ArrayList<String> riverIDs = RiverSectionJsonUtil.getRiverIDs(context); // Should this be run on the main thread?
        new StreamRepositoryAsyncTask(context, riverIDs, mViewModel).execute();
    }
}
