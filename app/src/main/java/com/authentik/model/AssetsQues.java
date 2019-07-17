package com.authentik.model;

public class AssetsQues {
    private int id;
    private String code;
    private String question_text;
    private String unit;
    private String plant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    @Override
    public String toString() {
        return "AssetsQues{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", question_text='" + question_text + '\'' +
                ", unit='" + unit + '\'' +
                ", plant='" + plant + '\'' +
                '}';
    }
}
