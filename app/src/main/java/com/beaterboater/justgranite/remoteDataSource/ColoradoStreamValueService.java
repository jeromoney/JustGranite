package com.beaterboater.justgranite.remoteDataSource;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
// example url https://dnrweb.state.co.us/DWR/DwrApiService/api/v2/telemetrystations/telemetrystation/?format=json&abbrev=PLABAICO

public interface ColoradoStreamValueService {
    @GET("DWR/DwrApiService/api/v2/telemetrystations/telemetrystation/")
    Call<ColoradoStreamValue> getStreamsValues(@QueryMap Map<String, String> options);
}
