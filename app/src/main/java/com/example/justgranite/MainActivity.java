package com.example.justgranite;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.justgranite.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements NetworkReceiver.onInternetConnectedListener {

    private GraniteViewModel graniteViewModel;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    public void onInternetConnected() {
        // Internet is connected to refresh data.
        graniteViewModel.loadFlow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        graniteViewModel = ViewModelProviders.of(this)
                .get(GraniteViewModel.class);
        graniteViewModel.setmContext(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        binding.setGraniteviewmodel(graniteViewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Register my Broadcast receiver to track internet changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        receiver.setonInternetConnectedListener(this);



        if (isOnline() ){
            graniteViewModel.loadFlow();
        }
        else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister my receiver
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        graniteViewModel.loadFlow();
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void launchGauge(View view){
        String URL = getApplicationContext().getString(R.string.granite_gauge_url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));
        startActivity(intent);
    }

}