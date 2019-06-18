package com.example.justgranite;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadXmlTask extends AsyncTask<String, Void, FlowValue> {
    private final static String TAG = DownloadXmlTask.class.getSimpleName();
    private final GraniteViewModel mViewModel;

    public DownloadXmlTask(GraniteViewModel viewModel){
        super();
        mViewModel = viewModel;
    }


    @Override
    protected FlowValue doInBackground(String... urls) {
        try {
            return loadXmlFromNetwork(urls[0]);
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return null;
        }
        catch (XmlPullParserException e){
            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    @Override
    protected void onPostExecute(FlowValue flowValue) {
        // Got the flow. Now update ViewModel
        mViewModel.setmFlowValue(flowValue);
    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
// HTML markup. Returns HTML string.
    public FlowValue loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        // Instantiate the parser

        WaterServiceXMLParser waterServiceXMLParser = new WaterServiceXMLParser();
        FlowValue flowValue;
        try (InputStream stream = downloadUrl(urlString)) {
            flowValue = waterServiceXMLParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }

        return flowValue;
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
