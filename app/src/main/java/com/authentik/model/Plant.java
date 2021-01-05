package com.authentik.model;

import java.io.Serializable;

public class Plant implements Serializable {

    private int plant_id;
    private String plant_name;
    private int readingTimeId;
    private int isActive;
    private String plantSystemStatus;

    public int getPlant_id() {
        return plant_id;
    }

    public void setPlant_id(int plant_id) {
        this.plant_id = plant_id;
    }

    public String getPlant_name() {
        return plant_name;
    }

    public void setPlant_name(String plant_name) {
        this.plant_name = plant_name;
    }

    public int getReadingTimeId() {
        return readingTimeId;
    }

    public void setReadingTimeId(int readingTimeId) {
        this.readingTimeId = readingTimeId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void setPlantSystemStatus(String status) {
        this.plantSystemStatus = status;
    }

    public String getPlantSystemStatus() {
        return this.plantSystemStatus;
    }
}
