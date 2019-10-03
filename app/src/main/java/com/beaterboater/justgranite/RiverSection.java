package com.beaterboater.justgranite;

public class RiverSection {

    private String id;
    private String section_name;
    private String gauge_name;
    private String acronym;
    private String image;
    private String source;

    public String getId() {
        return id;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getGauge_name() {
        return gauge_name;
    }

    public String getSource(){return source;}

    public String getAcronym() {
        if (acronym != null){
            return acronym;
        }
        else {
            return "";
        }
    }

    public String getImage() {
        return null;
    }
}
