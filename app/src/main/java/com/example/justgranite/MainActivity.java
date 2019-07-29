package com.example.justgranite;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;

import com.example.justgranite.Widget.GraniteAppWidget;


public class MainActivity extends AppCompatActivity implements NetworkReceiver.onInternetConnectedListener,
RiverSectionFragment.OnFragmentInteractionListener{

    private GraniteViewModel graniteViewModel;
    private NetworkReceiver receiver = new NetworkReceiver();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup my ViewModel

        graniteViewModel = ViewModelProviders.of(this)
                .get(GraniteViewModel.class);
        graniteViewModel.setmContext(this);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // update the widget with the selected gauge
        Intent intent = new Intent(this, GraniteAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), GraniteAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Register my Broadcast receiver to track internet changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        receiver.setonInternetConnectedListener(this);

        if (InternetUtil.isOnline(getApplicationContext())){
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

    @Override
    public void onInternetConnected() {
        // Internet is connected to refresh data.
        graniteViewModel.loadFlow();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
