package com.beaterboater.justgranite.remoteDataSource;

import com.beaterboater.justgranite.RiverSection;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StreamRetrofitClientInstance {
    private static Retrofit retrofit;

    public static  Retrofit getRetrofitInstance(RiverSection... riverSections){
        String base_url = "https://waterservices.usgs.gov";
        if (riverSections.length > 0){
            String source = riverSections[0].getSource();
            if (source == "coDWR"){
                base_url = "https://dnrweb.state.co.us";
            }
        }

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (retrofit == null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }
}
