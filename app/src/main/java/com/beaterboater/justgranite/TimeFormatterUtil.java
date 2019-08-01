package com.beaterboater.justgranite;

import android.content.res.Resources;

import com.beaterboater.justgranite.R;

import java.util.concurrent.TimeUnit;

public class TimeFormatterUtil {

    public static String formatFreshness(FlowValue flowValue){
        if (flowValue == null || flowValue.mFlow == 0) return "";
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - flowValue.mTimeStamp;
        String timeDisplay;
        Resources resources = flowValue.getmContext().getResources();

        if (timeInterval < TimeUnit.HOURS.toMillis(1)){
            // If the age of the data is less than one hour, display as seconds
            int  minutesInterval = (int) TimeUnit.MILLISECONDS.toMinutes(timeInterval);
            timeDisplay = resources.getQuantityString(R.plurals.mins_format, minutesInterval, minutesInterval);
        }
        else if (timeInterval < TimeUnit.DAYS.toMillis(1)){
            // If data is less than one day old show as hours
            int hoursInterval = (int) TimeUnit.MILLISECONDS.toHours(timeInterval);
            timeDisplay = resources.getQuantityString(R.plurals.hours_format, hoursInterval, hoursInterval);
        }
        else {
            // show as days
            int daysInterval = (int) TimeUnit.MILLISECONDS.toDays(timeInterval);
            timeDisplay = resources.getQuantityString(R.plurals.days_format, daysInterval, daysInterval);
        }
        return timeDisplay;
    }
}
