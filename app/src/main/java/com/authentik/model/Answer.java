package com.authentik.model;

public class Answer {
    private int id;
    private String reading;
    private String image_fileName;
    private int ques_id;
    private int user_id;
    private int status;
    private String recorded_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getImage_fileName() {
        return image_fileName;
    }

    public void setImage_fileName(String image_fileName) {
        this.image_fileName = image_fileName;
    }

    public int getQues_id() {
        return ques_id;
    }

    public void setQues_id(int ques_id) {
        this.ques_id = ques_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRecorded_time() {
        return recorded_time;
    }

    public void setRecorded_time(String recorded_time) {
        this.recorded_time = recorded_time;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", reading='" + reading + '\'' +
                ", image_fileName='" + image_fileName + '\'' +
                ", ques_id=" + ques_id +
                ", user_id=" + user_id +
                ", status=" + status +
                ", recorded_time='" + recorded_time + '\'' +
                '}';
    }
}
