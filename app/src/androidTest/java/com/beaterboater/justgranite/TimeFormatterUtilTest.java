package com.beaterboater.justgranite;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class TimeFormatterUtilTest {
    @Test
    public void assertHelloWorld(){
        assertEquals("hello world","hello world");
    }

    @Test
    public void timeStrTest(){
        // Set up test
        long currentTime = System.currentTimeMillis();
        Context context = ApplicationProvider.getApplicationContext();
        String gaugeId = "USGS gauge";
        FlowValue flowValue = new FlowValue(312,currentTime,gaugeId,context);

        //Apply function
        String formattedTimeStr = TimeFormatterUtil.formatFreshness(flowValue);

        //Assert
        assertEquals("0 mins", formattedTimeStr);

        // Set up test
        flowValue = new FlowValue(312, currentTime - TimeUnit.HOURS.toMillis(1),gaugeId, context);

        //Apply function
        formattedTimeStr = TimeFormatterUtil.formatFreshness(flowValue);

        //Assert
        assertEquals("1 hour", formattedTimeStr);

        // Set up test
        flowValue = new FlowValue(312, currentTime - TimeUnit.DAYS.toMillis(1),gaugeId, context);

        //Apply function
        formattedTimeStr = TimeFormatterUtil.formatFreshness(flowValue);

        //Assert
        assertEquals("1 day", formattedTimeStr);

    }
}
