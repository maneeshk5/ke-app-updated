package com.authentik.model;

import java.io.Serializable;

public class System implements Serializable {
    private int id;
    private String name;
    private String logSheet;
    private int plantId;
    private int isActive;
    private String status;

    public System() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogSheet() {
        return logSheet;
    }

    public void setLogSheet(String logSheet) {
        this.logSheet = logSheet;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
