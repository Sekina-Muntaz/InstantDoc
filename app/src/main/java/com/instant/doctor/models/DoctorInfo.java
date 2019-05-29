package com.instant.doctor.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorInfo implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phoneNo;
    private String specialization;
    private String serviceNo;
    private String imageURL;
    private Map<String, ArrayList<String>> availability;

    public DoctorInfo(String id, String name, String email, String phoneNo, String specialization, String serviceNo,String imageURL) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.specialization = specialization;
        this.serviceNo = serviceNo;
        this.imageURL=imageURL;

    }

    public DoctorInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }


    public String getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }

    public Map<String, ArrayList<String>> getAvailability() {
        return availability;
    }

    public void setAvailability(Map<String, ArrayList<String>> availability) {
        this.availability = availability;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageUrl) {
        this.imageURL = imageUrl;
    }

    @Override
    public String toString() {
        return "DoctorInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", specialization='" + specialization + '\'' +
                ", serviceNo='" + serviceNo + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", availability=" + availability +
                '}';
    }
}
