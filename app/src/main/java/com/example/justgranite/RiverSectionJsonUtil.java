package com.example.justgranite;

import android.content.Context;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RiverSectionJsonUtil {
    private static RiverSection[] getRiverSections(Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.riversections);
        String jsonString = readTextFile(inputStream);

        // Create gson object
        Gson gson = new Gson();
        return gson.fromJson(jsonString, RiverSection[].class);
    }


    public static int getCount(Context context){
        return getRiverSections(context).length;
    }

    public static ArrayList<String> getRiverIDs(Context context){
        RiverSection[] riverSections = getRiverSections(context);
        ArrayList<String> riverIDs = new ArrayList<String>();
        for (int i = 0; i< riverSections.length; i++){
            riverIDs.add(riverSections[i].getId());
        }
        return riverIDs;
    }

    /**
     * search by order of river section
     * @param context
     * @param section
     * @return
     */
    public static RiverSection getRiverSection(Context context, int section){
        return getRiverSections(context)[section];
    }

    /**
     * Search by gauge id
     * @param context
     * @param gaugeId
     * @return
     */
    public static RiverSection getRiverSection(Context context, String gaugeId){
        for (RiverSection section: getRiverSections(context)){
            if (section.getId().equals(gaugeId)) return section;
        }
        return null;
    }

    private static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
}
