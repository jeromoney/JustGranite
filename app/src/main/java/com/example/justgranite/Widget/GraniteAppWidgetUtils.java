package com.example.justgranite.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.justgranite.FlowValue;
import com.example.justgranite.R;
import com.example.justgranite.RiverSection;
import com.example.justgranite.RiverSectionJsonUtil;
import com.example.justgranite.TimeFormatterUtil;

public class GraniteAppWidgetUtils {
    public static void setLayout(final Context context, AppWidgetManager appWidgetManager,
                                  int appWidgetId, FlowValue flowValue, RemoteViews views, int cellWidth){
        String flowStr;
        String ageStr;
        if (flowValue == null){
            flowStr = context.getString(R.string.flowDefault);
            ageStr = context.getString(R.string.ageDefault);
        }
        else {
            flowStr = flowValue.getmFlow().toString();
            // look up initial for gauge and add to age str
            RiverSection section = RiverSectionJsonUtil.getRiverSection(context, flowValue.mGaugeId);
            ageStr = TimeFormatterUtil.formatFreshness(flowValue) + String.format(" (%s)", section.getAcronym());
        }
        String widgetText = context.getString(R.string.cfs_format);
        switch(cellWidth){
            case 1:
                views.setTextViewText(R.id.appwidget_text, flowValue.mFlow.toString());
                break;

            default:
                views.setTextViewText(R.id.appwidget_text, String.format(widgetText, flowStr));
                views.setTextViewText(R.id.data_freshness, ageStr);
                break;
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}
