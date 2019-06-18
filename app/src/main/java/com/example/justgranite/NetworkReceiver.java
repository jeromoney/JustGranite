package com.example.justgranite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {

    private onInternetConnectedListener listener = null;

    public interface  onInternetConnectedListener {
        void onInternetConnected();
    }

    public void  setonInternetConnectedListener(Context context){
        this.listener = (onInternetConnectedListener) context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            // internet is active so refresh connection
            // check how stale data is before refreshing though
            if (listener != null){
                listener.onInternetConnected();
            }
        }
    }
}
