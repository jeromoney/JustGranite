package com.beaterboater.justgranite;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.beaterboater.justgranite.repository.StreamRepository;

import java.util.HashMap;

public class GraniteViewModel extends ViewModel {

    public final MutableLiveData<FlowValue> mFlowValue;
    public final MutableLiveData<HashMap<String,FlowValue>> mStreamValues;

    private Context mContext;


    public GraniteViewModel(){
        // create a list of livedata objects for each section
        mStreamValues = new MutableLiveData<>();
        mStreamValues.setValue(new HashMap<>());

        mFlowValue = new MutableLiveData<>();
        mFlowValue.setValue(new FlowValue(0, (long) 0, null, null));
    }

    public void setmContext(Context context){
        mContext = context;
       // set StreamValues
        TinyDB tinyDB = new TinyDB(context);
        HashMap<String,FlowValue> flowValues = new  HashMap<String,FlowValue>(){};

        RiverSection[] riverSections = RiverSectionJsonUtil.getRiverSections(context);
        for (RiverSection riverSection : riverSections){
            String riverId = riverSection.getId();
            FlowValue value = new FlowValue(0,null,riverId,context);
            // check if saved value loaded before
            if (tinyDB.getAll().containsKey(riverSection)){
                FlowValue savedFloadValue = tinyDB.getObject(riverId, FlowValue.class);
                value.setmFlow(savedFloadValue.getmFlow());
                value.setmTimeStamp(savedFloadValue.getmTimeStamp());
            }
            flowValues.put(riverId, value);
            }
        mStreamValues.setValue(flowValues);
    }

    private MutableLiveData<FlowValue> getmFlowValue(){
        return mFlowValue;
    }

    public LiveData<HashMap<String,FlowValue>> getmStreamValues() {
        return mStreamValues;
    }

    public void setmStreamValues(HashMap<String, FlowValue> flowValueHashMap){
        // need to merge values
        HashMap<String,FlowValue> oldHashmap = mStreamValues.getValue();
        for (String key: flowValueHashMap.keySet()){
            oldHashmap.put(key, flowValueHashMap.get(key));
        }
        mStreamValues.setValue(oldHashmap);
    }

    public void loadFlow(){
        boolean isDataFresh = false;
        if (getmFlowValue().getValue() != null){
            isDataFresh = getmFlowValue().getValue().isDataFresh();
        }

        if (!isDataFresh) {
            new StreamRepository(this, mContext);
        }
    }
}
