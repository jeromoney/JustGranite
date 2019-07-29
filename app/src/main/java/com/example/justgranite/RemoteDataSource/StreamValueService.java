package com.example.justgranite.RemoteDataSource;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
// example url https://waterservices.usgs.gov/nwis/iv/?format=json&sites=07087050,07094500,07091200&parameterCd=00060

public interface StreamValueService {
    @GET("nwis/iv/")
    Call<StreamValue> getStreamsValues(@QueryMap Map<String, String> options);
}
