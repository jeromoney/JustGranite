package com.example.justgranite.RemoteDataSource;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StreamValue {
    @SerializedName("value")
    public StreamValueService streamValueService;

    public class StreamValueService{
        @SerializedName("timeSeries")
        public List<TimeSeries> timeSeries;

        public class TimeSeries{
            @SerializedName("name")
            public String name;

            @SerializedName("values")
            public List<StreamValues> streamValues;

            public class StreamValues{
                @SerializedName("value")
                public List<JSONValue> value;

                public class JSONValue{
                    @SerializedName("value")
                    public int flow;

                    @SerializedName("dateTime")
                    public String dateTime;
                }
            }
        }
    }
}
