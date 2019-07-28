package com.example.justgranite;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.justgranite.Repository.StreamRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class GraniteViewModel extends ViewModel {

    public final LiveData<String> mFlowStr;
    public final MutableLiveData<FlowValue> mFlowValue;
    public final LiveData<String> mAgeStr;
    public final MutableLiveData<HashMap<String,FlowValue>> mStreamValues;

    private Context mContext;


    public GraniteViewModel(){
        // create a list of livedata objects for each section
        mStreamValues = new MutableLiveData<>();
        mStreamValues.setValue(new HashMap<String,FlowValue>());

        mFlowValue = new MutableLiveData<>();
        mFlowStr = Transformations.map(
                mFlowValue,
                flowValue -> {
                    if (flowValue == null || flowValue.mFlow == 0) return "---";
                    else return flowValue.mFlow.toString();}
        );
        mFlowValue.setValue(new FlowValue(0, (long) 0, null, null));
        mAgeStr = Transformations.map(
                mFlowValue,
                TimeFormatterUtil::formatFreshness);
    }

    public void setmContext(Context context){
        mContext = context;
       // set StreamValues
        TinyDB tinyDB = new TinyDB(context);
        HashMap<String,FlowValue> flowValues = new  HashMap<String,FlowValue>(){};

        ArrayList<String> riverIds = RiverSectionJsonUtil.getRiverIDs(context);
        for (String riverId : riverIds){
            FlowValue value = new FlowValue(0,null,riverId,context);
            // check if saved value loaded before
            if (tinyDB.getAll().containsKey(riverId)){
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
    };

    public void setmStreamValues(HashMap<String, FlowValue> flowValueHashMap){
        mStreamValues.setValue(flowValueHashMap);
    }

    public void loadFlow(){
        if (!getmFlowValue().getValue().isDataFresh()) {
            new StreamRepository(this, mContext);
        }
    }
}
