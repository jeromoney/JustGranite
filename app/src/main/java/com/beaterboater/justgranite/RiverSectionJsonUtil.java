package com.beaterboater.justgranite;

import android.content.Context;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RiverSectionJsonUtil {
    public static RiverSection[] getRiverSections(Context context, String... selection) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.riversections);
        String jsonString = readTextFile(inputStream);

        // Create gson object
        Gson gson = new Gson();
        if (selection.length == 0) {
            // return all sections
            return gson.fromJson(jsonString, RiverSection[].class);
        }
        else {
            // return only selections
            RiverSection[] allSections = gson.fromJson(jsonString, RiverSection[].class);
            ArrayList<RiverSection> selectedSections = new ArrayList<>();
            int counter = 0;
            for (RiverSection riverSection: allSections){
                if (riverSection.getSource().equals(selection[0])){
                    counter++;
                    selectedSections.add(riverSection);
                }
            }
            return selectedSections.toArray(new RiverSection[counter]);
        }

    }


    public static int getCount(Context context){
        return getRiverSections(context).length;
    }


    /**
     * search by order of river section
     * @return the metadata for that river section
     */
    public static RiverSection getRiverSection(Context context, int section){
        return getRiverSections(context)[section];
    }

    /**
     * Search by gauge id
     * @param gaugeId the USGS string to identify gauges
     * @return the metadata for that river section
     */
    public static RiverSection getRiverSection(Context context, String gaugeId){
        for (RiverSection section: getRiverSections(context)){
            if (section.getId().equals(gaugeId)) return section;
        }
        return null;
    }

    private static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException ignored) {
        }
        return outputStream.toString();
    }
}
