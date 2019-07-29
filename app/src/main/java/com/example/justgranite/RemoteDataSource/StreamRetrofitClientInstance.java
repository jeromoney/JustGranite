package com.example.justgranite.RemoteDataSource;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StreamRetrofitClientInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://waterservices.usgs.gov";

    public static  Retrofit getRetrofitInstance(){
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (retrofit == null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();
        }
        return retrofit;
    }
}
