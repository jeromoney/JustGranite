package com.beaterboater.justgranite.remoteDataSource;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ColoradoStreamValue {
    @SerializedName("ResultList")
    public List<ColoradoStreamValueService> coloradoStreamValueService;

    public class ColoradoStreamValueService{
        public String abbrev;
        public String measDateTime;
        public Integer measValue;
    }
}
