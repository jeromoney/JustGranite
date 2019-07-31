package com.example.justgranite;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

public class FlowValue{
    public String mGaugeId;
    public Integer mFlow;
    public Long mTimeStamp;
    private Context mContext;

    public FlowValue(int flow, Long timeStamp, String gaugeId, Context context){
        setmFlow(flow);
        setmDate(timeStamp);
        setmGaugeId(gaugeId);
        setmContext(context);
    }

    private void setmDate(Long date) {
        mTimeStamp = date;
    }

    private void setmFlow(int flow) {
        mFlow = flow;
    }

    public void setmGaugeId(String gaugeId){
        mGaugeId = gaugeId;
    }

    @NonNull
    public Integer getmFlow() {
        if (mFlow != null) return mFlow;
        else return 0;
    }
    public String getmGaugeId(){ return mGaugeId;}
    public Long getmTimeStamp(){
        return mTimeStamp;
    }
    public void setmFlow(Integer flow){
        mFlow = flow;
    }
    public void setmTimeStamp(Long timeStamp){
        mTimeStamp = timeStamp;
    }
    // It seems hokey to have to store context here but need it to access string resources in the
    // transformations map
    public void setmContext(Context context){
        mContext = context;
    }

    public Context getmContext(){
        return mContext;
    }

    public boolean isDataFresh(){
        return (getmTimeStamp() != null &&
                System.currentTimeMillis() - getmTimeStamp() < TimeUnit.HOURS.toMillis(1));
    }
}
