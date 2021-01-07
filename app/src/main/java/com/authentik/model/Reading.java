package com.authentik.model;

public class Reading {

    private String id;
    private int instrument_id;
    private String time;
    private String shift_id;
    private Double reading_value;
    private String date_time;
    private String image_path;
    private int system_id;
    private int plant_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInstrument_id() {
        return instrument_id;
    }

    public void setInstrument_id(int instrument_id) {
        this.instrument_id = instrument_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public Double getReading_value() {
        return reading_value;
    }

    public void setReading_value(Double reading_value) {
        this.reading_value = reading_value;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getShift_id() {
        return shift_id;
    }

    public void setShift_id(String shift_id) {
        this.shift_id = shift_id;
    }

    public int getSystem_id() {
        return system_id;
    }

    public void setSystem_id(int system_id) {
        this.system_id = system_id;
    }

    public int getPlant_id() {
        return plant_id;
    }

    public void setPlant_id(int plant_id) {
        this.plant_id = plant_id;
    }
}
