package com.example.justgranite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
       FlowValue flowValue = SharedPreferencesUtils.getSavedFlowValue(context);
       if (flowValue != null) setmFlowValue(flowValue);
    }

    public void setmFlowValue(FlowValue flowValue){
        if (flowValue == null) return;
        if (flowValue.getmContext() == null) flowValue.setmContext(mContext);
        mFlowValue.setValue(flowValue);
        // Store values in shared preferences
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("time", flowValue.getmTimeStamp());
        editor.putInt("flow", flowValue.getmFlow());
        editor.apply();
    }

    public void loadFlow(){
        if (!isDataFresh()) {
            new DownloadXmlTask(this).execute(mContext.getString(R.string.granite_url));
        }
    }

    private boolean isDataFresh(){
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - Objects.requireNonNull(mFlowValue.getValue()).mTimeStamp;
        return (timeInterval < TimeUnit.HOURS.toMillis(1));
    }

}
