package com.authentik.model;

import java.io.Serializable;

public class Reading implements Serializable {

    private String id;
    private int instrument_id;
    private String time;
    private String shift_id;
    private Double reading_value;
    private String date_time;
    private byte[] image_path;
    private int system_id;
    private int plant_id;
    private String system_status;
    private int sync_status;
    private String user_name;
    private int user_id;
    private String recorded_at;

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

    public byte[] getImage_path() {
        return image_path;
    }

    public void setImage_path(byte[] image_path) {
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

    public String getSystem_status() {
        return system_status;
    }

    public void setSystem_status(String system_status) {
        this.system_status = system_status;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getRecorded_at() {
        return recorded_at;
    }

    public void setRecorded_at(String recorded_at) {
        this.recorded_at = recorded_at;
    }
}
