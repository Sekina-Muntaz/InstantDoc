package com.instant.doctor.models;

import java.io.Serializable;

public class PatientInfo implements Serializable {
    private String name;
    private long date;
    private String gender;
    private String phoneNo;
    private String email;
    private String id;
    private String imageURL;

    public PatientInfo(String name, long date, String gender, String phoneNo, String email, String id,String imageURL) {
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.phoneNo = phoneNo;
        this.email = email;
        this.id = id;
        this.imageURL=imageURL;

    }

    public PatientInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
