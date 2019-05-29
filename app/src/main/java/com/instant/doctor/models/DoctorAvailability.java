package com.instant.doctor.models;

import java.io.Serializable;
import java.util.ArrayList;

public class DoctorAvailability implements Serializable {

    private String date;
    private ArrayList<String> time;


    public DoctorAvailability(String date, ArrayList<String> time) {
        this.date = date;
        this.time = time;
    }

    public DoctorAvailability() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getTime() {
        return time;
    }

    public void setTime(ArrayList<String> time) {
        this.time = time;
    }
}
