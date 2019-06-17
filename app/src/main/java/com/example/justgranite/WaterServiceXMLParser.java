package com.example.justgranite;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * reads from https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&sites=07087050&parameterCd=00060&siteStatus=all
 */
public class WaterServiceXMLParser {
    private static String TAG = WaterServiceXMLParser.class.getSimpleName();

    private static final String ns = null;
    private Context context;

    public FlowValue parse(InputStream in) throws XmlPullParserException, IOException{
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            Log.d("i am ","here");
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {

            // in.close();
        }
    }

    private FlowValue readFeed(XmlPullParser parser) throws XmlPullParserException, IOException{
        //parser.require(XmlPullParser.START_TAG, ns, "feed");
        int flow = -1;
        int eventType = parser.getEventType();
        String timeStr;
        LocalDate localDate = null;
        SimpleDateFormat sdf;
        Date mDate = null;
        long timeInMilliseconds = 0;
        // Grab the time and flow value
        while (eventType != XmlPullParser.END_DOCUMENT){
            if (parser.getName() != null && parser.getName().equals("MeasurementTVP")) {
                for (int i=0;i<2;i++) parser.next();
                // time tag
                timeStr = parser.getText();
                // Got the time as a String and now convert to a milliseconds since epoch
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                try {
                    mDate = sdf.parse(timeStr);
                    timeInMilliseconds = mDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // jump ahead 4 tags to the flow value
                for (int i=0;i<3;i++) parser.next();
                String flowText = parser.getText();
                flow = Integer.valueOf(flowText);
                return new FlowValue(flow, timeInMilliseconds, null);
            }

            eventType = parser.next();
        }
        return null;
    }

}
