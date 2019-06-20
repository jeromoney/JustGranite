package com.example.justgranite;

import android.content.Context;

public class FlowValue {
    public Integer mFlow;
    public Long mTimeStamp;
    private Context mContext;

    public FlowValue(int flow, Long timeStamp, Context context){
        setmFlow(flow);
        setmDate(timeStamp);
        setmContext(context);
    }

    private void setmDate(Long date) {
        mTimeStamp = date;
    }

    private void setmFlow(int flow) {
        mFlow = flow;
    }


    public Integer getmFlow() {
        return mFlow;
    }

    public Long getmTimeStamp(){
        return mTimeStamp;
    }

    // It seems hokey to have to store context here but need it to access string resources in the
    // transformations map
    public void setmContext(Context context){
        mContext = context;
    }

    public Context getmContext(){
        return mContext;
    }
}