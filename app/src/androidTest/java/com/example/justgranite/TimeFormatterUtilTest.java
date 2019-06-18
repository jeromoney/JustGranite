package com.example.justgranite;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TimeFormatterUtilTest {
    @Test
    public void formatFreshnessTest(){
        // Set up test
        Long currentTime = System.currentTimeMillis();
        Context context = ApplicationProvider.getApplicationContext();
        FlowValue flowValue = new FlowValue(213, currentTime, context);

        // apply function
        String formattedTimeInterval = TimeFormatterUtil.formatFreshness(flowValue);

        // test returned value
        assertTrue(formattedTimeInterval.equals("0 mins"));
    }
}
