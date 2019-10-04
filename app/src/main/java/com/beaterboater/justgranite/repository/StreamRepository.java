package com.beaterboater.justgranite.repository;

import android.content.Context;

import com.beaterboater.justgranite.GraniteViewModel;
import com.beaterboater.justgranite.RiverSection;
import com.beaterboater.justgranite.RiverSectionJsonUtil;

public class StreamRepository {
    private static GraniteViewModel mViewModel;

    public StreamRepository(GraniteViewModel viewModel, Context context){
        mViewModel = viewModel;
        updateStreamValues(context);
    }

    private static void updateStreamValues(Context context){
        RiverSection[] USGSriverSections = RiverSectionJsonUtil.getRiverSections(context, "usgs");
        RiverSection[] coDWRiverSections = RiverSectionJsonUtil.getRiverSections(context, "coDWR"); // Should this be run on the main thread?
        new StreamRepositoryAsyncTask(context, USGSriverSections, mViewModel).execute();
        new StreamRepositoryAsyncTask(context, coDWRiverSections, mViewModel).execute();
    }
}
