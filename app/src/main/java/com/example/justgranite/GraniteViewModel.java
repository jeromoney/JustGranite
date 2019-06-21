package com.example.justgranite;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class GraniteViewModel extends ViewModel {

    public final LiveData<String> mFlowStr;
    public final MutableLiveData<FlowValue> mFlowValue;
    public final LiveData<String> mAgeStr;
    private Context mContext;


    public GraniteViewModel(){
        mFlowValue = new MutableLiveData<>();
        mFlowStr = Transformations.map(
                mFlowValue,
                flowValue -> {
                    if (flowValue == null || flowValue.mFlow == 0) return "---";
                    else return flowValue.mFlow.toString();}
        );
        mFlowValue.setValue(new FlowValue(0, (long) 0, null));
        mAgeStr = Transformations.map(
                mFlowValue,
                TimeFormatterUtil::formatFreshness);
    }

    public void setmContext(Context context){
        mContext = context;
        // Now that I have the context I can load values in shared preferences
       FlowValue flowValue = SharedPreferencesUtil.getSavedFlowValue(context);
       if (flowValue != null) setmFlowValue(flowValue);
    }

    private MutableLiveData<FlowValue> getmFlowValue(){
        return mFlowValue;
    }

    public void setmFlowValue(FlowValue flowValue){
        if (flowValue == null) return;
        if (flowValue.getmContext() == null) flowValue.setmContext(mContext);
        mFlowValue.setValue(flowValue);
        SharedPreferencesUtil.setSavedFlowValue(mContext, flowValue);
    }

    public void loadFlow(){
        if (!getmFlowValue().getValue().isDataFresh()) {
            new DownloadXmlTask(this).execute(mContext.getString(R.string.granite_url));
        }
    }
}
